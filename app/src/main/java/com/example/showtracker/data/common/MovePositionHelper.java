package com.example.showtracker.data.common;

import androidx.annotation.*;

import java.util.*;

public abstract class MovePositionHelper<T extends ListItem> {

    private T toMove;
    private T target;
    private final int oldPosition;
    private final int newPosition;
    private int positionDifference;
    private List<T> itemsToMove = null;

    public MovePositionHelper(T toMove, T target) {
        this.toMove = toMove;
        this.target = target;
        oldPosition = toMove.getPosition();
        newPosition = target.getPosition();
        positionDifference = newPosition - oldPosition;
    }

    /**
     * items to move should be a range of all the items that will need their position incremented or
     * decremented when dragging and dropping. This range should not include the item that is being
     * dragged.
     **/
    public abstract List<T> findItemsToMove(int startOfRange, int endOfRange);

    /**
     * returns null when positions are identical
     * */
    @Nullable
    public List<T> getItemsWithAdjustedPositions() {
        // if position difference is 0 that means it's the same item and no changes should be made
        if (positionDifference == 1 || positionDifference == -1) {
            // if difference is 1 or -1 the items are adjacent and only need to swap position
            swapAdjacentPositions();
        } else if (positionDifference > 1) {
            // if difference is positive the item needs to move up towards the end of the array
            // and all other items in between it and the target need to move 1 back
            itemsToMove = findItemsToMove(oldPosition + 1, newPosition);
            for (T item : itemsToMove) {
                item.setPosition(item.getPosition() - 1);
            }
        } else if (positionDifference < -1) {
            // if difference is negative the item needs to move back towards the beginning of the
            // array and all items in between it and the target need to move 1 forward
            itemsToMove = findItemsToMove(newPosition, oldPosition - 1);
            for (T item : itemsToMove) {
                item.setPosition(item.getPosition() + 1);
            }
        }

        if (itemsToMove != null) {
            // move item to target position and add to the final array list
            toMove.setPosition(newPosition);
            itemsToMove.add(toMove);
        }
        return itemsToMove;
    }

    private void swapAdjacentPositions() {
        target.setPosition(oldPosition);
        itemsToMove = new ArrayList<>();
        itemsToMove.add(target);
    }
}
