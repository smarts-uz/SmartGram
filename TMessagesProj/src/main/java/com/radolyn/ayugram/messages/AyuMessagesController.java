/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package com.radolyn.ayugram.messages;

import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.OptIn;
import androidx.room.ExperimentalRoomApi;
import androidx.room.Room;

import com.exteragram.messenger.ExteraConfig;
import com.google.android.exoplayer2.util.Log;
import com.radolyn.ayugram.AyuConfig;
import com.radolyn.ayugram.AyuConstants;
import com.radolyn.ayugram.AyuUtils;
import com.radolyn.ayugram.database.AyuDatabase;
import com.radolyn.ayugram.database.dao.DeletedMessageDao;
import com.radolyn.ayugram.database.dao.EditedMessageDao;
import com.radolyn.ayugram.database.entities.DeletedMessage;
import com.radolyn.ayugram.database.entities.DeletedMessageFull;
import com.radolyn.ayugram.database.entities.DeletedMessageReaction;
import com.radolyn.ayugram.database.entities.EditedMessage;
import com.radolyn.ayugram.proprietary.AyuMessageUtils;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLRPC;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AyuMessagesController {
    public static final String attachmentsSubfolder = "Saved Attachments";
    private static final File attachmentsPath = new File(
            new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), AyuConstants.APP_NAME),
            attachmentsSubfolder
    );
    private static AyuMessagesController instance;
    private final AyuDatabase database;
    private final EditedMessageDao editedMessageDao;
    private final DeletedMessageDao deletedMessageDao;

    private @OptIn(markerClass = ExperimentalRoomApi.class) AyuMessagesController() {
        // recreate for testing if debug
        if (ExteraConfig.getLogging()) {
            ApplicationLoader.applicationContext.deleteDatabase(AyuConstants.AYU_DATABASE);
            if (attachmentsPath.exists()) {
                attachmentsPath.delete();
            }
        }

        if (!attachmentsPath.exists()) {
            attachmentsPath.mkdirs();
            try {
                new File(attachmentsPath, ".nomedia").createNewFile();
            } catch (IOException e) {
                // ignored, I hate java
            }
        }

        database = Room.databaseBuilder(ApplicationLoader.applicationContext, AyuDatabase.class, AyuConstants.AYU_DATABASE)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .setAutoCloseTimeout(10, TimeUnit.MINUTES)
                .build();

        editedMessageDao = database.editedMessageDao();
        deletedMessageDao = database.deletedMessageDao();
    }

    public static AyuMessagesController getInstance() {
        if (instance == null) {
            instance = new AyuMessagesController();
        }
        return instance;
    }

    public void onMessageEdited(TLRPC.Message oldMessage, TLRPC.Message newMessage, long userId, int accountId, int currentTime) {
        if (!AyuConfig.keepMessagesHistory) {
            return;
        }

        onMessageEditedInner(oldMessage, newMessage, userId, accountId, currentTime);
    }

    private void onMessageEditedInner(TLRPC.Message oldMessage, TLRPC.Message newMessage, long userId, int accountId, int currentTime) {
        boolean sameMedia = false;
        boolean isDocument = false;
        if (oldMessage.media instanceof TLRPC.TL_messageMediaPhoto && newMessage.media instanceof TLRPC.TL_messageMediaPhoto && oldMessage.media.photo != null && newMessage.media.photo != null) {
            sameMedia = oldMessage.media.photo.id == newMessage.media.photo.id;
        } else if (oldMessage.media instanceof TLRPC.TL_messageMediaDocument && newMessage.media instanceof TLRPC.TL_messageMediaDocument && oldMessage.media.document != null && newMessage.media.document != null) {
            sameMedia = oldMessage.media.document.id == newMessage.media.document.id;
            isDocument = true;
        } else if (oldMessage.media instanceof TLRPC.TL_messageMediaWebPage && newMessage.media instanceof TLRPC.TL_messageMediaWebPage) {
            sameMedia = true;
        }

        if (sameMedia && TextUtils.equals(oldMessage.message, newMessage.message)) {
            return;
        }

        // reactions fix
        if (!sameMedia && newMessage.edit_hide) {
            return;
        }

        var revision = new EditedMessage();

        var attachPathFile = FileLoader.getInstance(accountId).getPathToMessage(oldMessage);

        if (!sameMedia && attachPathFile.exists()) {
            var f = AyuUtils.getFilename(oldMessage, attachPathFile);
            var dest = new File(attachmentsPath, f);

            // move file, because it's likely to be deleted by Telegram in a few seconds
            var success = AyuUtils.moveFile(attachPathFile, dest);

            if (success) {
                attachPathFile = new File(dest.getAbsolutePath());
            } else {
                attachPathFile = new File("/");
            }
        }

        var attachPath = attachPathFile.getAbsolutePath();

        revision.mediaPath = attachPath.equals("/") ? null : attachPath;
        revision.isDocument = isDocument;

        var dialogId = MessageObject.getDialogId(oldMessage);
        var messageId = newMessage.id;

        if (!sameMedia && !TextUtils.isEmpty(revision.mediaPath) && editedMessageDao.isFirstRevisionWithChangedMedia(userId, dialogId, messageId)) {
            // update previous revisions to reflect media change
            // like, there's no previous file, so replace it with one we copied before...
            editedMessageDao.updateAttachmentForRevisionsBeforeDate(userId, dialogId, messageId, attachPath, currentTime);
        }

        revision.userId = userId;
        revision.dialogId = dialogId;
        revision.messageId = messageId;
        revision.text = AyuMessageUtils.htmlify(oldMessage);
        revision.editedDate = currentTime;

        editedMessageDao.insert(revision);
    }

    public void onMessageDeleted(long userId, long dialogId, long topicId, int msgId, int accountId, int currentTime, TLRPC.Message msg) {
        if (!AyuConfig.keepDeletedMessages) {
            return;
        }

        onMessageDeletedInner(userId, dialogId, topicId, msgId, accountId, currentTime, msg);
    }

    private void onMessageDeletedInner(long userId, long dialogId, long topicId, int msgId, int accountId, int currentTime, TLRPC.Message msg) {
        var deletedMessage = new DeletedMessage();
        deletedMessage.userId = userId;
        deletedMessage.dialogId = dialogId;
        deletedMessage.messageId = msgId;
        deletedMessage.deletedDate = currentTime;

        if (msg != null) {
            deletedMessage.text = AyuMessageUtils.htmlify(msg);
            deletedMessage.date = msg.date;
            deletedMessage.flags = msg.flags;

            deletedMessage.peerId = MessageObject.getPeerId(msg.peer_id);
            deletedMessage.fromId = MessageObject.getPeerId(msg.from_id);
            deletedMessage.groupedId = msg.grouped_id;
            deletedMessage.topicId = topicId;

            deletedMessage.out = msg.out;
            deletedMessage.editDate = msg.edit_date;
            deletedMessage.editHide = msg.edit_hide;

            // --- media work

            if (msg.media == null) {
                deletedMessage.documentType = 0; // none
            } else if (msg.media instanceof TLRPC.TL_messageMediaPhoto && msg.media.photo != null) {
                deletedMessage.documentType = 1; // photo
            } else if (msg.media instanceof TLRPC.TL_messageMediaDocument && msg.media.document != null && (MessageObject.isStickerMessage(msg) || msg.media.document.mime_type.equals("application/x-tgsticker"))) {
                deletedMessage.documentType = 2; // sticker

                try {
                    NativeByteBuffer buffer = new NativeByteBuffer(msg.media.getObjectSize());
                    msg.media.serializeToStream(buffer);
                    buffer.reuse();
                    buffer.buffer.rewind();
                    byte[] arr = new byte[buffer.buffer.remaining()];
                    buffer.buffer.get(arr);

                    deletedMessage.documentSerialized = arr;
                } catch (Exception e) {
                    Log.e("AyuGram", "fake news sticker");
                }
            } else {
                deletedMessage.documentType = 3; // file
            }

            if (deletedMessage.documentType == 1 || deletedMessage.documentType == 3) {
                var attachPathFile = FileLoader.getInstance(accountId).getPathToMessage(msg);

                if (attachPathFile.exists()) {
                    var f = AyuUtils.getFilename(msg, attachPathFile);
                    var dest = new File(attachmentsPath, f);

                    // move file, because it's likely to be deleted by Telegram in a few seconds
                    var success = AyuUtils.moveFile(attachPathFile, dest);

                    if (success) {
                        attachPathFile = new File(dest.getAbsolutePath());
                    } else {
                        attachPathFile = new File("/");
                    }
                } else {
                    attachPathFile = new File("/");
                }

                var attachPath = attachPathFile.getAbsolutePath();

                deletedMessage.mediaPath = attachPath.equals("/") ? null : attachPath;
            }
        }

        var fakeMsgId = deletedMessageDao.insert(deletedMessage);

        if (msg != null && msg.reactions != null) {
            processDeletedReactions(fakeMsgId, msg.reactions);
        }
    }

    private void processDeletedReactions(long fakeMessageId, TLRPC.TL_messageReactions reactions) {
        for (var reaction : reactions.results) {
            if (reaction.reaction instanceof TLRPC.TL_reactionEmpty) {
                continue;
            }

            var deletedReaction = new DeletedMessageReaction();
            deletedReaction.deletedMessageId = fakeMessageId;
            deletedReaction.count = reaction.count;
            deletedReaction.selfSelected = reaction.chosen;

            if (reaction.reaction instanceof TLRPC.TL_reactionEmoji) {
                deletedReaction.emoticon = ((TLRPC.TL_reactionEmoji) reaction.reaction).emoticon;
            } else if (reaction.reaction instanceof TLRPC.TL_reactionCustomEmoji) {
                deletedReaction.documentId = ((TLRPC.TL_reactionCustomEmoji) reaction.reaction).document_id;
                deletedReaction.isCustom = true;
            } else {
                Log.e("AyuGram", "fake news emoji");
                continue;
            }

            deletedMessageDao.insertReaction(deletedReaction);
        }
    }

    public boolean hasAnyRevisions(long userId, long dialogId, int msgId) {
        return editedMessageDao.hasAnyRevisions(userId, dialogId, msgId);
    }

    public List<EditedMessage> getRevisions(long userId, long dialogId, int msgId) {
        return editedMessageDao.getAllRevisions(userId, dialogId, msgId);
    }

    public DeletedMessageFull getMessage(long userId, long dialogId, int messageId) {
        return deletedMessageDao.getMessage(userId, dialogId, messageId);
    }

    public List<DeletedMessageFull> getMessages(long userId, long dialogId, long topicId, int startDate, int endDate, int limit) {
        return deletedMessageDao.getMessages(userId, dialogId, topicId, startDate, endDate, limit);
    }

    public List<DeletedMessageFull> getMessagesGrouped(long userId, long dialogId, long groupedId) {
        return deletedMessageDao.getMessagesGrouped(userId, dialogId, groupedId);
    }

    public boolean isDeleted(long userId, long dialogId, int msgId) {
        return deletedMessageDao.isDeleted(userId, dialogId, msgId);
    }

    public boolean isDeleted(long userId, ArrayList<MessageObject> messageObjects) {
        var ids = messageObjects.stream().map(MessageObject::getId).collect(Collectors.toList());
        var dialogId = messageObjects.get(0).messageOwner.dialog_id;

        return deletedMessageDao.isDeleted(userId, dialogId, ids);
    }

    public void clean() {
        editedMessageDao.cleanTable();
        deletedMessageDao.cleanTable();

        database.close();

        ApplicationLoader.applicationContext.deleteDatabase(AyuConstants.AYU_DATABASE);

        // force recreate database to avoid crash
        instance = null;
    }
}
