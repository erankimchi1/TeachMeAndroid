package com.example.user.teachme;

import android.widget.Button;

public class DataModel {

    String teacher;
    String id;

    public DataModel(String teacher, String id) {
        this.teacher = teacher;
        this.id = id;
    }


    public String getTeacher() {
        return teacher;
    }
    public String getId() {
        return id;
    }

}
