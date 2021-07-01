package com.bawp.todoister.util;

import androidx.room.TypeConverter;

import com.bawp.todoister.model.Priority;

import java.util.Date;

public class Convertor {

    @TypeConverter
    public static Date fromTimeStamp(Long value){
        if(value == null){
            return null;
        }else{
            return new Date(value);
        }
    }
    @TypeConverter
    public static Long dateToTimeStamp(Date value){
        if(value == null){
            return null;
        }else{
            return value.getTime();
        }
    }

    @TypeConverter
    public static String fromPriority(Priority priority){
        if (priority == null){
            return null;
        }else {
            return priority.name();
        }
    }

    @TypeConverter
    public static Priority toPriority(String priority){
        if (priority == null){
            return null;
        }else {
            return Priority.valueOf(priority);
        }
    }
}
