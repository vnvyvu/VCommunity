package com.vyvu.vcommunity.viewmodel.home;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.vyvu.vcommunity.firebase.PostDAO;
import com.vyvu.vcommunity.firebase.TagDAO;
import com.vyvu.vcommunity.firebase.UserDAO;
import com.vyvu.vcommunity.model.Tag;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HomeViewModel extends ViewModel {
    private MutableLiveData<String> message;
    private MutableLiveData<Fragment> fragment;
    private UserDAO userDAO;
    private PostDAO postDAO;
    private TagDAO tagDAO;

    public HomeViewModel() {
        super();
        message=new MutableLiveData<>();
        userDAO=new UserDAO();
        postDAO=new PostDAO();
        tagDAO =new TagDAO();
        fragment=new MutableLiveData<>();
    }

    public MutableLiveData<String> getMessage() {
        return message;
    }

    public MutableLiveData<Fragment> getFragment() {
        return fragment;
    }

    public boolean isUserTagEmpty(){
        return UserDAO.getUser().getTagIDs()==null||UserDAO.getUser().getTagIDs().size()==0;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void initAllTags(Callable<Void> callable){
        Log.d("TAG", "onSuccess: "+TagDAO.getTags());
        if(TagDAO.getTags()==null){
            tagDAO.getAllTags().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    TagDAO.setTags(new HashMap<String, Tag>(
                            queryDocumentSnapshots.getDocuments()
                                    .stream()
                                    .map((ds)->{
                                        Tag t=ds.toObject(Tag.class);
                                        t.setId(ds.getId());
                                        return t;
                                    })
                                    .collect(Collectors.toMap(Tag::getId, Function.identity()))
                    ));
                    //Init live data
                    TagDAO.initTagsCount();
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
