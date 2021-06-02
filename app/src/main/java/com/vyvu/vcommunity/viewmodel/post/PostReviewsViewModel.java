package com.vyvu.vcommunity.viewmodel.post;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.vyvu.vcommunity.firebase.PostDAO;
import com.vyvu.vcommunity.firebase.ReviewDAO;
import com.vyvu.vcommunity.firebase.UserDAO;
import com.vyvu.vcommunity.model.Post;
import com.vyvu.vcommunity.model.Review;
import com.vyvu.vcommunity.model.User;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PostReviewsViewModel extends ViewModel {
    private MutableLiveData<String> message;
    private UserDAO userDAO;
    private ReviewDAO reviewDAO;
    private PostDAO postDAO;

    public PostReviewsViewModel() {
        super();
        message=new MutableLiveData<>();
        userDAO=new UserDAO();
        reviewDAO=new ReviewDAO();
        postDAO=new PostDAO();
    }

    public void initPostAuthor(String uid, Consumer<User> consumer){
        userDAO.getUserById(uid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                consumer.accept(documentSnapshot.toObject(User.class));
            }
        });
    }

    public void initReview(String postID, Consumer<ArrayList<Review>> consumer){
        reviewDAO.getReviewsByPostId(postID).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                consumer.accept(new ArrayList<>(
                        queryDocumentSnapshots.getDocuments()
                                .stream()
                                .map((ds)->{
                                    Review r=ds.toObject(Review.class);
                                    r.setId(ds.getId());
                                    return r;
                                }).collect(Collectors.toList())
                ));
            }
        });
    }

    public void updatePostScore(Post post, Callable<Void> callable){
        postDAO.updateScore(post).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    public void updatePostView(Post post){
        postDAO.updateView(post);
    }
}
