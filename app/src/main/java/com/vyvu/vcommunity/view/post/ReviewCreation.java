package com.vyvu.vcommunity.view.post;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.vyvu.vcommunity.R;
import com.vyvu.vcommunity.databinding.ActivityReviewCreationBinding;
import com.vyvu.vcommunity.firebase.PostDAO;
import com.vyvu.vcommunity.model.Review;
import com.vyvu.vcommunity.viewmodel.post.ReviewCreationViewModel;

import java.util.Date;
import java.util.concurrent.Callable;

public class ReviewCreation extends AppCompatActivity {
    private ActivityReviewCreationBinding reviewCreationBinding;
    private ReviewCreationViewModel mViewModel;
    private Review review;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reviewCreationBinding=DataBindingUtil.setContentView(this, R.layout.activity_review_creation);
        mViewModel=new ViewModelProvider(this).get(ReviewCreationViewModel.class);
        reviewCreationBinding.setReview(review=(Review) getIntent().getSerializableExtra("review"));
        reviewCreationBinding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        reviewCreationBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PostDAO.getPost()==null) finish();
                review.setTitle(reviewCreationBinding.inTitle.getEditText().getText().toString());
                review.setContent(reviewCreationBinding.inContent.getEditText().getText().toString());
                review.setCreatedDate(new Date());
                review.setScore(reviewCreationBinding.btnRating.getRating());
                mViewModel.submitReview(review, new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        finish();
                        return null;
                    }
                });
            }
        });
        reviewCreationBinding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewCreationBinding.btnDelete.setEnabled(false);
                mViewModel.deleteReview(review, new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        finish();
                        return null;
                    }
                });
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        reviewCreationBinding.btnDelete.setEnabled(true);
                    }
                }, 5000);
            }
        });
    }
}