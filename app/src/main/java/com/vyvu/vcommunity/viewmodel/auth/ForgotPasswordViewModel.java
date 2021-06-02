package com.vyvu.vcommunity.viewmodel.auth;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QuerySnapshot;
import com.vyvu.vcommunity.firebase.UserDAO;
import com.vyvu.vcommunity.model.User;

public class ForgotPasswordViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private UserDAO userDAO;
    private MutableLiveData<String> message;

    public ForgotPasswordViewModel() {
        super();
        userDAO = new UserDAO();
        message = new MutableLiveData<>();
    }

    public MutableLiveData<String> getMessage() {
        return message;
    }

    public void forgotPassword(User u) {
        //Validate
        if (u.getEmail().equals("") || u.getUsername().equals("")) {
            message.setValue("Don't leave any information blank");
            return;
        }

        userDAO.getUserByUsername(u).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                //Check username match email
                if (!queryDocumentSnapshots.isEmpty()
                        && queryDocumentSnapshots.getDocuments().get(0).toObject(User.class).getEmail().equals(u.getEmail())) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(u.getEmail()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            message.setValue("Success. Please check your mailbox!");
                        }
                    });
                } else message.setValue("Your information doesn't match!");
            }
        });
    }
}