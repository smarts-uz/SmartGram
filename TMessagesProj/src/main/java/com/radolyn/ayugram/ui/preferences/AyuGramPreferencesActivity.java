/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package com.radolyn.ayugram.ui.preferences;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.exteragram.messenger.preferences.BasePreferencesActivity;
import com.radolyn.ayugram.AyuConfig;
import com.radolyn.ayugram.AyuConstants;
import com.radolyn.ayugram.messages.AyuMessagesController;
import com.radolyn.ayugram.utils.AyuState;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.EditTextSettingsCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Components.BulletinFactory;

public class AyuGramPreferencesActivity extends BasePreferencesActivity {
    private int ghostEssentialsHeaderRow;
    private int sendReadPacketsRow;
    private int sendOnlinePacketsRow;
    private int sendUploadProgressRow;
    private int sendOfflinePacketAfterOnlineRow;
    private int markReadAfterSendRow;
    private int useScheduledMessagesRow;
    private int ghostDividerRow;

    private int spyHeaderRow;
    private int keepDeletedMessagesRow;
    private int keepMessagesHistoryRow;
    private int spyDividerRow;

    private int qolHeaderRow;
    private int showFromChannelRow;
    private int keepAliveServiceRow;
    private int enableAdsRow;
    private int qolDividerRow;

    private int customizationHeaderRow;
    private int deletedMarkTextRow;
    private int editedMarkTextRow;
    private int showGhostToggleInDrawerRow;
    private int showKillButtonInDrawerRow;
    private int customizationDividerRow;

    private int debugHeaderRow;
    private int WALModeRow;
    private int cleanDatabaseBtnRow;

    @Override
    protected void updateRowsId() {
        super.updateRowsId();

        ghostEssentialsHeaderRow = newRow();
        sendReadPacketsRow = newRow();
        sendOnlinePacketsRow = newRow();
        sendUploadProgressRow = newRow();
        sendOfflinePacketAfterOnlineRow = newRow();
        markReadAfterSendRow = newRow();
        useScheduledMessagesRow = newRow();
        ghostDividerRow = newRow();

        spyHeaderRow = newRow();
        keepDeletedMessagesRow = newRow();
        keepMessagesHistoryRow = newRow();
        spyDividerRow = newRow();

        qolHeaderRow = newRow();
        showFromChannelRow = newRow();
        keepAliveServiceRow = newRow();
        enableAdsRow = newRow();
        qolDividerRow = newRow();

        customizationHeaderRow = newRow();
        deletedMarkTextRow = newRow();
        editedMarkTextRow = newRow();
        showGhostToggleInDrawerRow = newRow();
        showKillButtonInDrawerRow = newRow();
        customizationDividerRow = newRow();

        debugHeaderRow = newRow();
        WALModeRow = newRow();
        cleanDatabaseBtnRow = newRow();
    }

