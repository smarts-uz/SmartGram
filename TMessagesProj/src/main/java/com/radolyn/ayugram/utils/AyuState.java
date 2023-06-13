/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package com.radolyn.ayugram.utils;

public class AyuState {
    private static final Object automaticallyScheduledSync = new Object();
    private static final Object allowReadPacketSync = new Object();
    private static boolean automaticallyScheduled;
    private static boolean allowReadPacket;

    public static boolean isAutomaticallyScheduled() {
        synchronized (automaticallyScheduledSync) {
            return automaticallyScheduled;
        }
    }

    public static void resetAutomaticallyScheduled() {
        synchronized (automaticallyScheduledSync) {
            automaticallyScheduled = false;
        }
    }

    public static void setAutomaticallyScheduled() {
        synchronized (automaticallyScheduledSync) {
            automaticallyScheduled = true;
        }
    }

    public static boolean isAllowReadPacket() {
        synchronized (allowReadPacketSync) {
            return allowReadPacket;
        }
    }

    public static void resetAllowReadPacket() {
        synchronized (allowReadPacketSync) {
            allowReadPacket = false;
        }
    }

    public static void setAllowReadPacket() {
        synchronized (allowReadPacketSync) {
            allowReadPacket = true;
        }
    }

    public static void reset() {
        automaticallyScheduled = false;
        allowReadPacket = false;
    }
}
