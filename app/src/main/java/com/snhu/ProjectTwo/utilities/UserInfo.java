package com.snhu.ProjectTwo.utilities;


//@Author Christian Clark
//@Date 8-14-25

//Class to hold the data for a userinfo
public class UserInfo {
    private long _id;
    private long _user;
    private String _date;
    private float _weight;

    public UserInfo(long id, long user, String date, float weight){
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
        return _date;
    }
}
