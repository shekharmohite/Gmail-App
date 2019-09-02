package com.example.gmail.presenter.interfaces;

import com.google.api.services.gmail.model.Message;

import java.util.List;

public interface IResponse {

    void onSuccess(List<Message> messages, String nextPage);

    void onFailure(String message);

}
