package com.snhu.ProjectTwo.utilities;


//@Author Christian Clark
//@Date 8-14-25

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

//Class to hold the data for a userinfo
public class UserInfo {
    private long _id;
    private long _user;
    private long _date;
    private float _weight;

    public UserInfo(long id, long user, long date, float weight){
        _id = id;
        _user = user;
        _date = date;
        _weight = weight;
    }

    public long getId() {
        return _id;
    }

    public float getWeight() {
        return _weight;
    }

    public long getUser() {
        return _user;
    }

    public String getDate() {
        return formatDate(_date);
    }

    public long getDateMilliseconds(){
        return _date;
    }
    private String formatDate(long date){
        return Instant.ofEpochMilli(date)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("MM/dd/yyyy\nhh:mm"));
    }
}
