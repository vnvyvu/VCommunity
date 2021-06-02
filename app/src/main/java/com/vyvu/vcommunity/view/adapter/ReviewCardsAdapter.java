package com.vyvu.vcommunity.view.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.vyvu.vcommunity.R;
import com.vyvu.vcommunity.databinding.ReviewRecyclerviewBinding;
import com.vyvu.vcommunity.firebase.ReviewDAO;
import com.vyvu.vcommunity.firebase.UserDAO;
import com.vyvu.vcommunity.model.Review;
import com.vyvu.vcommunity.model.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ReviewCardsAdapter extends RecyclerView.Adapter<ReviewCardsAdapter.ReviewCardsViewHolder> {
    private Context context;
    private ArrayList<Review> reviews;

    public ReviewCardsAdapter(Context context) {
        this.context = context;
        this.reviews = new ArrayList<>();
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public float getPostScore(){
        if (reviews.size()==0) return 0;
        return reviews.stream()
                .map((v)->v.getScore())
                .reduce((float)0, (a, b)->a+b)/reviews.size();
    }

    @NonNull
    @NotNull
    @Override
    public ReviewCardsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ReviewRecyclerviewBinding recyclerviewBinding=DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.review_recyclerview, parent, false);
        return new ReviewCardsViewHolder(recyclerviewBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ReviewCardsViewHolder holder, int position) {
        Review r=reviews.get(position);
        UserDAO userDAO=new UserDAO();
        ReviewDAO reviewDAO=new ReviewDAO();
        ReviewRecyclerviewBinding binding=holder.getBinding();
        //Bind user data to his review
        if(r.getUserID().equals(UserDAO.getUser().getId())) binding.setUser(UserDAO.getUser());
        else userDAO.getUserById(r.getUserID()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                binding.setUser(documentSnapshot.toObject(User.class));
            }
        });
        //Bind review data
        binding.setReview(r);

        //Handle user vote this review
        binding.btnUserVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Avoid NullPointer
                ArrayList<String> votes;
                if(r.getUserVote()==null) votes=new ArrayList<>();
                else votes=r.getUserVote();
                //Current user vote
                String uid=UserDAO.getUser().getId();

                //If he voted, then remove his vote
                if(votes.contains(uid)) votes.remove(uid);
                else votes.add(uid);

                //Update local object(cache)
                r.setUserVote(votes);
                //Update it on database
                reviewDAO.updateReview(r).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        binding.setReview(r);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ReviewCardsViewHolder extends RecyclerView.ViewHolder{
        private ReviewRecyclerviewBinding binding;

        public ReviewCardsViewHolder(ReviewRecyclerviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ReviewRecyclerviewBinding getBinding() {
            return binding;
        }
    }
}
