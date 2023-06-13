/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package com.radolyn.ayugram.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.radolyn.ayugram.database.entities.EditedMessage;
import com.radolyn.ayugram.messages.AyuMessagesController;
import com.radolyn.ayugram.proprietary.AyuMessageUtils;
import com.radolyn.ayugram.utils.AyuFakeMessageUtils;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;
import java.util.List;

public class AyuMessageHistory extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private final List<EditedMessage> messages;
    private final int rowCount;
    private RecyclerListView listView;

    public AyuMessageHistory(long userId, MessageObject messageObject) {
        var messagesController = AyuMessagesController.getInstance();
        messages = messagesController.getRevisions(userId, messageObject.messageOwner.dialog_id, messageObject.messageOwner.id);
        rowCount = messages.size();
    }

    @Override
    public View createView(Context context) {
        var firstMsg = messages.get(0);
        var peer = getMessagesController().getUserOrChat(firstMsg.dialogId);
        // todo: check sender of the message

        String name;
        if (peer == null) {
            name = "?"; // wtf
        } else if (peer instanceof TLRPC.User) {
            name = ((TLRPC.User) peer).first_name;
        } else if (peer instanceof TLRPC.Chat) {
            name = ((TLRPC.Chat) peer).title;
        } else {
            name = LocaleController.getString("EditsHistoryTitle", R.string.EditsHistoryTitle);
        }

        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(name);
        actionBar.setSubtitle(String.valueOf(firstMsg.messageId));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        listView = new RecyclerListView(context);
        listView.setItemAnimator(null);
        listView.setLayoutAnimation(null);
        LinearLayoutManager layoutManager;
        listView.setLayoutManager(layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        listView.setVerticalScrollBarEnabled(true);
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        ListAdapter adapter;
        listView.setAdapter(adapter = new AyuMessageHistory.ListAdapter(context));

        return fragmentView;
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return new ArrayList<>();
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        // todo: update list in real time
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private final Context context;

        public ListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 1) {
                view = new AyuMessageCell(context, getParentActivity(), AyuMessageHistory.this);
            } else {
                view = null;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 1) {
                var ayuMessageDetailCell = (AyuMessageCell) holder.itemView;

                var editedMessage = messages.get(position);
                var msg = createMessageObject(editedMessage);

                ayuMessageDetailCell.setMessageObject(msg, null, false, false);
                ayuMessageDetailCell.setEditedMessage(editedMessage);
                ayuMessageDetailCell.setId(position);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position >= 0 && position < messages.size() ? 1 : 0;
        }

        private MessageObject createMessageObject(EditedMessage editedMessage) {
            var parseResult = AyuMessageUtils.unhtmlify(editedMessage.text);

            var text = parseResult.first;
            var entities = parseResult.second;

            // shamefully copied from Extera's sticker size preview
            var msg = new TLRPC.TL_message();
            msg.message = text;
            msg.entities = entities;
            msg.date = editedMessage.editedDate;
            msg.dialog_id = -1;
            msg.flags = 512;
            msg.id = Utilities.random.nextInt();
            msg.out = false;
            msg.peer_id = new TLRPC.TL_peerUser();
            msg.peer_id.user_id = 1;

            AyuFakeMessageUtils.fillMedia(msg, editedMessage.mediaPath, editedMessage.isDocument ? 3 : 1, editedMessage.editedDate);

            return new MessageObject(getCurrentAccount(), msg, true, true);
        }
    }
}