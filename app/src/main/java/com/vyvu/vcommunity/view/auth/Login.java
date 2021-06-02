package com.vyvu.vcommunity.view.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.vyvu.vcommunity.R;
import com.vyvu.vcommunity.databinding.LoginFragmentBinding;
import com.vyvu.vcommunity.utils.ComponentUtils;
import com.vyvu.vcommunity.view.home.Home;
import com.vyvu.vcommunity.viewmodel.auth.LoginViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.concurrent.Callable;

public class Login extends Fragment {

    private LoginViewModel mViewModel;
    private LoginFragmentBinding loginFragmentBinding;

    public static Fragment newInstance() {
        return new Login();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        loginFragmentBinding=DataBindingUtil.inflate(inflater,
                R.layout.login_fragment, container, false);
        mViewModel=new ViewModelProvider(this).get(LoginViewModel.class);
        return loginFragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Listen when message change
        mViewModel.getMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //Notice
                ComponentUtils.pushSnackBarInform(view, s, 5000).show();
            }
        });

        //Listen when credential data change
        mViewModel.getCredentialData().observe(getViewLifecycleOwner(), new Observer<HashMap<String, String>>() {
            @Override
            public void onChanged(HashMap<String, String> stringStringHashMap) {
                //Login with new credential data
                mViewModel.login(stringStringHashMap, new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        afterAuth();
                        return null;
                    }
                });
            }
        });

        //Listen when 3rd party's credential data change
        mViewModel.getAuthCredential().observe(getActivity(), new Observer<AuthCredential>() {
            @Override
            public void onChanged(AuthCredential authCredential) {
                //Login with 3rd party's credential data
                mViewModel.authWith3rdPartyCredential(authCredential, new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        afterAuth();
                        return null;
                    }
                });
            }
        });

        loginFragmentBinding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get credential data
                String usernameOrEmail = loginFragmentBinding.inUsernameOrEmail.getEditText().getText().toString(),
                        password = loginFragmentBinding.inPassword.getEditText().getText().toString();
                HashMap<String, String> data = new HashMap<>();
                //Validate
                if (usernameOrEmail.equals("") || password.equals("")) {
                    mViewModel.getMessage().setValue("Don't leave any information blank");
                } else if (usernameOrEmail.contains("@")) {
                    //Login with email
                    data.put("email", usernameOrEmail);
                    data.put("password", password);
                    mViewModel.getCredentialData().setValue(data);
                } else {
                    //Login with username
                    data.put("username", usernameOrEmail);
                    data.put("password", password);
                    mViewModel.getCredentialData().setValue(data);
                }
            }
        });

        loginFragmentBinding.btnLoginWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(
                        getActivity(),
                        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken("897600951976-lk5hgkdenp7ph0bnktksa5c8vtnacmbf.apps.googleusercontent.com")
                                .requestEmail()
                                .build()
                );
                //Open Google Sign In dialog
                startActivityForResult(googleSignInClient.getSignInIntent(), 0);
            }
        });
    }

    private void afterAuth() {
        //Check current user logged in
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(this.getContext(), Home.class));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult();
            mViewModel.getAuthCredential().setValue(GoogleAuthProvider.getCredential(account.getIdToken(), null));
        }
    }

}