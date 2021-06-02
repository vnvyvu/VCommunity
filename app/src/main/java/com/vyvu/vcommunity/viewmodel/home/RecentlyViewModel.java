package com.vyvu.vcommunity.viewmodel.home;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.vyvu.vcommunity.firebase.PostDAO;
import com.vyvu.vcommunity.model.Post;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class RecentlyViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private PostDAO postDAO;

    public RecentlyViewModel() {
        super();
        postDAO=new PostDAO();
    }

    public void initRecentlyPost(int limit, Consumer<ArrayList<Post>> consumer){
        postDAO.getRecentlyPost(limit).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                consumer.accept(new ArrayList<>(
                        queryDocumentSnapshots
                        .getDocuments()
                        .stream()
                        .map((ds)->{
                            Post res=ds.toObject(Post.class);
                            res.setId(ds.getId());
                            return res;
                        })
                        .collect(Collectors.toList())
                ));
            }
        });
    }
}