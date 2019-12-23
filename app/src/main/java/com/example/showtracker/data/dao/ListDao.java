package com.example.showtracker.data.dao;

import androidx.lifecycle.*;
import androidx.room.*;

import com.example.showtracker.data.entities.*;

import java.util.*;

@Dao
public abstract class ListDao {

    @Query("SELECT COUNT(*) FROM lists")
    abstract int getListCount();

    @Transaction
    @Query("SELECT * FROM lists ")
    public abstract LiveData<List<ListWithShows>> getAllListsWithShows();

    @Query(
        "SELECT * FROM lists "
        + "WHERE id NOT IN ("
        + "SELECT listId FROM lists_synced_to_all_shows"
        + ") "
        + "ORDER BY name"
    )
    public abstract LiveData<List<ListOfShows>> getAllEditableLists();

    @Query("SELECT * FROM lists WHERE id IN (:ids)")
    public abstract ListOfShows findById(String... ids);

//    @Query(
//        "SELECT lists.* FROM lists "
//        + "INNER JOIN list_show_join "
//        + "ON lists.id = list_show_join.listId "
//        + "WHERE list_show_join.showId = :showId"
//    )
//    public abstract LiveData<List<ListOfShows>> findListsByShowId(String showId);

    @Query("SELECT * FROM lists "
           + "WHERE position >= :startPosition "
           + "AND position <= :endPosition")
    abstract List<ListOfShows> findListsInPositionRange(int startPosition, int endPosition);

    @Transaction
    public void moveListPostion(ListOfShows listToMove, ListOfShows listInDesiredPosition) {
        int oldPosition = listToMove.position;
        int newPosition = listInDesiredPosition.position;
        int positionDifference = newPosition - oldPosition;
        List<ListOfShows> listsToMove = null;

        // if position difference is 0 that means it's the same list and no changes should be made
        if (positionDifference == 1 || positionDifference == -1) {
            listInDesiredPosition.position = oldPosition;
            listsToMove = new ArrayList<>();
            listsToMove.add(listInDesiredPosition);
        } else if (positionDifference > 1) {
            listsToMove = findListsInPositionRange(oldPosition + 1, newPosition);
            for (ListOfShows list : listsToMove) {
                list.position--;
            }
        } else if (positionDifference < -1) {
            listsToMove = findListsInPositionRange(newPosition, oldPosition - 1);
            for (ListOfShows list : listsToMove) {
                list.position++;
            }
        }

        if (listsToMove != null) {
            listToMove.position = newPosition;
            listsToMove.add(listToMove);
            update(listsToMove);
        }
    }

    @Transaction
    public long addList(ListOfShows list) {
        list.position = getListCount() + 1;
        return add(list);
    }

    @Insert
    abstract long add(ListOfShows list);

    @Update
    public abstract void update(ListOfShows... list);

    @Update
    public abstract void update(List<ListOfShows> lists);

    @Query("SELECT * FROM list_show_join")
    public abstract List<ListShowJoin> getAllListShowJoins();

    /**
     * the listIds are lists about to be deleted, the shows in the list have to be deleted first,
     * afterwards the call to delete lists will be made(both calls are done in a @Transaction)
     *
     * in this call: delete all shows joined to the given listIds
     * AND are not also joined to a list that is not about to be deleted.
     */
    @Query(
        "DELETE FROM shows "
        + "WHERE id IN ("
        + "SELECT DISTINCT showId "
        + "FROM list_show_join j1 "
        + "WHERE j1.listId IN (:listIds)"
        + "AND NOT EXISTS ("
        + "SELECT * FROM list_show_join j2 "
        + "WHERE j1.showId = j2.showId "
        + "AND j1.listId <> j2.listId "
        + "AND j2.listId NOT IN (:listIds)"
        + ")"
        + ")"
    )
    public abstract void deleteShowsByListIds(String... listIds);

    @Query(
        "DELETE FROM lists "
        + "WHERE id IN (:listIds)"
    )
    public abstract void deleteListsById(String... listIds);

    /**
     * When deleting a list, shows within that list should also be deleted.
     * However, in this app the user is able to assign multiple lists to a TV show so
     * you don't want to delete a show that is also contained in another list.
     * This is the reason that a simple delete query won't do in this case
     */
    @Transaction
    public void deleteListsWithShows(String... listIds) {
        deleteShowsByListIds(listIds);
        deleteListsById(listIds);
    }


    @Query("DELETE FROM lists")
    public abstract void deleteAll();
}
