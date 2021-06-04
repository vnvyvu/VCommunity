package com.vyvu.vcommunity.firebase;

import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.vyvu.vcommunity.model.Tag;

import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TagDAO extends DAO{
    private static CollectionReference collection;
    private static HashMap<String, Tag> tags;
    private static ListenerRegistration listenerRegistration;

    public TagDAO() {
        super();
        if (collection == null) collection = this.getDb().collection("tag");
    }

    public static void initTagsCount(){
        listenerRegistration=collection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if(error==null&&value!=null) {
                    tags=new HashMap<String, Tag>(
                            value.getDocuments()
                            .stream()
                            .map((ds)->{
                                Tag t=ds.toObject(Tag.class);
                                t.setId(ds.getId());
                                return t;
                            }).collect(Collectors.toMap(Tag::getId, Function.identity()))
                    );
                }
            }
        });
    }

    public static HashMap<String, Tag> getTags() {
        return tags;
    }

    public static void setTags(HashMap<String, Tag> tags) {
        TagDAO.tags = tags;
    }

    public Task<Void> updatePostCount(String tagID, boolean isInscrement){
        return collection.document(tagID).update("postCount", FieldValue.increment(isInscrement?1:-1));
    }

    public Task<QuerySnapshot> getAllTags(){
        return collection.get();
    }

    public static void signOut(){
        if(listenerRegistration!=null) listenerRegistration.remove();
        tags=null;
    }
}
