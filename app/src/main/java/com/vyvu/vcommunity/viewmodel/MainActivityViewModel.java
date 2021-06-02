package com.vyvu.vcommunity.viewmodel;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.vyvu.vcommunity.firebase.UserDAO;
import com.vyvu.vcommunity.model.User;

import java.util.concurrent.Callable;

public class MainActivityViewModel extends ViewModel {
    private MutableLiveData<Fragment> fragment;
    private UserDAO userDAO;

    public MainActivityViewModel() {
        super();
        fragment = new MutableLiveData<>();
        userDAO=new UserDAO();
    }

    public MutableLiveData<Fragment> getFragment() {
        return fragment;
    }

    public void initExtraUserInformation(String uid, Callable<Void> callable){
        userDAO.getUserById(uid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserDAO.setUser(documentSnapshot.toObject(User.class));
                UserDAO.initUser();
                try {
                    callable.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
