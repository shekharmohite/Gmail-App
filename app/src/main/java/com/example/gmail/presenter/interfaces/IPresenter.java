package com.example.gmail.presenter.interfaces;

import android.accounts.Account;
import android.content.Context;

public interface IPresenter {
    void getInboxMessages(Context context, Account account, String nextPage);
}
