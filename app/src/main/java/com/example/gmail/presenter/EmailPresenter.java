package com.example.gmail.presenter;

import android.accounts.Account;
import android.content.Context;

import com.example.gmail.presenter.interfaces.IInteractor;
import com.example.gmail.presenter.interfaces.IPresenter;
import com.example.gmail.presenter.interfaces.IResponse;
import com.example.gmail.presenter.interfaces.IView;
import com.google.api.services.gmail.model.Message;

import java.util.List;

public class EmailPresenter implements IPresenter, IResponse {

    private IView view;
    private IInteractor msgInteract;


    public EmailPresenter(IView view) {
        this.view = view;
        msgInteract = new EmailInteractor(this);
    }

    @Override
    public void getInboxMessages(Context context, Account account, String nextPage) {
        view.showProgressBar();
        msgInteract.getInboxMessages(context, account, nextPage);
    }

    @Override
    public void onSuccess(List<Message> messages, String nextPage) {
        view.hideProgressBar();
        view.onGetDataSuccess(messages, nextPage);
    }

    @Override
    public void onFailure(String message) {
        view.hideProgressBar();
        view.onGetDataFailure(message);
    }
}
