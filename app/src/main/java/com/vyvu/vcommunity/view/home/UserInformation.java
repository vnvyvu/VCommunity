package com.vyvu.vcommunity.view.home;

import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.vyvu.vcommunity.R;
import com.vyvu.vcommunity.databinding.ActivityUserInformationBinding;
import com.vyvu.vcommunity.firebase.UserDAO;
import com.vyvu.vcommunity.model.User;
import com.vyvu.vcommunity.utils.ComponentUtils;
import com.vyvu.vcommunity.viewmodel.home.UserInformationViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Callable;


public class UserInformation extends AppCompatActivity {
    private ActivityUserInformationBinding activityUserInformationBinding;
    private UserInformationViewModel mViewModel;
    private HashMap<String, String> data;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityUserInformationBinding=DataBindingUtil.setContentView(this, R.layout.activity_user_information);
        mViewModel=new ViewModelProvider(this).get(UserInformationViewModel.class);

        mViewModel.getMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                ComponentUtils.pushSnackBarInform(activityUserInformationBinding.getRoot(), s, 5000);
            }
        });
        data=new HashMap<>();

        activityUserInformationBinding.setUser(UserDAO.getUser());
        activityUserInformationBinding.setFirebaseUser(FirebaseAuth.getInstance().getCurrentUser());

        activityUserInformationBinding.inDateOfBirth.getEditText().setOnKeyListener(null);
        activityUserInformationBinding.inDateOfBirth.getEditText().setInputType(InputType.TYPE_NULL);
        activityUserInformationBinding.inDateOfBirth.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
                        activityUserInformationBinding.inDateOfBirth.getEditText().setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date(selection)));
                    }
                });
                picker.show(getSupportFragmentManager(), "DateOfBirth");
                activityUserInformationBinding.inDateOfBirth.setFocusable(false);
            }
        });

        activityUserInformationBinding.btnSaveAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user=new User();
                data.put("password", activityUserInformationBinding.inPassword.getEditText().getText().toString());
                data.put("repassword", activityUserInformationBinding.inRePassword.getEditText().getText().toString());
                user.setUsername(
                        activityUserInformationBinding.inUsername.getEditText().getText().toString());
                mViewModel.updateUserAuth(data, user, new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        mViewModel.getMessage().setValue("Success.");
                        return null;
                    }
                });
            }
        });

        activityUserInformationBinding.btnSaveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user=new User();
                data.put("name", activityUserInformationBinding.inName.getEditText().getText().toString());
                data.put("avatar", activityUserInformationBinding.inAvatar.getEditText().getText().toString());
                user.setDateOfBirth(activityUserInformationBinding.inDateOfBirth.getEditText().getText().toString());
                user.setGender(activityUserInformationBinding.inMale.isChecked()?1:0);
                user.setDisplayName(data.get("name"));
                user.setPhotoUrl(data.get("avatar"));
                user.setId(UserDAO.getUser().getId());

                mViewModel.updateUserInfo(data, user, new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        mViewModel.getMessage().setValue("Success.");
                        return null;
                    }
                });
            }
        });

        activityUserInformationBinding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}