    @Override
    protected void onItemClick(View view, int position, float x, float y) {
        if (position == sendReadPacketsRow) {
            AyuConfig.editor.putBoolean("sendReadPackets", AyuConfig.sendReadPackets ^= true).apply();
            ((TextCheckCell) view).setChecked(AyuConfig.sendReadPackets);

            AyuState.resetAllowReadPacket();
        } else if (position == sendOnlinePacketsRow) {
            AyuConfig.editor.putBoolean("sendOnlinePackets", AyuConfig.sendOnlinePackets ^= true).apply();
            ((TextCheckCell) view).setChecked(AyuConfig.sendOnlinePackets);
        } else if (position == sendUploadProgressRow) {
            AyuConfig.editor.putBoolean("sendUploadProgress", AyuConfig.sendUploadProgress ^= true).apply();
            ((TextCheckCell) view).setChecked(AyuConfig.sendUploadProgress);
        } else if (position == sendOfflinePacketAfterOnlineRow) {
            AyuConfig.editor.putBoolean("sendOfflinePacketAfterOnline", AyuConfig.sendOfflinePacketAfterOnline ^= true).apply();
            ((TextCheckCell) view).setChecked(AyuConfig.sendOfflinePacketAfterOnline);
        } else if (position == markReadAfterSendRow) {
            AyuConfig.editor.putBoolean("markReadAfterSend", AyuConfig.markReadAfterSend ^= true).apply();
            ((TextCheckCell) view).setChecked(AyuConfig.markReadAfterSend);

            AyuState.resetAllowReadPacket();
        } else if (position == useScheduledMessagesRow) {
            AyuConfig.editor.putBoolean("useScheduledMessages", AyuConfig.useScheduledMessages ^= true).apply();
            ((TextCheckCell) view).setChecked(AyuConfig.useScheduledMessages);

            AyuState.resetAutomaticallyScheduled();
        } else if (position == keepDeletedMessagesRow) {
            AyuConfig.editor.putBoolean("keepDeletedMessages", AyuConfig.keepDeletedMessages ^= true).apply();
            ((TextCheckCell) view).setChecked(AyuConfig.keepDeletedMessages);
        } else if (position == keepMessagesHistoryRow) {
            AyuConfig.editor.putBoolean("keepMessagesHistory", AyuConfig.keepMessagesHistory ^= true).apply();
            ((TextCheckCell) view).setChecked(AyuConfig.keepMessagesHistory);
        } else if (position == showFromChannelRow) {
            AyuConfig.editor.putBoolean("showFromChannel", AyuConfig.showFromChannel ^= true).apply();
            ((TextCheckCell) view).setChecked(AyuConfig.showFromChannel);
        } else if (position == keepAliveServiceRow) {
            AyuConfig.editor.putBoolean("keepAliveService", AyuConfig.keepAliveService ^= true).apply();
            ((TextCheckCell) view).setChecked(AyuConfig.keepAliveService);
        } else if (position == enableAdsRow) {
            AyuConfig.editor.putBoolean("enableAds", AyuConfig.enableAds ^= true).apply();
            ((TextCheckCell) view).setChecked(AyuConfig.enableAds);
        } else if (position == showGhostToggleInDrawerRow) {
            AyuConfig.editor.putBoolean("showGhostToggleInDrawer", AyuConfig.showGhostToggleInDrawer ^= true).apply();
            ((TextCheckCell) view).setChecked(AyuConfig.showGhostToggleInDrawer);

            NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.mainUserInfoChanged);
        } else if (position == showKillButtonInDrawerRow) {
            AyuConfig.editor.putBoolean("showKillButtonInDrawer", AyuConfig.showKillButtonInDrawer ^= true).apply();
            ((TextCheckCell) view).setChecked(AyuConfig.showKillButtonInDrawer);

            NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.mainUserInfoChanged);
        } else if (position == deletedMarkTextRow) {
            var builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("DeletedMarkText", R.string.DeletedMarkText));
            var layout = new LinearLayout(getParentActivity());
            var input = new EditTextSettingsCell(getParentActivity());
            input.setText(AyuConfig.getDeletedMark(), true);

            layout.setGravity(LinearLayout.VERTICAL);
            layout.addView(input);
            builder.setView(layout);
            builder.setPositiveButton(LocaleController.getString("Save", R.string.Save), (dialog, which) -> {
                AyuConfig.editor.putString("deletedMarkText", input.getText()).apply();
                ((TextCell) view).setTextAndValue(LocaleController.getString("DeletedMarkText", R.string.DeletedMarkText), AyuConfig.getDeletedMark(), true);
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (dialog, which) -> dialog.cancel());
            builder.setNeutralButton(LocaleController.getString("Reset", R.string.Reset), (dialog, which) -> {
                AyuConfig.editor.putString("deletedMarkText", "🧹").apply();
                ((TextCell) view).setTextAndValue(LocaleController.getString("DeletedMarkText", R.string.DeletedMarkText), AyuConfig.getDeletedMark(), true);
            });

            builder.show();
        } else if (position == editedMarkTextRow) {
            var builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("EditedMarkText", R.string.EditedMarkText));
            var layout = new LinearLayout(getParentActivity());
            var input = new EditTextSettingsCell(getParentActivity());
            input.setText(AyuConfig.getEditedMark(), true);

            layout.setGravity(LinearLayout.VERTICAL);
            layout.addView(input);
            builder.setView(layout);
            builder.setPositiveButton(LocaleController.getString("Save", R.string.Save), (dialog, which) -> {
                AyuConfig.editor.putString("editedMarkText", input.getText()).apply();
                ((TextCell) view).setTextAndValue(LocaleController.getString("EditedMarkText", R.string.EditedMarkText), AyuConfig.getEditedMark(), true);
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (dialog, which) -> dialog.cancel());
            builder.setNeutralButton(LocaleController.getString("Reset", R.string.Reset), (dialog, which) -> {
                AyuConfig.editor.putString("editedMarkText", LocaleController.getString("EditedMessage", R.string.EditedMessage)).apply();
                ((TextCell) view).setTextAndValue(LocaleController.getString("EditedMarkText", R.string.EditedMarkText), AyuConfig.getEditedMark(), true);
            });

            builder.show();
        } else if (position == WALModeRow) {
            AyuConfig.editor.putBoolean("WALMode", AyuConfig.WALMode ^= true).apply();
            ((TextCheckCell) view).setChecked(AyuConfig.WALMode);
        } else if (position == cleanDatabaseBtnRow) {
            AyuMessagesController.getInstance().clean();
            AyuState.reset();

            ((TextCell) view).setValue("…");

            BulletinFactory.of(this).createSimpleBulletin(R.raw.info, LocaleController.getString("CleanDatabaseNotification", R.string.CleanDatabaseNotification)).show();
        }
    }

    @Override
    protected String getTitle() {
        return LocaleController.getString("AyuPreferences", R.string.AyuPreferences);
    }

    @Override
    protected BaseListAdapter createAdapter(Context context) {
        return new ListAdapter(context);
    }

    private class ListAdapter extends BaseListAdapter {

        public ListAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, boolean payload) {
            switch (holder.getItemViewType()) {
                case 1:
                    holder.itemView.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    break;
                case 2:
                    TextCell textCell = (TextCell) holder.itemView;
                    if (position == deletedMarkTextRow) {
                        textCell.setTextAndValue(LocaleController.getString("DeletedMarkText", R.string.DeletedMarkText), AyuConfig.getDeletedMark(), true);
                    } else if (position == editedMarkTextRow) {
                        textCell.setTextAndValue(LocaleController.getString("EditedMarkText", R.string.EditedMarkText), AyuConfig.getEditedMark(), true);
                    } else if (position == cleanDatabaseBtnRow) {
                        var file = ApplicationLoader.applicationContext.getDatabasePath(AyuConstants.AYU_DATABASE);
                        var size = file.exists() ? file.length() : 0;

                        textCell.setTextAndValueAndIcon(LocaleController.getString("CleanDatabase", R.string.CleanDatabase), AndroidUtilities.formatFileSize(size), R.drawable.msg_clearcache, false);
                        textCell.setColors(Theme.key_text_RedBold, Theme.key_text_RedBold);
                    }
                    break;
                case 3:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == ghostEssentialsHeaderRow) {
                        headerCell.setText(LocaleController.getString("GhostEssentialsHeader", R.string.GhostEssentialsHeader));
                    } else if (position == spyHeaderRow) {
                        headerCell.setText(LocaleController.getString("SpyEssentialsHeader", R.string.SpyEssentialsHeader));
                    } else if (position == qolHeaderRow) {
                        headerCell.setText(LocaleController.getString("QoLTogglesHeader", R.string.QoLTogglesHeader));
                    } else if (position == customizationHeaderRow) {
                        headerCell.setText(LocaleController.getString("CustomizationHeader", R.string.CustomizationHeader));
                    } else if (position == debugHeaderRow) {
                        headerCell.setText(LocaleController.getString("SettingsDebug", R.string.SettingsDebug));
                    }
                    break;
                case 5:
                    TextCheckCell textCheckCell = (TextCheckCell) holder.itemView;
                    textCheckCell.setEnabled(true, null);
                    if (position == sendReadPacketsRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("SendReadPackets", R.string.SendReadPackets), AyuConfig.sendReadPackets, true);
                    } else if (position == sendOnlinePacketsRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("SendOnlinePackets", R.string.SendOnlinePackets), AyuConfig.sendOnlinePackets, true);
                    } else if (position == sendUploadProgressRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("SendUploadProgress", R.string.SendUploadProgress), AyuConfig.sendUploadProgress, true);
                    } else if (position == sendOfflinePacketAfterOnlineRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("SendOfflinePacketAfterOnline", R.string.SendOfflinePacketAfterOnline), AyuConfig.sendOfflinePacketAfterOnline, true);
                    } else if (position == markReadAfterSendRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("MarkReadAfterSend", R.string.MarkReadAfterSend), AyuConfig.markReadAfterSend, true);
                    } else if (position == useScheduledMessagesRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("UseScheduledMessages", R.string.UseScheduledMessages) + " β", AyuConfig.useScheduledMessages, true);
                    } else if (position == keepDeletedMessagesRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("KeepDeletedMessages", R.string.KeepDeletedMessages), AyuConfig.keepDeletedMessages, true);
                    } else if (position == keepMessagesHistoryRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("KeepMessagesHistory", R.string.KeepMessagesHistory), AyuConfig.keepMessagesHistory, true);
                    } else if (position == showFromChannelRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("ShowFromChannel", R.string.ShowFromChannel), AyuConfig.showFromChannel, true);
                    } else if (position == keepAliveServiceRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("KeepAliveService", R.string.KeepAliveService) + " β", AyuConfig.keepAliveService, true);
                    } else if (position == enableAdsRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("EnableAds", R.string.EnableAds), AyuConfig.enableAds, true);
                    } else if (position == showGhostToggleInDrawerRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("ShowGhostToggleInDrawer", R.string.ShowGhostToggleInDrawer), AyuConfig.showGhostToggleInDrawer, true);
                    } else if (position == showKillButtonInDrawerRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("ShowKllButtonInDrawer", R.string.ShowKllButtonInDrawer), AyuConfig.showKillButtonInDrawer, true);
                    } else if (position == WALModeRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("WALMode", R.string.WALMode), AyuConfig.WALMode, true);
                    }
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (
                    position == ghostDividerRow ||
                            position == spyDividerRow ||
                            position == qolDividerRow ||
                            position == customizationDividerRow
            ) {
                return 1;
            } else if (position == deletedMarkTextRow || position == editedMarkTextRow || position == cleanDatabaseBtnRow) {
                return 2;
            } else if (
                    position == ghostEssentialsHeaderRow ||
                            position == spyHeaderRow ||
                            position == qolHeaderRow ||
                            position == customizationHeaderRow ||
                            position == debugHeaderRow
            ) {
                return 3;
            }
            return 5;
        }
    }
}
