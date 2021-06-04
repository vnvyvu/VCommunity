package com.vyvu.vcommunity.firebase;

import com.google.firebase.firestore.FirebaseFirestore;

public class DAO {
    private static FirebaseFirestore db;

    public DAO() {
        if (db == null) this.db = FirebaseFirestore.getInstance();
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public static void signOut(){
        UserDAO.signOut();
        TagDAO.signOut();
    }
}
