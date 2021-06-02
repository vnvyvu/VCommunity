package com.vyvu.vcommunity.viewmodel.auth;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.vyvu.vcommunity.firebase.UserDAO;
import com.vyvu.vcommunity.firebase.UsernameUniqueDAO;
import com.vyvu.vcommunity.model.User;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Callable;

public class SignUpViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<String> message;
    private MutableLiveData<HashMap<String, String>> userData;
    private UserDAO userDAO;
    private UsernameUniqueDAO usernameUniqueDAO;

    public SignUpViewModel() {
        super();
        message = new MutableLiveData<>();
        userDAO = new UserDAO();
        userData = new MutableLiveData<>();
        usernameUniqueDAO = new UsernameUniqueDAO();
    }

    public MutableLiveData<HashMap<String, String>> getUserData() {
        return userData;
    }

    public MutableLiveData<String> getMessage() {
        return message;
    }

    public void sign(HashMap<String, String> data, Callable<Void> callable) {
        //Check username already exists
        usernameUniqueDAO.getUnique(data.get("username")).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //If username not exist
                if (!documentSnapshot.exists()) {
                    //Sign up in FirebaseAuth
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(data.get("email"), data.get("password")).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            //Convert to HashMap<String, Object>
                            HashMap<String, Object> userDat=new HashMap<>(data);
                            //Put external data
                            userDat.put("displayName", authResult.getUser().getDisplayName());
                            if(authResult.getUser().getPhotoUrl()!=null){
                                data.put("photoUrl", authResult.getUser().getPhotoUrl().toString());
                            }
                            userDat.put("creationTimestamp", new Date(authResult.getUser().getMetadata().getCreationTimestamp()));

                            //Add username to username_unique collection in Firestore
                            usernameUniqueDAO.insertUnique(data.get("username"));
                            //Add user to Firestore
                            userDAO.insertUserWithId(userDat, authResult.getUser().getUid()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    //Listen this user for live update
                                    userDAO.getUserById(authResult.getUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            UserDAO.setUser(documentSnapshot.toObject(User.class));
                                            //Listen this user for live update
                                            UserDAO.initUser();
                                            //Sign in now
                                            FirebaseAuth.getInstance().signInWithEmailAndPassword(data.get("email"), data.get("password")).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                @Override
                                                public void onSuccess(AuthResult authResult) {
                                                    try {
                                                        callable.call();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            message.setValue("Email has been taken!");
                        }
                    });
                } else message.setValue("Username has been taken!");
            }
        });
    }
}