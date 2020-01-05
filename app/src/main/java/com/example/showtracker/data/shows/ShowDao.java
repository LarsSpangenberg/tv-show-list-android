package com.example.showtracker.data.shows;

import androidx.lifecycle.*;
import androidx.room.*;

import com.example.showtracker.data.common.joins.*;
import com.example.showtracker.data.common.utils.*;
import com.example.showtracker.data.shows.entities.*;

import java.util.*;

@Dao
public abstract class ShowDao {

    @Query("SELECT COUNT(*) FROM shows")
    abstract int getShowCount();

    @Query("SELECT id FROM shows")
    public abstract List<String> getAllShowIds();

    @Transaction
    @Query(
        "SELECT shows.* "
        + "FROM shows "
        + "INNER JOIN list_show_join "
        + "ON shows.id = list_show_join.showId "
        + "WHERE list_show_join.listId = :listId "
    )
    public abstract LiveData<List<ShowWithTags>> getShowsInList(String listId);

    @Transaction
    @Query(
        "SELECT shows.* "
        + "FROM shows "
        + "INNER JOIN list_show_join "
        + "ON shows.id = list_show_join.showId "
        + "INNER JOIN show_tag_join "
        + "ON shows.id = show_tag_join.showId "
        + "WHERE list_show_join.listId = :listId "
        + "AND shows.status IN (:statusFilter) "
        + "AND show_tag_join.tagId IN (:tagFilter)"
    )
    public abstract LiveData<List<ShowWithTags>> getShowsInListFiltered(
        String listId,
        List<Integer> statusFilter,
        List<String> tagFilter
    );

    @Transaction
    @Query(
        "SELECT shows.* "
        + "FROM shows "
        + "INNER JOIN list_show_join "
        + "ON shows.id = list_show_join.showId "
        + "WHERE list_show_join.listId = :listId "
        + "AND shows.status IN (:statusFilter)"
    )
    public abstract LiveData<List<ShowWithTags>> getShowsInListByStatus(
        String listId,
        List<Integer> statusFilter
    );

    @Transaction
    @Query(
        "SELECT shows.* "
        + "FROM shows "
        + "INNER JOIN list_show_join "
        + "ON shows.id = list_show_join.showId "
        + "INNER JOIN show_tag_join "
        + "ON shows.id = show_tag_join.showId "
        + "WHERE list_show_join.listId = :listId "
        + "AND show_tag_join.tagId IN (:tagFilter)"
    )
    public abstract LiveData<List<ShowWithTags>> getShowsInListByTag(
        String listId,
        List<String> tagFilter
    );

    @Transaction
    @Query("SELECT * FROM shows WHERE id = :showId")
    public abstract LiveData<ShowDetails> getShowDetails(String showId);

    @Query("SELECT * FROM shows WHERE id = :id")
    public abstract Show findById(String id);

    @Query("SELECT * FROM shows "
           + "WHERE position >= :startPosition "
           + "AND position <= :endPosition")
    abstract List<Show> findShowsInPositionRange(int startPosition, int endPosition);

    @Query(
        "SELECT DISTINCT showId "
        + "FROM list_show_join j1 "
        + "WHERE NOT EXISTS ("
        + "SELECT * FROM list_show_join j2 "
        + "WHERE j1.showId = j2.showId "
        + "AND j1.listId <> j2.listId"
        + ")"
    )
    abstract List<String> findShowsInSingleList();

    @Query("SELECT * FROM lists_synced_to_all_shows")
    public abstract List<String> findListsSyncedToAllShows();

    @Query("DELETE FROM lists_synced_to_all_shows WHERE listId = :listId")
    abstract void unsyncListFromAllShows(String listId);

