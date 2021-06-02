package com.vyvu.vcommunity.viewmodel.auth;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.vyvu.vcommunity.firebase.UserDAO;
import com.vyvu.vcommunity.model.User;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Callable;

public class LoginViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<String> message;
    private MutableLiveData<HashMap<String, String>> credentialData;
    private MutableLiveData<AuthCredential> authCredential;
    private static UserDAO userDAO;

    public LoginViewModel() {
        this.credentialData = new MutableLiveData<>();
        this.message = new MutableLiveData<>();
        this.authCredential = new MutableLiveData<>();
        userDAO = new UserDAO();
    }

    public MutableLiveData<HashMap<String, String>> getCredentialData() {
        return credentialData;
    }

    public MutableLiveData<AuthCredential> getAuthCredential() {
        return authCredential;
    }

    public MutableLiveData<String> getMessage() {
        return message;
    }

    public void login(HashMap<String, String> dat, Callable<Void> callable) {
        //Check login with username
        if (!dat.containsKey("email")) {
            User user = new User();
            user.setUsername(dat.get("username"));
            userDAO.getUserByUsername(user).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    //Check if username exist then get his email to auth by email and password
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        dat.put("email", String.valueOf(documentSnapshot.toObject(User.class).getEmail()));
                        loginWithEmail(dat, callable);
                    } else message.setValue("Authentication failed. Check your information again!");
                }
            });
        } else loginWithEmail(dat, callable);
    }

    private void loginWithEmail(HashMap<String, String> dat, Callable<Void> callable) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(dat.get("email"), dat.get("password")).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                //Get user data with email
                userDAO.getUserByEmail(dat.get("email")).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            UserDAO.setUser(queryDocumentSnapshots.getDocuments().get(0).toObject(User.class));
                            //Listen this user for live update
                            UserDAO.initUser();
                            try {
                                callable.call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                message.setValue("Authentication failed. Check your information again!");
            }
        });
    }

    public void authWith3rdPartyCredential(AuthCredential authCredential, Callable<Void> callable) {
        FirebaseAuth.getInstance().signInWithCredential(authCredential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                //Need to check user data already exists
                userDAO.getUserByEmail(authResult.getUser().getEmail()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //If exists
                        if(!queryDocumentSnapshots.isEmpty()){
                            //Init live data
                            try {
                                UserDAO.setUser(
                                        queryDocumentSnapshots.getDocuments().get(0).toObject(User.class)
                                );
                                //Listen this user for live update
                                UserDAO.initUser();
                                callable.call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else{
                            //Get some data provided by FirebaseUser
                            User user = new User();
                            //FirebaseUser uid is document id
                            user.setId(authResult.getUser().getUid());
                            //This is for loginWithEmail()
                            user.setEmail(authResult.getUser().getEmail());
                            //This allow for other user can get display name, photo url, creation time of this user
                            user.setDisplayName(authResult.getUser().getDisplayName());
                            user.setPhotoUrl(authResult.getUser().getPhotoUrl().toString());
                            user.setCreationTimestamp(new Date(authResult.getUser().getMetadata().getCreationTimestamp()));

                            //Update user data, if user not exist in Firestore, he will be created
                            userDAO.updateUserById(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    try {
                                        UserDAO.setUser(user);
                                        //Listen this user for live update
                                        UserDAO.initUser();
                                        callable.call();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                message.setValue("Authentication error!");
            }
        });
    }

}