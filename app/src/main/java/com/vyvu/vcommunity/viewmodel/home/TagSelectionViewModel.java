package com.vyvu.vcommunity.viewmodel.home;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.vyvu.vcommunity.firebase.UserDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class TagSelectionViewModel extends ViewModel {
    private MutableLiveData<Integer> maxOfTags;
    private UserDAO userDAO;
    public final static int MAX=5;

    public TagSelectionViewModel() {
        super();
        userDAO=new UserDAO();
        maxOfTags=new MutableLiveData<>();
        maxOfTags.setValue(MAX-UserDAO.getUser().getTags().size());
    }

    public MutableLiveData<Integer> getMaxOfTags() {
        return maxOfTags;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void saveUserTags(HashMap<Chip, String> chipStringHashMap, Callable<Void> callable){
        UserDAO.getUser().setTags(new ArrayList<>(
                chipStringHashMap.keySet()
                        .stream()
                        .filter((chip)->chip.isChecked())
                        .map((chip)->chipStringHashMap.get(chip))
                        .collect(Collectors.toList())
        ));
        userDAO.updateUserById(UserDAO.getUser()).addOnSuccessListener(new OnSuccessListener<Void>() {
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
}
