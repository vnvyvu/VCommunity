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

public class SearchViewModel extends ViewModel {
    private PostDAO postDAO;

    public SearchViewModel() {
        super();
        postDAO=new PostDAO();
    }

    public void initPostByTag(String tagID, Consumer<ArrayList<Post>> consumer){
        postDAO.getPostByTag(tagID, 10).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    consumer.accept(new ArrayList<>(
                            queryDocumentSnapshots.getDocuments().stream()
                                .map((ds)->{
                                    Post p=ds.toObject(Post.class);
                                    p.setId(ds.getId());
                                    return p;
                                }).collect(Collectors.toList())
                    ));
                }
            }
        });
    }

    public void initPostByWords(String words, Consumer<ArrayList<Post>> consumer){
        postDAO.getPostByWords(words, 10).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    consumer.accept(new ArrayList<>(
                            queryDocumentSnapshots.getDocuments().stream()
                                    .map((ds)->{
                                        Post p=ds.toObject(Post.class);
                                        p.setId(ds.getId());
                                        return p;
                                    }).collect(Collectors.toList())
                    ));
                }
            }
        });
    }
}
