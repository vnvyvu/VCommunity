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

import java.util.HashMap;
import java.util.stream.Collectors;

public class TagCountDAO extends DAO{
    private static CollectionReference collection;
    private static HashMap<String, Long> tagsCount;
    private static ListenerRegistration listenerRegistration;

    public TagCountDAO() {
        super();
        if (collection == null) collection = this.getDb().collection("tag");
    }

    public static void initTagsCount(){
        listenerRegistration=collection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if(error==null&&value!=null)
                tagsCount=new HashMap<>(
                        value.getDocuments()
                                .stream()
                                .collect(Collectors.toMap((ds)->ds.getId(), (ds)->ds.get("postCount", Long.class)))
                );
            }
        });
    }

    public static void setTagsCount(HashMap<String, Long> tagsCount) {
        TagCountDAO.tagsCount = tagsCount;
    }

    public static HashMap<String, Long> getTagsCount(){
        return tagsCount;
    }

    public Task<Void> updatePostCount(String tag, boolean isInscrement){
        return collection.document(tag).update("postCount", FieldValue.increment(isInscrement?1:-1));
    }

    public Task<QuerySnapshot> getAllTagsCount(){
        return collection.get();
    }

    public static void signOut(){
        if(listenerRegistration!=null) listenerRegistration.remove();
        tagsCount=null;
    }
}
