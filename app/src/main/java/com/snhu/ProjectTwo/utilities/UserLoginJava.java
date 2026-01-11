package com.snhu.ProjectTwo.utilities;


//@Author Christian Clark
//@Date 8-14-25

//Class to hold the info of a user login
public class UserLoginJava {
    private long _id;
    private String _username;
    private float _goal;

    public UserLoginJava(long id, String username, float goal){
        _id = id;
        _username = username;
        _goal = goal;
    }

    public long getId() {
        return _id;
    }

    public float getGoal() {
        return _goal;
    }

    public String getUsername() {
        return _username;
    }
}