    @Query("INSERT INTO lists_synced_to_all_shows (listId) VALUES(:listId)")
    abstract void syncListToAllShows(String listId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void addShowToList(ListShowJoin... listShowJoin);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract void addShowToList(List<ListShowJoin> listShowJoin);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract void addTagsToShow(List<ShowTagJoin> showTagJoins);

    @Query(
        "DELETE FROM list_show_join "
        + "WHERE showId = :showId "
        + "AND listId NOT IN (:listIds)"
    )
    abstract void deleteShowsListsNotInSelection(String showId, String... listIds);

    @Query(
        "DELETE FROM show_tag_join "
        + "WHERE showId = :showId "
        + "AND tagId NOT IN (:tagIds)"
    )
    abstract void deleteShowsTagsNotInSelection(String showId, String... tagIds);

    /**
     * Add an array of list ids in which this show should be shown, existing entries will be
     * ignored.
     * also delete join entries that exist for this show which are not mentioned in the
     * listIds array
     **/
    @Transaction
    public void updateShowsLists(String showId, String... listIds) {
        List<ListShowJoin> showsLists = new ArrayList<>();
        for (String listId : listIds) {
            showsLists.add(new ListShowJoin(listId, showId));
        }
        addShowToList(showsLists);
        deleteShowsListsNotInSelection(showId, listIds);
    }

    /**
     * Add an array of tags, existing entries for this show will be ignored.
     * also delete join entries in database that exist for this show which are not mentioned in the
     * tagIds array
     **/
    @Transaction
    public void updateShowsTags(String showId, String... tagIds) {
        List<ShowTagJoin> showsTags = new ArrayList<>();
        for (String tagId : tagIds) {
            showsTags.add(new ShowTagJoin(showId, tagId));
        }
        addTagsToShow(showsTags);
        deleteShowsTagsNotInSelection(showId, tagIds);
    }

    /**
     * Use this method to addTag a new show to db
     * <p>
     * All shows also automatically addTag to all the lists whose id is in the list_synced_to_all
     * table
     **/
    @Transaction
    public void addShowWithListId(Show show, String listId) {
        show.position = getShowCount() + 1;
        add(show);
        List<ListShowJoin> newJoins = new ArrayList<>();
        List<String> addTo = findListsSyncedToAllShows();
        addTo.add(listId);
        for (String list : addTo) {
            newJoins.add(new ListShowJoin(list, show.id));
        }
        addShowToList(newJoins);
    }

    /**
     * either sync a list to all shows by putting its id lists_synced_to_all_shows and joining every
     * show in the database to the list OR if it already exists in lists_synced_to_all_shows,
     * remove it from that table, effectively turning off the sync, and do nothing to all shows
     * already in the list.
     **/
    @Transaction
    public void handleAllShowsSyncToList(String listId) {
        List<String> listsSyncedToAllShows = findListsSyncedToAllShows();
        if (listsSyncedToAllShows.contains(listId)) {
            unsyncListFromAllShows(listId);
        } else {
            syncListToAllShows(listId);
            List<String> allShowIds = getAllShowIds();
            List<ListShowJoin> newJoins = new ArrayList<>();
            for (String showId : allShowIds) {
                newJoins.add(new ListShowJoin(listId, showId));
            }
            addShowToList(newJoins);
        }
    }

    @Transaction
    public void moveShowPosition(Show showToMove, Show target) {
        List<Show> itemsToMove = new DragAndDropPositionAdjuster<Show>() {
            @Override
            public List<Show> findItemsToMove(int startOfRange, int endOfRange) {
                return findShowsInPositionRange(startOfRange, endOfRange);
            }
        }.adjustPositions(showToMove, target);

        if (itemsToMove != null) {
            updateShow(itemsToMove);
        }
    }

    /**
     * if show does not exist in any other list, the show will be deleted from the database,
     * otherwise the show is only removed from this list while the other lists will still keep a
     * reference to this show
     **/
    @Transaction
    public void deleteShowFromList(String listId, String... showIds) {
        List<String> deletableShows = findShowsInSingleList();
        for (String id : showIds) {
            if (id != null) {
                if (deletableShows.contains(id)) {
                    delete(id);
                } else {
                    removeShowFromList(new ListShowJoin(listId, id));
                }
            }
        }
    }

    @Delete
    public abstract void removeShowFromList(ListShowJoin join);

    @Insert
    abstract void add(Show show);

    @Update
    public abstract void updateShow(Show show);

    @Update
    abstract void updateShow(List<Show> shows);

    @Delete
    public abstract void delete(Show show);

    @Query("DELETE FROM shows WHERE id IN (:showIds)")
    public abstract void delete(String... showIds);

    // --------- reset the shows table for testing -------------------------------------------------

    @Query("DELETE FROM shows")
    abstract void deleteAll();

    @Query("DELETE FROM list_show_join")
    abstract void deleteAllListShowJoins();

    @Query("DELETE FROM show_tag_join")
    abstract void deleteAllShowTagJoins();

    @Transaction
    public void deleteAllShowRelated() {
        deleteAllShowTagJoins();
        deleteAllListShowJoins();
        deleteAll();
    }
}
