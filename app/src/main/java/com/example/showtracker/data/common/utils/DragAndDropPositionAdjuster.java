package com.example.showtracker.data.common.utils;

import androidx.annotation.*;

import com.example.showtracker.data.common.interfaces.*;

import java.util.*;

public abstract class DragAndDropPositionAdjuster<T extends ListItem> {
    private List<T> itemsToMove = null;

    /**
     * when this class is instantiated this method should be delegated to the DAO and simply return
     * every item within the range of the given start position and end position.
     *
     * It is assumed these items already have a position field with the correct value in order.
     **/
    public abstract List<T> findItemsToMove(int startOfRange, int endOfRange);

    /**
     * adjusts the position of the dragged item to the target position and increments or decrements
     * every other item within the range of the target and the old position as appropriate.
     *
     * returns null when positions are identical
     * */
    @Nullable
    public List<T> adjustPositions(T toMove, T target) {
        int oldPosition = toMove.getPosition();
        int newPosition = target.getPosition();
        int positionDifference = newPosition - oldPosition;

        // if position difference is 0 that means it's the same item and no changes should be made
        if (positionDifference == 1 || positionDifference == -1) {
            // if difference is 1 or -1 the items are adjacent and only need to swap position
            target.setPosition(oldPosition);
            itemsToMove = new ArrayList<>();
            itemsToMove.add(target);
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
}
