package com.example.gmail.presenter.interfaces;

import android.accounts.Account;

import androidx.appcompat.widget.Toolbar;

public interface IEventHandle {

    void signIn();

    void signOut();

    Account getAccount();

    void setToolbar(Toolbar toolbar);
}
