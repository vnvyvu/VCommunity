package com.vyvu.vcommunity.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.vyvu.vcommunity.R;
import com.vyvu.vcommunity.databinding.PostTinyRecyclerviewBinding;
import com.vyvu.vcommunity.firebase.PostDAO;
import com.vyvu.vcommunity.model.Post;
import com.vyvu.vcommunity.view.post.PostReviews;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PostTinyCardsAdapter extends RecyclerView.Adapter<PostTinyCardsAdapter.PostTinyCardsViewHolder> {
    private Context context;
    private ArrayList<Post> posts;

    public PostTinyCardsAdapter(Context context) {
        super();
        this.context = context;
        this.posts = new ArrayList<>();
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public PostTinyCardsViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        PostTinyRecyclerviewBinding recyclerviewBinding= DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.post_tiny_recyclerview, parent, false);
        return new PostTinyCardsViewHolder(recyclerviewBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostTinyCardsViewHolder holder, int position) {
        Post p=posts.get(position);
        PostTinyRecyclerviewBinding binding=holder.getBinding();
        binding.setPost(p);
        binding.outImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPost(p);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPost(p);
            }
        });
    }

    private void showPost(Post p) {
        new PostDAO();
        PostDAO.setPost(p);
        PostDAO.initPost(p.getId());
        Intent i=new Intent(context, PostReviews.class);
        context.startActivity(i);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class PostTinyCardsViewHolder extends RecyclerView.ViewHolder{
        private PostTinyRecyclerviewBinding binding;
        public PostTinyCardsViewHolder(PostTinyRecyclerviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public PostTinyRecyclerviewBinding getBinding() {
            return binding;
        }
    }
}
