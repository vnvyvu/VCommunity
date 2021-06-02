package com.vyvu.vcommunity.viewmodel.post;

import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;
import com.vyvu.vcommunity.firebase.ReviewDAO;
import com.vyvu.vcommunity.model.Review;

import java.util.concurrent.Callable;

public class ReviewCreationViewModel extends ViewModel {
    private ReviewDAO reviewDAO;

    public ReviewCreationViewModel() {
        super();
        reviewDAO=new ReviewDAO();
    }

    public void submitReview(Review review, Callable<Void> callable){
        reviewDAO.getReviewByAuthorAndPost(review.getUserID(), review.getPostID()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    review.setId(queryDocumentSnapshots.getDocuments().get(0).getId());
                    reviewDAO.updateReview(review).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            try {
                                callable.call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }else {
                    reviewDAO.insertReview(review).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            try {
                                callable.call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    public void deleteReview(Review review, Callable<Void> callable){
        reviewDAO.getReviewByAuthorAndPost(review.getUserID(), review.getPostID()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    reviewDAO.deleteReview(
                            queryDocumentSnapshots.getDocuments().get(0).getId()
                    ).addOnSuccessListener(new OnSuccessListener<Void>() {
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
        });
    }
}
