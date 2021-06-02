package com.vyvu.vcommunity.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;

public class UsernameUniqueDAO extends DAO{
    private static CollectionReference collection;

    public UsernameUniqueDAO() {
        super();
        if (collection == null) collection = this.getDb().collection("username_unique");
    }

    public Task<Void> insertUnique(String username) {
        return collection.document(username).set(new HashMap<>());
    }

    public Task<DocumentSnapshot> getUnique(String username) {
        return collection.document(username).get();
    }
}
