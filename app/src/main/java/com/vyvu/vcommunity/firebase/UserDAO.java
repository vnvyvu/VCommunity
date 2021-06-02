package com.vyvu.vcommunity.firebase;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.vyvu.vcommunity.model.User;
import com.vyvu.vcommunity.utils.CoreUtils;

import java.util.HashMap;

public class UserDAO extends DAO {
    private static CollectionReference collection;
    private static User user;
    private static ListenerRegistration listenerRegistration;

    public UserDAO() {
        super();
        if (collection==null) collection = this.getDb().collection("user");
    }

    //Listen when user data change
    public static void initUser() {
        String uid=FirebaseAuth.getInstance().getUid();
        listenerRegistration=collection.document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                //When user data changed without error
                if(error==null&&value!=null){
                    //Get new user data
                    User u=value.toObject(User.class);
                    if(u!=null) u.setId(uid);
                    user=u;
                }
            }
        });
    }

    public static User getUser() {
        //Get user from preferences
        return user;
    }

    public static void setUser(User user) {
        UserDAO.user = user;
    }

    public static void signOut(){
        //Remove user data change listener
        listenerRegistration.remove();
        //Clear preferences
        user=null;
    }

    public static CollectionReference getCollection() {
        return collection;
    }

    public Task<Void> insertUserWithId(User user) {
        return collection.document(user.getId()).set(CoreUtils.obj2Map(user));
    }

    public Task<Void> insertUserWithId(HashMap<String, Object> user, String uid) {
        return collection.document(uid).set(user);
    }

    public Task<QuerySnapshot> getUserByUsername(User user) {
        return collection.whereEqualTo("username", user.getUsername())
                .limit(1)
                .get();
    }
    public Task<QuerySnapshot> getUserByEmail(String email) {
        return collection.whereEqualTo("email", email)
                .limit(1)
                .get();
    }

    public Task<Void> updateUserById(User user) {
        return collection.document(user.getId()).set(CoreUtils.obj2Map(user), SetOptions.merge());
    }
    public Task<DocumentSnapshot> getUserById(String uid){
        return collection.document(uid).get();
    }
}
