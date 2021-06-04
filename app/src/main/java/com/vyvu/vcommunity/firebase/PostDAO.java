package com.vyvu.vcommunity.firebase;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.vyvu.vcommunity.model.Post;
import com.vyvu.vcommunity.utils.CoreUtils;

public class PostDAO extends DAO {
    private static CollectionReference collection;
    private static Post post;
    private static ListenerRegistration listenerRegistration;

    public PostDAO() {
        super();
        if (collection == null) collection = this.getDb().collection("post");
    }

    public Task<QuerySnapshot> getRecentlyPost(int limit){
        return collection.limit(limit)
                .orderBy("createdDate", Query.Direction.DESCENDING)
                .get();
    }

    public static void initPost(String postID){
        listenerRegistration=collection.document(postID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                //When user data changed without error
                if(error==null&&value!=null){
                    //Get new user data
                    Post p=value.toObject(Post.class);
                    if(p!=null) p.setId(value.getId());
                    post=p;
                }
            }
        });
    }

    public static void destroy(){
        if(listenerRegistration!=null) listenerRegistration.remove();
        post=null;
    }

    public static Post getPost() {
        return post;
    }

    public static void setPost(Post post) {
        PostDAO.post = post;
    }

    public Task<DocumentReference> insertPost(Post post){
        return collection.add(post);
    }

    public Task<Void> updatePost(Post post){
        return collection.document(post.getId()).set(CoreUtils.obj2Map(post), SetOptions.merge());
    }

    public Task<Void> updateView(Post post){
        return collection.document(post.getId()).update("view", FieldValue.increment(1));
    }
    public Task<Void> updateScore(Post post){
        return collection.document(post.getId()).update("score", post.getScore());
    }

    public Task<QuerySnapshot> getPostByUserId(String uid, long limit){
        return collection.whereEqualTo("userID", uid).limit(limit).get();
    }
    public Task<QuerySnapshot> getPostByTag(String tagID, long limit){
        return collection.whereEqualTo("tagID", tagID).limit(limit).get();
    }
    public Task<QuerySnapshot> getPostByWords(String words, long limit){
        return collection.orderBy("detail").startAt(words).endAt(words+"~").limit(limit).get();
    }
    public Task<DocumentSnapshot> getPostById(String postID){
        return collection.document(postID).get();
    }

    public Task<Void> delete(Post post){
        return collection.document(post.getId()).delete();
    }
}
