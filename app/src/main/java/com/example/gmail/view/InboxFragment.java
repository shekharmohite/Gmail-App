package com.example.gmail.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gmail.R;
import com.example.gmail.databinding.FragmentInboxBinding;
import com.example.gmail.presenter.EmailPresenter;
import com.example.gmail.presenter.interfaces.IEventHandle;
import com.example.gmail.presenter.interfaces.IView;
import com.example.gmail.util.Helper;
import com.google.api.services.gmail.model.Message;

import java.util.ArrayList;
import java.util.List;

public class InboxFragment extends Fragment implements IView {

    private ProgressDialog progressDialog;
    private FragmentInboxBinding binding;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private String nextPageToken = "";
    private boolean loading = true;
    private List<Message> emails;
    private IEventHandle listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInboxBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        emails = new ArrayList<>();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initProgressDialog();
        listener.setToolbar(binding.toolbar.toolbar);

        setUpPagination();

        fetchMailFromServer(nextPageToken);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_logout, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_logout) {
            listener.signOut();
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof IEventHandle){
            listener = (IEventHandle) context;
        }
    }

    @Override
    public void initProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }

    @Override
    public void showProgressBar() {
        progressDialog.show();
    }

    @Override
    public void hideProgressBar() {
        progressDialog.dismiss();
    }

    @Override
    public void onGetDataSuccess(List<Message> messages, String nextPageToken) {
        emails.addAll(messages);
        binding.getAdapter().notifyDataSetChanged();
        loading = true;
        this.nextPageToken = nextPageToken;
    }

    @Override
    public void onGetDataFailure(final String message) {
        /*added run on ui due to handel app crash looper thread toast display from on background*/
        loading = true;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setUpPagination() {

        binding.setAdapter(new MailListAdapter(emails));

        binding.rvMail.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = binding.rvMail.getLayoutManager().getChildCount();
                    totalItemCount = binding.rvMail.getLayoutManager().getItemCount();
                    pastVisiblesItems = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            //Do pagination.. i.e. fetch new data
                            loading = false;
                            fetchMailFromServer(nextPageToken);

                        }
                    }
                }
            }
        });
    }

    private void fetchMailFromServer(String nextPageToken) {
        if (Helper.isNetworkConnected(getActivity())) {
            EmailPresenter presenter = new EmailPresenter(this);
            presenter.getInboxMessages(getActivity(), listener.getAccount(), nextPageToken);
        } else
            loading = true;
    }
}
