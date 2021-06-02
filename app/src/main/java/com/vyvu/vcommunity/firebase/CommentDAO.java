package com.vyvu.vcommunity.firebase;

import com.google.firebase.firestore.CollectionReference;

public class CommentDAO extends DAO {
    private static CollectionReference collection;

    public CommentDAO() {
        super();
        if (collection == null) collection = this.getDb().collection("comment");
    }
}
