package com.vyvu.vcommunity.viewmodel.home;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.vyvu.vcommunity.firebase.PostDAO;
import com.vyvu.vcommunity.firebase.TagDAO;
import com.vyvu.vcommunity.firebase.UserDAO;
import com.vyvu.vcommunity.model.Post;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MyTagsViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private TagDAO tagDAO;
    private PostDAO postDAO;

    public MyTagsViewModel() {
        super();
        tagDAO =new TagDAO();
        postDAO=new PostDAO();
    }

    public void initMyPosts(Consumer<ArrayList<Post>> consumer){
        //Get post by id of author
        postDAO.getPostByUserId(UserDAO.getUser().getId(), 5).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    //Fire the posts to consumer, in View side, you can take it
                    consumer.accept(new ArrayList<>(
                            queryDocumentSnapshots.getDocuments().stream()
                                .map((ds)->{
                                    Post p=ds.toObject(Post.class);
                                    p.setId(ds.getId());
                                    return p;
                                })
                                .collect(Collectors.toList())
                    ));
                }
            }
        });
    }

    public void initMyTag(String tag, Consumer<ArrayList<Post>> consumer){
        //Get post by tag
        postDAO.getPostByTag(tag, 5).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    //Fire the posts to consumer, in View side, you can take it
                    consumer.accept(new ArrayList<>(
                            queryDocumentSnapshots.getDocuments().stream()
                                    .map((ds)->{
                                        Post p=ds.toObject(Post.class);
                                        p.setId(ds.getId());
                                        return p;
                                    })
                                    .collect(Collectors.toList())
                    ));
                }
            }
        });
    }
}