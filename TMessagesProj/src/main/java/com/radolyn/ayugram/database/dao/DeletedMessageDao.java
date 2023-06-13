/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package com.radolyn.ayugram.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.radolyn.ayugram.database.entities.DeletedMessage;
import com.radolyn.ayugram.database.entities.DeletedMessageFull;
import com.radolyn.ayugram.database.entities.DeletedMessageReaction;

import java.util.List;

@Dao
public interface DeletedMessageDao {
    @Query("SELECT EXISTS(SELECT * FROM deletedmessage WHERE userId = :userId AND dialogId = :dialogId AND messageId = :messageId)")
    boolean isDeleted(long userId, long dialogId, long messageId);

    @Query("SELECT EXISTS(SELECT * FROM deletedmessage WHERE userId = :userId AND dialogId = :dialogId AND messageId in (:messageIds))")
    boolean isDeleted(long userId, long dialogId, List<Integer> messageIds);

    @Transaction
    @Query("SELECT * FROM deletedmessage WHERE userId = :userId AND dialogId = :dialogId AND messageId = :messageId")
    DeletedMessageFull getMessage(long userId, long dialogId, int messageId);

    @Transaction
    @Query("SELECT * FROM deletedmessage WHERE userId = :userId AND dialogId = :dialogId AND topicId = :topicId AND :startDate <= date <= :endDate ORDER BY date LIMIT :limit")
    List<DeletedMessageFull> getMessages(long userId, long dialogId, long topicId, int startDate, int endDate, int limit);

    @Transaction
    @Query("SELECT * FROM deletedmessage WHERE userId = :userId AND dialogId = :dialogId AND groupedId = :groupedId")
    List<DeletedMessageFull> getMessagesGrouped(long userId, long dialogId, long groupedId);

    @Insert
    long insert(DeletedMessage msg);

    @Insert
    void insertReaction(DeletedMessageReaction reaction);

    @Query("DELETE FROM deletedmessage")
    void cleanTable();
}
