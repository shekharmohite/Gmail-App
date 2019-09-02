package com.example.gmail.presenter.interfaces;

import android.accounts.Account;
import android.content.Context;

public interface IInteractor {
    void getInboxMessages(Context context, Account account, String nextPage);
}
