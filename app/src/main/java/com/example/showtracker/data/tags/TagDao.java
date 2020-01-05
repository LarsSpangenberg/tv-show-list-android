package com.example.showtracker.data.tags;

import androidx.lifecycle.*;
import androidx.room.*;

import com.example.showtracker.data.common.joins.*;
import com.example.showtracker.data.common.utils.*;
import com.example.showtracker.data.tags.entities.*;

import java.util.*;

@Dao
public abstract class TagDao {
    @Query("SELECT * FROM tags")
    public abstract LiveData<List<Tag>> getAll();

    @Query("SELECT COUNT(*) FROM tags")
    abstract int getTagCount();

    @Query("SELECT * FROM tags "
           + "WHERE position >= :startPosition "
           + "AND position <= :endPosition")
    abstract List<Tag> findTagsInPositionRange(int startPosition, int endPosition);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract void addTagsToShow(List<ShowTagJoin> showTagJoins);

    @Query("DELETE FROM show_tag_join "
           + "WHERE showId = :showId "
           + "AND tagId NOT IN (:tagIds)")
    abstract void deleteUnselectedShowTagJoins(String showId, String... tagIds);

    @Transaction
    public void saveShowsTags(String showId, String... tagIds) {
        List<ShowTagJoin> showsTags = new ArrayList<>();
        for (String tagId : tagIds) {
            showsTags.add(new ShowTagJoin(showId, tagId));
        }
        addTagsToShow(showsTags);
        deleteUnselectedShowTagJoins(showId, tagIds);
    }

    @Transaction
    public void moveTagPosition(Tag tagToMove, Tag target) {
        List<Tag> itemsToMove = new DragAndDropPositionAdjuster<Tag>() {
            @Override
            public List<Tag> findItemsToMove(int startOfRange, int endOfRange) {
                return findTagsInPositionRange(startOfRange, endOfRange);
            }
        }.adjustPositions(tagToMove, target);

        if (itemsToMove != null) {
            update(itemsToMove);
        }
    }

    @Insert
    abstract void add(Tag tag);

    @Transaction
    public void addTag(Tag tag) {
        tag.position = getTagCount() + 1;
        add(tag);
    }

    @Update
    public abstract void update(Tag... tag);

    @Update
    public abstract void update(List<Tag> tag);

    @Delete
    public abstract void delete(Tag... tag);

    @Query("DELETE FROM tags WHERE id IN (:tagIds)")
    public abstract void delete(String... tagIds);

    @Query("DELETE FROM tags")
    public abstract void deleteAll();

}
