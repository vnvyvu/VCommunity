package com.vyvu.vcommunity.viewmodel.home;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.vyvu.vcommunity.firebase.PostDAO;
import com.vyvu.vcommunity.firebase.ReviewDAO;
import com.vyvu.vcommunity.firebase.TagDAO;
import com.vyvu.vcommunity.model.Post;

import java.util.function.Consumer;

public class PostCreationViewModel extends ViewModel {
    private MutableLiveData<String> message;
    private MutableLiveData<Boolean> isTagNotFound;
    private PostDAO postDAO;
    private ReviewDAO reviewDAO;
    private TagDAO tagDAO;

    public PostCreationViewModel() {
        super();
        postDAO=new PostDAO();
        tagDAO =new TagDAO();
        reviewDAO=new ReviewDAO();
        message=new MutableLiveData<>();
        isTagNotFound =new MutableLiveData<>();
    }

    public MutableLiveData<String> getMessage() {
        return message;
    }

    public MutableLiveData<Boolean> getIsTagNotFound() {
        return isTagNotFound;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void validateAutoCompleteTextView(String tag){
        //Tag must be in database, user cant create new
        if(TagDAO.getTags().values().stream().anyMatch((t) -> t.getName().equals(tag))){
            isTagNotFound.setValue(false);
        }else isTagNotFound.setValue(true);
    }

    public void submitPost(Post post, Consumer<Post> consumer){
        //Check post id is not provided
        if(post.getId()==null){
            //Count the number of posts with this tag
            tagDAO.updatePostCount(post.getTagID(), true);
            //Insert new post
            postDAO.insertPost(post).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Post p=documentSnapshot.toObject(Post.class);
                            p.setId(documentReference.getId());
                            //Send new post to consumer
                            consumer.accept(p);
                        }
                    });
                }
            });
        }else {
            postDAO.updatePost(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onSuccess(Void unused) {
                    consumer.accept(post);
                }
            });
        }
    }

    public void delete(Post post, Consumer<Void> consumer){
        //Count the number of posts with this tag
        tagDAO.updatePostCount(post.getTagID(), false);
        //Delete post document
        postDAO.delete(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(Void unused) {
                //Delete reviews
                reviewDAO.getReviewsByPostId(post.getId()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        queryDocumentSnapshots.getDocuments().forEach((ds)->{
                            ds.getReference().delete();
                        });
                    }
                });
                PostDAO.destroy();
                consumer.accept(null);
            }
        });
    }

    public void checkPostDeleted(Post post, Consumer<Boolean> isExist){
        postDAO.getPostById(post.getId()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                isExist.accept(documentSnapshot.exists());
            }
        });
    }
}
