package com.example.gmail.presenter.interfaces;

import com.google.api.services.gmail.model.Message;

import java.util.List;

public interface IView {

    void initProgressDialog();

    void showProgressBar();

    void hideProgressBar();

    void onGetDataSuccess(List<Message> messages, String nextPage);

    void onGetDataFailure(String message);
}
