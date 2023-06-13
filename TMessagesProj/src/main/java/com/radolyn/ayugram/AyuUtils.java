/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package com.radolyn.ayugram;

import static org.telegram.messenger.Utilities.random;

import android.app.Activity;
import android.text.TextUtils;

import com.google.android.exoplayer2.util.Log;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.tgnet.TLRPC;

import java.io.File;

public class AyuUtils {
    private static final char[] chars = "abcdefghijklmnopqrstuvwxyz1234567890".toCharArray();

    public static boolean moveFile(File from, File to) {
        boolean success;
        try {
            success = from.renameTo(to);
        } catch (SecurityException e) {
            Log.d("AyuGram", e.toString());
            success = false;
        }

        return success;
    }

    public static String removeExtension(String filename) {
        int index = filename.lastIndexOf('.');
        return filename.substring(0, index);
    }

    public static String getExtension(String filename) {
        int index = filename.lastIndexOf('.');
        return filename.substring(index);
    }

    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    public static String getFilename(TLRPC.Message msg, File attachPathFile) {
        var filename = FileLoader.getMessageFileName(msg);
        if (TextUtils.isEmpty(filename)) {
            filename = attachPathFile.getName();
        }

        var f = AyuUtils.removeExtension(filename);

        if (msg.media instanceof TLRPC.TL_messageMediaPhoto) {
            var photoSize = FileLoader.getClosestPhotoSizeWithSize(msg.media.photo.sizes, AndroidUtilities.getPhotoSize());
            f += "#" + photoSize.w + "x" + photoSize.h;
        }

        f += "@" + AyuUtils.generateRandomString(6);
        f += AyuUtils.getExtension(filename);

        return f;
    }

    public static void killApplication(Activity activity) {
        activity.finishAndRemoveTask();
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
    }
}
