package com.example.showtracker.data.tags;

import androidx.lifecycle.*;
import androidx.room.*;

import com.example.showtracker.data.common.joins.*;
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
        int oldPosition = tagToMove.position;
        int newPosition = target.position;
        int positionDifference = newPosition - oldPosition;
        List<Tag> tagsToMove = null;

        // TODO create helper class for moving position in database and implement in all DAOs

        // if position difference is 0 that means it's the same tag and no changes should be made
        if (positionDifference == 1 || positionDifference == -1) {
            // if difference is 1 or -1 the tags are adjacent and only need to swap position
            target.position = oldPosition;
            tagsToMove = new ArrayList<>();
            tagsToMove.add(target);
        } else if (positionDifference > 1) {
            // if difference is positive the tag needs to move up towards the end of the array
            // and all other tags in between it and the target need to move 1 back
            tagsToMove = findTagsInPositionRange(oldPosition + 1, newPosition);
            for (Tag tag : tagsToMove) {
                tag.position--;
            }
        } else if (positionDifference < -1) {
            // if difference is negative the tag needs to move back towards the beginning of the
            // array and all tags in between it and the target need to move 1 forward
            tagsToMove = findTagsInPositionRange(newPosition, oldPosition - 1);
            for (Tag tag : tagsToMove) {
                tag.position++;
            }
        }

        if (tagsToMove != null) {
            tagToMove.position = newPosition;
            tagsToMove.add(tagToMove);
            update(tagsToMove);
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
