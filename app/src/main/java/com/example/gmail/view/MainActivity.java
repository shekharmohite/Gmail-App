package com.example.gmail.view;

import android.accounts.Account;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.gmail.R;
import com.example.gmail.databinding.ActivityMainBinding;
import com.example.gmail.presenter.interfaces.IEventHandle;
import com.example.gmail.util.Helper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.services.gmail.GmailScopes;

import static com.example.gmail.util.Constants.KEY_ACCOUNT;
import static com.example.gmail.util.Constants.RC_SIGN_IN;

public class MainActivity extends AppCompatActivity implements IEventHandle {

    private GoogleSignInClient mGoogleSignInClient;
    private Account mAccount;
    private String accName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Restore instance state
        if (savedInstanceState != null) {
            mAccount = savedInstanceState.getParcelable(KEY_ACCOUNT);
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(GmailScopes.MAIL_GOOGLE_COM))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if the user is already signed in and all required scopes are granted
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (GoogleSignIn.hasPermissions(account, new Scope(GmailScopes.MAIL_GOOGLE_COM))) {
            mAccount = account.getAccount();
            accName = account.getDisplayName();
            replaceFragment(new InboxFragment());
        } else {
            replaceFragment(new LoginFragment());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_ACCOUNT, mAccount);
    }

    @Override
    public void signIn() {
        if (Helper.isNetworkConnected(this)) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {

        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            accName = account.getDisplayName();
            mAccount = account.getAccount();
            replaceFragment(new InboxFragment());
        } catch (ApiException e) {
            mAccount = null;
            replaceFragment(new LoginFragment());
        }
    }

    public void replaceFragment(Fragment fragment) {
        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.screenArea, fragment);
            ft.commit();
        }
    }

    @Override
    public void signOut() {
        if (Helper.isNetworkConnected(this))
            mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    replaceFragment(new LoginFragment());
                }
            });
    }

    @Override
    public Account getAccount() {
        return mAccount;
    }

    @Override
    public void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(accName);
        getSupportActionBar().setSubtitle(mAccount.name);
    }
}
