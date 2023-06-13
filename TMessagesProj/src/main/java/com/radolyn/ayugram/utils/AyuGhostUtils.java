/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package com.radolyn.ayugram.utils;

import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;

public class AyuGhostUtils {
    public static void markRead(MessagesController messagesController, ConnectionsManager connectionsManager, MessageObject message) {
        TLRPC.TL_messages_readHistory request = new TLRPC.TL_messages_readHistory();
        request.peer = messagesController.getInputPeer(message.messageOwner.peer_id);
        request.max_id = message.getId();

        AyuState.setAllowReadPacket();
        connectionsManager.sendRequest(request, (response, error) -> {
        });
    }
}
