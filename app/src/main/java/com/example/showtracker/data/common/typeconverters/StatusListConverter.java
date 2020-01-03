package com.example.showtracker.data.common.typeconverters;

import androidx.room.*;

import com.example.showtracker.data.shows.entities.*;

import java.util.*;

public class StatusListConverter {
    @TypeConverter
    public static List<Show.Status> toStatusList(List<Integer> statusCodes) {
        List<Show.Status> statusList = new ArrayList<>();
        for (int code : statusCodes) {
            statusList.add(StatusConverter.toStatus(code));
        }
        return statusList;
    }

    @TypeConverter
    public static List<Integer> toIntegerList(List<Show.Status> statusList) {
        List<Integer> statusCodes = new ArrayList<>();
        for (Show.Status status : statusList) {
            statusCodes.add(StatusConverter.toInteger(status));
        }
        return statusCodes;
    }
}
