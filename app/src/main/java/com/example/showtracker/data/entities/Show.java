package com.example.showtracker.data.entities;

import androidx.annotation.*;
import androidx.room.*;

import com.example.showtracker.data.typeconverters.*;

import java.util.*;

@Entity(
    tableName = "shows",
    indices = @Index("id")
)
public class Show implements listItem {
    @Ignore
    public static final String SHOW_ID = "SHOW_ID";

    @PrimaryKey
    @NonNull
    public String id;
    public String title;
    public int season;
    public int episode;
    public String comment;
    @TypeConverters(StatusConverter.class)
    public Status status;
    public int position;

    public enum Status {
        CURRENT(1),
        COMPLETED(2),
        PLAN_TO_WATCH(3),
        ON_HOLD(4),
        DROPPED(5);
        private int code;

        Status(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        @Override
        public String toString() {
            switch (this) {
                case CURRENT:
                    return "Currently Watching";
                case COMPLETED:
                    return "Completed";
                case PLAN_TO_WATCH:
                    return "Watch Later";
                case ON_HOLD:
                    return "On Hold";
                case DROPPED:
                    return "Dropped";
                default:
                    return "";
            }
        }
    }

    public Show(String title) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.season = 1;
        this.episode = 1;
        this.status = Status.CURRENT;
        this.comment = "";
    }

    @NonNull
    @Override
    public String getId() {
        return this.id;
    }

    @Ignore
    @NonNull
    @Override
    public String toString() {
        return this.title;
    }
}
