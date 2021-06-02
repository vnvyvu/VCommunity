package com.vyvu.vcommunity.viewmodel.home;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.vyvu.vcommunity.firebase.PostDAO;
import com.vyvu.vcommunity.firebase.TagCountDAO;
import com.vyvu.vcommunity.firebase.UserDAO;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class HomeViewModel extends ViewModel {
    private MutableLiveData<String> message;
    private MutableLiveData<Fragment> fragment;
    private UserDAO userDAO;
    private PostDAO postDAO;
    private TagCountDAO tagCountDAO;

    public HomeViewModel() {
        super();
        message=new MutableLiveData<>();
        userDAO=new UserDAO();
        postDAO=new PostDAO();
        tagCountDAO=new TagCountDAO();
        fragment=new MutableLiveData<>();
    }

    public MutableLiveData<String> getMessage() {
        return message;
    }

    public MutableLiveData<Fragment> getFragment() {
        return fragment;
    }

    public boolean isUserTagEmpty(){
        return UserDAO.getUser().getTags()==null||UserDAO.getUser().getTags().size()==0;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void initAllTags(Callable<Void> callable){
        if(TagCountDAO.getTagsCount()==null){
            tagCountDAO.getAllTagsCount().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    TagCountDAO.setTagsCount(new HashMap<>(
                            queryDocumentSnapshots.getDocuments()
                                    .stream()
                                    .collect(Collectors.toMap((ds)->ds.getId(), (ds)->ds.get("postCount", Long.class)))
                    ));
                    //Init live data
                    TagCountDAO.initTagsCount();
                    try {
                        callable.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }else {
            try {
                callable.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
