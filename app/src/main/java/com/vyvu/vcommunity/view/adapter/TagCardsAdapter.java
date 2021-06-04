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
import com.vyvu.vcommunity.databinding.TagsRecyclerviewBinding;
import com.vyvu.vcommunity.model.Tag;
import com.vyvu.vcommunity.view.home.Search;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class TagCardsAdapter extends RecyclerView.Adapter<TagCardsAdapter.TagCardsViewHolder> {
    private final Context context;
    private ArrayList<Tag> tags;

    public TagCardsAdapter(Context context) {
        this.context = context;
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public TagCardsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        TagsRecyclerviewBinding recyclerviewBinding= DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.tags_recyclerview, parent, false);
        return new TagCardsViewHolder(recyclerviewBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TagCardsViewHolder holder, int position) {
        Tag tag=tags.get(position);
        TagsRecyclerviewBinding binding=holder.getBinding();
        binding.setTag(tag);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context, Search.class);
                i.putExtra("search-tag", tag);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public class TagCardsViewHolder extends RecyclerView.ViewHolder{
        private TagsRecyclerviewBinding binding;

        public TagCardsViewHolder(TagsRecyclerviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public TagsRecyclerviewBinding getBinding() {
            return binding;
        }
    }
}
