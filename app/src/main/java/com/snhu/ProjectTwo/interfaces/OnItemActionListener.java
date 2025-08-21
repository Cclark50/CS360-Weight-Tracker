package com.snhu.ProjectTwo.interfaces;

import com.snhu.ProjectTwo.utilities.UserInfo;

//callbacks for the delete and date clicked buttons on cards
public interface OnItemActionListener{
    void onDeleteClicked(UserInfo info, int position);
    void onDateClicked(UserInfo info, int position);
}
