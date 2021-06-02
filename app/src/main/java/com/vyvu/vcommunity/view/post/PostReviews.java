package com.vyvu.vcommunity.view.post;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.vyvu.vcommunity.R;
import com.vyvu.vcommunity.databinding.ActivityPostReviewsBinding;
import com.vyvu.vcommunity.firebase.PostDAO;
import com.vyvu.vcommunity.firebase.UserDAO;
import com.vyvu.vcommunity.model.Post;
import com.vyvu.vcommunity.model.Review;
import com.vyvu.vcommunity.model.User;
import com.vyvu.vcommunity.view.adapter.ReviewCardsAdapter;
import com.vyvu.vcommunity.view.home.PostCreation;
import com.vyvu.vcommunity.viewmodel.post.PostReviewsViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class PostReviews extends AppCompatActivity {
    private ActivityPostReviewsBinding postReviewsBinding;
    private PostReviewsViewModel mViewModel;
    private ReviewCardsAdapter reviewCardsAdapter;
    private Post post;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postReviewsBinding=DataBindingUtil.setContentView(this, R.layout.activity_post_reviews);
        mViewModel=new ViewModelProvider(this).get(PostReviewsViewModel.class);

        if (PostDAO.getPost()==null) finish();
        else post=PostDAO.getPost();
        //Bind post to bottom sheet
        postReviewsBinding.bottomSheet.setPost(post);

        mViewModel.updatePostView(post);

        mViewModel.initPostAuthor(post.getUserID(), new Consumer<User>() {
            @Override
            public void accept(User user) {
                //Bind post's author to bottom sheet
                postReviewsBinding.bottomSheet.setUser(user);
            }
        });

        //Init reviews recyclerview
        reviewCardsAdapter=new ReviewCardsAdapter(this);
        postReviewsBinding.outReviews.setAdapter(reviewCardsAdapter);
        postReviewsBinding.outReviews.setLayoutManager(new LinearLayoutManager(this));

        //Get all reviews of this post
        mViewModel.initReview(post.getId(), new Consumer<ArrayList<Review>>() {
            @Override
            public void accept(ArrayList<Review> reviews) {
                reviewCardsAdapter.setReviews(reviews);
                //Calculate average score
                post.setScore(reviewCardsAdapter.getPostScore());
                //Update average score
                mViewModel.updatePostScore(post, new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        postReviewsBinding.bottomSheet.setPost(post);
                        return null;
                    }
                });
            }
        });

        postReviewsBinding.btnRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Get all reviews by postID
                mViewModel.initReview(post.getId(), new Consumer<ArrayList<Review>>() {
                    @Override
                    public void accept(ArrayList<Review> reviews) {
                        reviewCardsAdapter.setReviews(reviews);
                        //Calculate average score
                        post.setScore(reviewCardsAdapter.getPostScore());
                        //Update average score
                        mViewModel.updatePostScore(post, new Callable<Void>() {
                            @Override
                            public Void call() throws Exception {
                                postReviewsBinding.bottomSheet.setPost(post);
                                return null;
                            }
                        });
                    }
                });
                //The refresh finished
                postReviewsBinding.btnRefresh.setRefreshing(false);
            }
        });

        //Get BottomSheetBehavior
        BottomSheetBehavior sheetBehavior=BottomSheetBehavior.from(postReviewsBinding.bottomSheet.layout);
        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull @NotNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull @NotNull View bottomSheet, float slideOffset) {

            }
        });

        //Handle user rating event
        postReviewsBinding.btnRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(fromUser){
                    Review review=new Review();
                    review.setPostID(post.getId());
                    review.setUserID(UserDAO.getUser().getId());
                    review.setScore(rating);
                    Intent i=new Intent(getApplicationContext(), ReviewCreation.class);
                    i.putExtra("review", review);
                    startActivity(i);
                }
            }
        });

        //Check viewer(user) is this post's author
        if(UserDAO.getUser().getId().equals(post.getUserID())){
            //Allow user edit this post
            postReviewsBinding.bottomSheet.btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(getApplicationContext(), PostCreation.class);
                    startActivity(i);
                }
            });
            postReviewsBinding.bottomSheet.btnEdit.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        if (PostDAO.getPost()==null) finish();
        super.onResume();
    }

    @Override
    protected void onStop() {
        if (PostDAO.getPost()==null) finish();
        super.onStop();
    }
}