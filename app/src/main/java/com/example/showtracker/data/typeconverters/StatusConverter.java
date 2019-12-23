package com.example.showtracker.data.typeconverters;

import androidx.room.*;

import com.example.showtracker.data.entities.*;

import static com.example.showtracker.data.entities.Show.Status.*;

public class StatusConverter {


    @TypeConverter
    public static Show.Status toStatus(int status) {
        if (status == CURRENT.getCode()) return CURRENT;
        else if (status == COMPLETED.getCode()) return COMPLETED;
        else if (status == PLAN_TO_WATCH.getCode()) return PLAN_TO_WATCH;
        else if (status == DROPPED.getCode()) return DROPPED;
        else if (status == ON_HOLD.getCode()) return ON_HOLD;
        else throw new IllegalArgumentException("Could not recognize status");

    }

    @TypeConverter
    public static int toInteger(Show.Status status) {
        return status.getCode();
    }
}
