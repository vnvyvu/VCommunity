package com.vyvu.vcommunity.viewmodel.home;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.vyvu.vcommunity.firebase.UserDAO;
import com.vyvu.vcommunity.firebase.UsernameUniqueDAO;
import com.vyvu.vcommunity.model.User;

import java.util.HashMap;
import java.util.concurrent.Callable;

public class UserInformationViewModel extends ViewModel {
    private MutableLiveData<String> message;
    private UserDAO userDAO;
    private UsernameUniqueDAO usernameUniqueDAO;

    public UserInformationViewModel() {
        super();
        userDAO=new UserDAO();
        message=new MutableLiveData<>();
        usernameUniqueDAO=new UsernameUniqueDAO();
    }

    public MutableLiveData<String> getMessage() {
        return message;
    }

    public void updateUserInfo(HashMap<String, String> data, User user, Callable<Void> callable){
        userDAO.updateUserById(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                FirebaseAuth.getInstance().getCurrentUser().updateProfile(
                        new UserProfileChangeRequest.Builder()
                                .setDisplayName(data.get("name"))
                                .setPhotoUri(Uri.parse(data.get("avatar")))
                                .build()
                ).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        try {
                            callable.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
    public void updateUserAuth(HashMap<String, String> data, User user, Callable<Void> callable){
        if(data.get("password")!=null&&!data.get("password").equals(data.get("repassword"))) {
            message.setValue("Re-Password not match!");
        }else
        userDAO.updateUserById(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                usernameUniqueDAO.insertUnique(UserDAO.getUser().getUsername());
                if(data.get("password")!=null) FirebaseAuth.getInstance().getCurrentUser().updatePassword(data.get("password"))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                try {
                                    callable.call();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                else {
                    try {
                        callable.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
