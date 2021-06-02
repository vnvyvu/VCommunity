package com.vyvu.vcommunity.view.auth;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.vyvu.vcommunity.R;
import com.vyvu.vcommunity.databinding.SignUpFragmentBinding;
import com.vyvu.vcommunity.utils.ComponentUtils;
import com.vyvu.vcommunity.view.home.Home;
import com.vyvu.vcommunity.viewmodel.auth.SignUpViewModel;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Callable;

public class SignUp extends Fragment {

    private SignUpViewModel mViewModel;
    private SignUpFragmentBinding signUpFragmentBinding;

    public static SignUp newInstance() {
        return new SignUp();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        signUpFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.sign_up_fragment, container, false);
        mViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        return signUpFragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Init Date Picker
        signUpFragmentBinding.inDateOfBirth.getEditText().setInputType(InputType.TYPE_NULL);
        signUpFragmentBinding.inDateOfBirth.getEditText().setOnKeyListener(null);
        signUpFragmentBinding.inDateOfBirth.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) return;
                MaterialDatePicker<Long> picker = MaterialDatePicker.Builder
                        .datePicker()
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();
                picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        signUpFragmentBinding.inDateOfBirth.getEditText().setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date(selection)));
                    }
                });
                picker.show(getActivity().getSupportFragmentManager(), "DateOfBirth");
                signUpFragmentBinding.inDateOfBirth.setFocusable(false);
            }
        });
        //Listen message change
        mViewModel.getMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //Notice
                ComponentUtils.pushSnackBarInform(view, s, 5000).show();
            }
        });

        mViewModel.getUserData().observe(getViewLifecycleOwner(), new Observer<HashMap<String, String>>() {
            @Override
            public void onChanged(HashMap<String, String> stringStringHashMap) {
                mViewModel.sign(stringStringHashMap, new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        afterAuth();
                        return null;
                    }
                });
            }
        });

        signUpFragmentBinding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                HashMap<String, String> data = new HashMap<>();
                data.put("username", signUpFragmentBinding.inUsername.getEditText().getText().toString());
                data.put("dateOfBirth", signUpFragmentBinding.inDateOfBirth.getEditText().getText().toString());
                data.put("email", signUpFragmentBinding.inEmail.getEditText().getText().toString());
                data.put("password", signUpFragmentBinding.inPassword.getEditText().getText().toString());
                data.put("repassword", signUpFragmentBinding.inRePassword.getEditText().getText().toString());

                //Validate
                if (!data.values().stream().findAny().isPresent() || data.values().stream().anyMatch((s) -> s.equals(""))) {
                    mViewModel.getMessage().setValue("Don't leave any information blank");
                }
                if (!data.get("password").equals(data.get("repassword"))) {
                    mViewModel.getMessage().setValue("Re-Password not match!");
                } else mViewModel.getUserData().setValue(data);
            }
        });
    }

    private void afterAuth() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent i = new Intent(this.getContext(), Home.class);
            startActivity(i);
        }
    }

}