package com.vyvu.vcommunity.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.vyvu.vcommunity.R;
import com.vyvu.vcommunity.databinding.PostRecyclerviewBinding;
import com.vyvu.vcommunity.firebase.PostDAO;
import com.vyvu.vcommunity.firebase.TagDAO;
import com.vyvu.vcommunity.model.Post;
import com.vyvu.vcommunity.view.post.PostReviews;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PostCardsAdapter extends RecyclerView.Adapter<PostCardsAdapter.PostCardsViewHolder> {
    private Context context;
    private ArrayList<Post> posts;

    public PostCardsAdapter(Context context) {
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
    public PostCardsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        final PostRecyclerviewBinding recyclerviewBinding=
                DataBindingUtil.inflate(LayoutInflater.from(context),
                        R.layout.post_recyclerview, parent, false);
        return new PostCardsViewHolder(recyclerviewBinding);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(PostCardsViewHolder holder, int position) {
        Post p=posts.get(position);
        PostRecyclerviewBinding binding=holder.getBinding();
        binding.setPost(p);
        binding.setTag(TagDAO.getTags().get(p.getTagID()));
        binding.btnExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.outDetail.getVisibility()==View.GONE) {
                    binding.outDetail.setVisibility(View.VISIBLE);
                    binding.btnExpand.setIcon(binding.getRoot().getResources().getDrawable(R.drawable.ic_baseline_keyboard_arrow_up_18));
                }else {
                    binding.outDetail.setVisibility(View.GONE);
                    binding.btnExpand.setIcon(binding.getRoot().getResources().getDrawable(R.drawable.ic_baseline_keyboard_arrow_down_18));
                }
            }
        });
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
    public class PostCardsViewHolder extends RecyclerView.ViewHolder{
        private PostRecyclerviewBinding binding;

        public PostCardsViewHolder(PostRecyclerviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public PostRecyclerviewBinding getBinding() {
            return binding;
        }
    }
}
