package com.vyvu.vcommunity.view.home;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.vyvu.vcommunity.R;
import com.vyvu.vcommunity.databinding.ActivitySearchBinding;
import com.vyvu.vcommunity.model.Post;
import com.vyvu.vcommunity.view.adapter.PostCardsAdapter;
import com.vyvu.vcommunity.viewmodel.home.SearchViewModel;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Search extends AppCompatActivity {
    private ActivitySearchBinding searchBinding;
    private PostCardsAdapter postCardsAdapter;
    private SearchViewModel mViewModel;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        searchBinding=DataBindingUtil.setContentView(this, R.layout.activity_search);
        mViewModel=new ViewModelProvider(this).get(SearchViewModel.class);
        setSupportActionBar(searchBinding.appTopBar);

        searchBinding.container.setAdapter(postCardsAdapter=new PostCardsAdapter(this));
        searchBinding.container.setLayoutManager(new LinearLayoutManager(this));
        String data=getIntent().getStringExtra("search-key");
        if(data==null) {
            data=getIntent().getStringExtra("search-tag");
            if(data==null) finish();
            mViewModel.initPostByTag(data, new Consumer<ArrayList<Post>>() {
                @Override
                public void accept(ArrayList<Post> posts) {
                    postCardsAdapter.setPosts(posts);
                }
            });
        }else{
            mViewModel.initPostByWords(data, new Consumer<ArrayList<Post>>() {
                @Override
                public void accept(ArrayList<Post> posts) {
                    postCardsAdapter.setPosts(posts);
                }
            });
        }
        getSupportActionBar().setTitle("Search for: ");
        getSupportActionBar().setSubtitle(data);
    }
}