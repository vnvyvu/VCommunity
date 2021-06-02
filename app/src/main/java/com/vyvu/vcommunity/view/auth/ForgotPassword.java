package com.vyvu.vcommunity.view.auth;

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

import com.vyvu.vcommunity.R;
import com.vyvu.vcommunity.databinding.ForgotPasswordFragmentBinding;
import com.vyvu.vcommunity.model.User;
import com.vyvu.vcommunity.utils.ComponentUtils;
import com.vyvu.vcommunity.viewmodel.auth.ForgotPasswordViewModel;

import org.jetbrains.annotations.NotNull;

public class ForgotPassword extends Fragment {

    private ForgotPasswordViewModel mViewModel;
    private ForgotPasswordFragmentBinding forgotPasswordFragmentBinding;

    public static ForgotPassword newInstance() {
        return new ForgotPassword();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        forgotPasswordFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.forgot_password_fragment, container, false);
        mViewModel = new ViewModelProvider(this).get(ForgotPasswordViewModel.class);
        return forgotPasswordFragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Listen when message change
        mViewModel.getMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                ComponentUtils.pushSnackBarInform(view, s, 5000).show();
            }
        });

        forgotPasswordFragmentBinding.btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();
                user.setUsername(forgotPasswordFragmentBinding.inUsername.getEditText().getText().toString());
                user.setEmail(forgotPasswordFragmentBinding.inEmail.getEditText().getText().toString());
                mViewModel.forgotPassword(user);
            }
        });
    }

}