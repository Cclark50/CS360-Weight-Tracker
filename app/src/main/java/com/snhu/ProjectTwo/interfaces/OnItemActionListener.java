package com.snhu.ProjectTwo.interfaces;

import com.snhu.ProjectTwo.utilities.UserInfo;
import com.snhu.ProjectTwo.utilities.UserInfoJava;

//callbacks for the delete and date clicked buttons on cards
public interface OnItemActionListener{
    //when clicking the delete button on a card
    void onDeleteClicked(UserInfo info, int position);

    //when clicking the date button on a card
    void onDateClicked(UserInfo info, int position);
}
