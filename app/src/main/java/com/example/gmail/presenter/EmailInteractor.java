package com.example.gmail.presenter;

import android.accounts.Account;
import android.content.Context;
import android.os.AsyncTask;

import com.example.gmail.R;
import com.example.gmail.presenter.interfaces.IInteractor;
import com.example.gmail.presenter.interfaces.IResponse;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.gmail.util.Constants.HTTP_TRANSPORT;
import static com.example.gmail.util.Constants.JSON_FACTORY;
import static com.example.gmail.util.Constants.PAGE_SIZE;

public class EmailInteractor implements IInteractor {


    private IResponse response;
    private Account account;
    private String pageInput;
    private String nextPage;
    private List<Message> responseList;

    EmailInteractor(IResponse response) {
        this.response = response;
    }

    @Override
    public void getInboxMessages(Context context, Account account, String pageInput) {
        this.account = account;
        this.pageInput = pageInput;
        responseList = new ArrayList<>();
        new GetMessagesList(context).execute();

    }

    private Gmail getService(WeakReference<Context> context) {
        Context applicationContext = context.get().getApplicationContext();

        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                applicationContext,
                Collections.singleton(GmailScopes.MAIL_GOOGLE_COM));
        credential.setSelectedAccount(account);

        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(context.get().getString(R.string.app_name)).build();
    }

    private class GetMessagesList extends AsyncTask<Void, Void, ListMessagesResponse> {

        private WeakReference<Context> context;

        GetMessagesList(Context context) {
            this.context = new WeakReference<>(context);
        }

        @Override
        protected ListMessagesResponse doInBackground(Void... objects) {
            if (context.get() == null) {
                return null;
            }

            try {
                Gmail service = getService(context);
                String user = "me";

                return service.users().messages().list(user).setMaxResults(PAGE_SIZE).setPageToken(pageInput).execute();

            } catch (IOException recoverableException) {
                recoverableException.printStackTrace();
                response.onFailure(recoverableException.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(ListMessagesResponse listMessagesResponse) {
            super.onPostExecute(listMessagesResponse);

            if (context.get() != null) {
                nextPage = listMessagesResponse.getNextPageToken();
                List<Message> messages = listMessagesResponse.getMessages();
                for (Message msg : messages) {
                    new GetMessagesById(context).execute(msg.getId());
                }
            }
        }
    }

    private class GetMessagesById extends AsyncTask<String, Void, Message> {

        private WeakReference<Context> context;


        GetMessagesById(WeakReference<Context> context) {
            this.context = context;
        }

        @Override
        protected Message doInBackground(String... messageIds) {
            if (context.get() == null) {
                return null;
            }

            try {

                Gmail service = getService(context);
                String user = "me";
                return service.users().messages().get(user, messageIds[0]).execute();

            } catch (UserRecoverableAuthIOException recoverableException) {
                if (context.get() != null) {
                    response.onFailure(recoverableException.getMessage());
                }
            } catch (IOException e) {
                response.onFailure(e.getMessage());
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Message message) {
            super.onPostExecute(message);

            if (context.get() != null) {
                responseList.add(message);
                if (responseList.size() == PAGE_SIZE)
                    response.onSuccess(responseList, nextPage);
            }
        }
    }
}
