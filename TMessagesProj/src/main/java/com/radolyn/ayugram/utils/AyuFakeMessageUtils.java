/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package com.radolyn.ayugram.utils;

import android.text.TextUtils;

import com.google.android.exoplayer2.util.Log;

import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLRPC;

import java.io.File;
import java.nio.ByteBuffer;

public class AyuFakeMessageUtils {
    public static void fillMedia(TLRPC.TL_message message, String mediaPath, int documentType, int date) {
        fillMedia(message, mediaPath, documentType, date, null);
    }

    public static void fillMedia(TLRPC.TL_message message, String mediaPath, int documentType, int date, byte[] documentData) {
        if (documentType == 0 || (documentType != 2 && TextUtils.isEmpty(mediaPath))) {
            return;
        }

        if (documentType == 2) {
            try {
                var data = new NativeByteBuffer(documentData.length);
                data.put(ByteBuffer.wrap(documentData));
                data.reuse();
                data.rewind();
                message.media = TLRPC.TL_messageMediaDocument.TLdeserialize(data, data.readInt32(false), false);
            } catch (Exception e) {
                Log.e("AyuGram", "fake news sticker..");
            }

            message.stickerVerified = 1;
            return;
        }

        message.attachPath = mediaPath;
        var file = new File(mediaPath);

        if (documentType == 1) {
            var name = file.getName();
            var start = name.indexOf("#") + 1;
            var end = name.indexOf("@");
            var size = name.substring(start, end).split("x");
            var w = Integer.parseInt(size[0]);
            var h = Integer.parseInt(size[1]);

            message.media = new TLRPC.TL_messageMediaPhoto();
            message.media.flags = 1;
            message.media.photo = new TLRPC.TL_photo();
            message.media.photo.has_stickers = false;
            message.media.photo.date = date;

            TLRPC.TL_photoSize photoSize = new TLRPC.TL_photoSize();
            photoSize.size = (int) file.length();
            photoSize.w = w;
            photoSize.h = h;
            photoSize.type = "y";
            photoSize.location = new AyuFileLocation(mediaPath);
            message.media.photo.sizes.add(photoSize);
        } else if (documentType == 3) {
            message.media = new TLRPC.TL_messageMediaDocument();
            message.media.flags |= 1;

            message.media.document = new TLRPC.TL_document();
            message.media.document.date = date;
            message.media.document.localPath = mediaPath;
            message.media.document.file_name = file.getName();
            message.media.document.size = file.length();
        }
    }
}
