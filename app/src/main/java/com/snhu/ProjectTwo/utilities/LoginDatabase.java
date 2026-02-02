package com.snhu.ProjectTwo.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.snhu.ProjectTwo.utilities.*;

import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;


//@Author Christian Clark
//@Date 8-14-25

//SQLite database to store two tables: the login table that holds user Ids, usernames, passwords, and a goalweight for each user
//The other table, the UserInfo table, holds all users weights and dates tied to a user's userIds
public class LoginDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "userLogins.db";
    private static final int VERSION = 1;
    private static final String TAG_LOGIN = "com.snhu.ProjectTwo.utilities.LoginDatabase";
    private static final String TAG_INFO = "UserInfoDatabase";
    private static final String TAG_GOAL = "GoalTableDatabase";

    public LoginDatabase(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    //login table
    private static final class LoginTable{
        private static final String TABLE = "login";
        private static final String COL_ID = "_id";
        private static final String COL_USERNAME = "username";
        private static final String COL_PASSWORD = "password";
        private static final String COL_GOAL = "goal";
    }

    //user info table
    private static final class UserInfoTable{
        private static final String TABLE = "userInfo";
        private static final String COL_ID = "_id";
        private static final String COL_USER = "user";
        private static final String COL_DATE = "date";
        private static final String COL_WEIGHT = "weight";
    }

    private static final class GoalTable{
        private static final String TABLE = "goaltable";
        private static final String COL_ID = "_id";
        private static final String COL_GOAL = "goal";
        private static final String COL_START = "start";
        private static final String COL_USER = "user";
    }

    //creation of both tables
    @Override
    public void onCreate(SQLiteDatabase db){
        //Creating the login table (holds the goal weight)
        db.execSQL(
                "create table " + LoginTable.TABLE + " (" +
                LoginTable.COL_ID + " integer primary key autoincrement, " +
                LoginTable.COL_USERNAME + " text not null unique, " +
                LoginTable.COL_PASSWORD + " text not null, " +
                LoginTable.COL_GOAL + " real not null);"
                );

        //Creating the stored dates + weights for each user
        db.execSQL(
                "create table " + UserInfoTable.TABLE + " (" +
                        UserInfoTable.COL_ID + " integer primary key autoincrement, " +
                        UserInfoTable.COL_USER + " integer not null, " +
                        UserInfoTable.COL_DATE + " integer, " +
                        UserInfoTable.COL_WEIGHT + " real, " +
                        "foreign key (" + UserInfoTable.COL_USER + ") references " +
                        LoginTable.TABLE + "(" + LoginTable.COL_ID + "));"
        );
        // create a new table to store the start and goal weights for each user
        db.execSQL(
                "create table " + GoalTable.TABLE + " (" +
                GoalTable.COL_ID + " integer primary key autoincrement," +
                        GoalTable.COL_USER + " integer not null, " +
                        GoalTable.COL_START + " real not null, " +
                        GoalTable.COL_GOAL + " real not null, " +
                        "foreign key (" + GoalTable.COL_USER + ") references " +
                        LoginTable.TABLE + "(" + LoginTable.COL_ID + "));"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("drop table if exists " + UserInfoTable.TABLE);
        db.execSQL("drop table if exists " + LoginTable.TABLE);
        db.execSQL("drop table if exists " + GoalTable.TABLE);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db){
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys = ON;");
    }

    //retrieves the goal weight of a user
    public float GetGoalWeight(long userId){
        SQLiteDatabase db = getReadableDatabase();
        try(Cursor cursor = db.query(
                GoalTable.TABLE,
                new String[]{LoginTable.COL_GOAL},
                LoginTable.COL_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null, null, null
        )){
            if(cursor.moveToFirst()){
                return cursor.getFloat(cursor.getColumnIndexOrThrow(GoalTable.COL_GOAL));
            }
            throw new Exception();
        }catch (Exception e){
            Log.d("GETGOALWEIGHT", "error in get goal weight: " + e.getMessage());
            return -1;
        }

    }

    //adds a user to the login database
    public long AddNewUser(String username, String password, float goalWeight){
        SQLiteDatabase db = getWritableDatabase();
        long userId = -1;
        try{
            try(Cursor cursor = db.query(
                    LoginTable.TABLE, new String[]{LoginTable.COL_ID, LoginTable.COL_USERNAME}, LoginTable.COL_USERNAME + " = ?",
                    new String[]{username}, null, null, null
            )){
                if(cursor.moveToFirst()){
                    cursor.close();
                    throw new SQLiteException("Username Already Exists");
                }
            }

            String hashedPw = BCrypt.hashpw(password, BCrypt.gensalt(12));
            ContentValues values = new ContentValues();
            values.put(LoginTable.COL_USERNAME, username);
            values.put(LoginTable.COL_PASSWORD, hashedPw);
            values.put(LoginTable.COL_GOAL, goalWeight);
            userId = db.insert(LoginTable.TABLE, null, values);
        }catch (SQLiteException e){
            throw new SQLiteException("Failed to add user: " + e.getMessage());
        }finally {
            db.close();
        }

        return userId;
    }

    //adds a new weight to the database
    public long AddNewWeight(long id, long date, float weight){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(UserInfoTable.COL_USER, id);
        values.put(UserInfoTable.COL_DATE, date);
        values.put(UserInfoTable.COL_WEIGHT, weight);

        return db.insert(UserInfoTable.TABLE, null, values);
    }

    //gets a list of a user's weights and dates
    public ArrayList<UserInfo> GetInfoListByUser(long user){
        if(user <= 0){
            return new ArrayList<>();
        }
        SQLiteDatabase db = getReadableDatabase();
        //String query = "select * from " + UserInfoTable.TABLE + " where " + UserInfoTable.COL_USER + " = " + user;
        String[] columns = new String[]{
                UserInfoTable.COL_ID,
                UserInfoTable.COL_USER,
                UserInfoTable.COL_DATE,
                UserInfoTable.COL_WEIGHT
        };
        try(Cursor cursor = db.query(
                UserInfoTable.TABLE,
                columns,
                UserInfoTable.COL_USER + " = ?",
                new String[]{String.valueOf(user)},
                null, null,
                UserInfoTable.COL_DATE + " DESC")
                ){
            ArrayList<UserInfo> list = new ArrayList<UserInfo>();
            if(cursor.moveToFirst()){
                do{
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(UserInfoTable.COL_ID));
                    long curuser = cursor.getLong(cursor.getColumnIndexOrThrow(UserInfoTable.COL_USER));
                    long date = cursor.getLong(cursor.getColumnIndexOrThrow(UserInfoTable.COL_DATE));
                    float weight = cursor.getFloat(cursor.getColumnIndexOrThrow(UserInfoTable.COL_WEIGHT));
                    UserInfo info = new UserInfo(id, curuser, date, weight);
                    list.add(info);
                }while(cursor.moveToNext());
            }

            //Collections.reverse(list);

            return list;
        }catch (Exception e){
            Log.d(TAG_INFO, "Failed to query user info for user " + user, e);
            return new ArrayList<>();
        }
    }

    //removes a weight from the database
    public int RemoveWeightAt(long id, long user){
        SQLiteDatabase db = getWritableDatabase();

        String query = UserInfoTable.COL_ID + " = ? AND " + UserInfoTable.COL_USER + " = ?";

        return db.delete(UserInfoTable.TABLE, query, new String[]{String.valueOf(id), String.valueOf(user)});
    }

    //changes the date of a weight
    public int ChangeDateAt(long id, long user, long date){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserInfoTable.COL_DATE, date);

        int rowsAffected = db.update(
                UserInfoTable.TABLE,
                values,
                UserInfoTable.COL_ID + " = ? AND " + UserInfoTable.COL_USER + " = ?",
                new String[]{String.valueOf(id), String.valueOf(user)}
        );

        return rowsAffected;
    }

    public UserLogin GetLoginOfUser(long id){
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = new String[]{
                LoginTable.COL_ID,
                LoginTable.COL_USERNAME,
                LoginTable.COL_PASSWORD,
                LoginTable.COL_GOAL,
        };
        UserLogin login = null;
        try(Cursor cursor = db.query(
                LoginTable.TABLE,
                columns,
                LoginTable.COL_ID + " = ?",
                new String[]{String.valueOf(id)},
                null ,null,
                LoginTable.COL_ID + " ASC"
        )){
            if(cursor.moveToFirst()){
                long userid = cursor.getLong(cursor.getColumnIndexOrThrow(LoginTable.COL_ID));
                String username = cursor.getString(cursor.getColumnIndexOrThrow(LoginTable.COL_USERNAME));
                float goal = cursor.getFloat(cursor.getColumnIndexOrThrow(LoginTable.COL_GOAL));
                login = new UserLogin(userid, username, goal);
            }
            return login;
        }catch (Exception e){
            Log.d(TAG_LOGIN, "Failed to query user login for user id: " + id, e);
            return null;
        }
    }

    //confirms that a user's login was successful or not
    public UserLogin ConfirmLogin(String username, String unhashedPassword){
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = new String[]{
                LoginTable.COL_ID,
                LoginTable.COL_USERNAME,
                LoginTable.COL_PASSWORD,
                LoginTable.COL_GOAL
        };
        UserLogin login = null;
        try(Cursor cursor = db.query(
                LoginTable.TABLE,
                columns,
                LoginTable.COL_USERNAME + " = ?",
                new String[]{username},
                null, null, LoginTable.COL_ID + " ASC"
        )){
            if(cursor.moveToFirst()){
                String hashedPw = cursor.getString(cursor.getColumnIndexOrThrow(LoginTable.COL_PASSWORD));
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(LoginTable.COL_ID));
                String queryUsername = cursor.getString(cursor.getColumnIndexOrThrow(LoginTable.COL_USERNAME));
                float goal = cursor.getFloat(cursor.getColumnIndexOrThrow(LoginTable.COL_GOAL));

                boolean success = BCrypt.checkpw(unhashedPassword, hashedPw);
                if(success){
                    return new UserLogin(id, queryUsername, goal);
                }
            }
            return null;
        }catch (Exception e){
            Log.d(TAG_LOGIN, "Failed to query user login for user: " + username, e);
            return null;
        }
    }

    public long SetGoal(long id, float start, float goal){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(GoalTable.COL_USER, id);
        values.put(GoalTable.COL_START, start);
        values.put(GoalTable.COL_GOAL, goal);

        return db.insert(GoalTable.TABLE, null, values);
    }

    public GoalRow GetGoalRow(long id){
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = new String[]{
                GoalTable.COL_ID,
                GoalTable.COL_USER,
                GoalTable.COL_START,
                GoalTable.COL_GOAL
        };
        try(Cursor cursor = db.query(
                GoalTable.TABLE,
                columns,
                GoalTable.COL_USER + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null
        )){
            if(cursor.moveToFirst()){
                long currId = cursor.getLong(cursor.getColumnIndexOrThrow(GoalTable.COL_ID));
                long user = cursor.getLong(cursor.getColumnIndexOrThrow(GoalTable.COL_USER));
                float start = cursor.getFloat(cursor.getColumnIndexOrThrow(GoalTable.COL_START));
                float goal = cursor.getFloat(cursor.getColumnIndexOrThrow(GoalTable.COL_GOAL));

                return new GoalRow(currId, user, start, goal);
            }
            return null;
        }catch (Exception ex){
            Log.d(TAG_GOAL, "User does not exist when getting goal row", ex);
            return null;
        }
    }
}