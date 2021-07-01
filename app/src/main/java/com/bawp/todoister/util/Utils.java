package com.bawp.todoister.util;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.bawp.todoister.model.Priority;
import com.bawp.todoister.model.Task;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static void hideSoftKeyboard(View view){
        InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }
    public static int priorityColor(Task task){
        int color;
        if(task.getPriority() == Priority.HIGH){
            color = Color.argb(200,201,21,23);
        }else if(task.getPriority() == Priority.MEDIUM){
            color = Color.argb(200,155,179,0);
        }
        else if(task.getPriority() == Priority.LOW){
            color = Color.argb(200,51,189,129);
        }else {
            color = 0;
        }
        return color;
    }
    public static String formatDate(Date date){
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        simpleDateFormat.applyPattern("EEE, MMM d");
        return simpleDateFormat.format(date);
    }
}