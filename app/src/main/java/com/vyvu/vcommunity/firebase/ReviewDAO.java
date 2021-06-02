package com.vyvu.vcommunity.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.vyvu.vcommunity.model.Review;
import com.vyvu.vcommunity.utils.CoreUtils;

public class ReviewDAO extends DAO {
    private static CollectionReference collection;

    public ReviewDAO() {
        super();
        if (collection == null) collection = this.getDb().collection("review");
    }

    public Task<QuerySnapshot> getReviewsByPostId(String postID){
        return collection.whereEqualTo("postID", postID).get();
    }

    public Task<DocumentReference> insertReview(Review review){
        return collection.add(review);
    }

    public Task<Void> updateReview(Review review){
        return collection.document(review.getId()).set(CoreUtils.obj2Map(review), SetOptions.merge());
    }

    public Task<QuerySnapshot> getReviewByAuthorAndPost(String uid, String postID){
        return collection.whereEqualTo("userID", uid)
                .whereEqualTo("postID", postID)
                .get();
    }

    public Task<Void> deleteReview(String id){
        return collection.document(id).delete();
    }

}
