/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package com.radolyn.ayugram.ui;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.radolyn.ayugram.database.entities.EditedMessage;
import com.radolyn.ayugram.proprietary.AyuMessageUtils;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.BulletinFactory;

public class AyuMessageCell extends ChatMessageCell {
    private EditedMessage editedMessage;

    public AyuMessageCell(Context context, Activity activity, BaseFragment fragment) {
        super(context);

        setFullyDraw(true);
        isChat = false;
        setDelegate(new ChatMessageCell.ChatMessageCellDelegate() {
        });

        setOnClickListener(v -> {
            // copy only if no media
            if (TextUtils.isEmpty(editedMessage.mediaPath)) {
                copyText(fragment);
            }

            // ..open media otherwise
            if (!TextUtils.isEmpty(editedMessage.mediaPath)) {
                AndroidUtilities.openForView(getMessageObject(), activity, null);
            }
        });

        setOnLongClickListener(v -> {
            copyText(fragment);
            return true;
        });
    }

    public void setEditedMessage(EditedMessage editedMessage) {
        this.editedMessage = editedMessage;
    }

    private void copyText(BaseFragment fragment) {
        if (!TextUtils.isEmpty(editedMessage.text)) {
            var unhtmlified = AyuMessageUtils.unhtmlify(editedMessage.text);

            AndroidUtilities.addToClipboard(unhtmlified.first);
            BulletinFactory.of(fragment).createCopyBulletin(LocaleController.getString("MessageCopied", R.string.MessageCopied)).show();
        }
    }
}
