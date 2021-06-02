package com.vyvu.vcommunity.view.home;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.vyvu.vcommunity.R;
import com.vyvu.vcommunity.databinding.RecentlyFragmentBinding;
import com.vyvu.vcommunity.firebase.PostDAO;
import com.vyvu.vcommunity.model.Post;
import com.vyvu.vcommunity.view.adapter.PostCardsAdapter;
import com.vyvu.vcommunity.viewmodel.home.RecentlyViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Recently extends Fragment {
    private RecentlyFragmentBinding recentlyFragmentBinding;
    private RecentlyViewModel mViewModel;
    private PostCardsAdapter postCardsAdapter;

    public static Recently newInstance() {
        return new Recently();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        recentlyFragmentBinding= DataBindingUtil.inflate(inflater, R.layout.recently_fragment, container, false);
        mViewModel=new ViewModelProvider(this).get(RecentlyViewModel.class);

        postCardsAdapter=new PostCardsAdapter(getContext());
        recentlyFragmentBinding.container.setAdapter(postCardsAdapter);
        recentlyFragmentBinding.container.setLayoutManager(new LinearLayoutManager(this.getContext()));

        return recentlyFragmentBinding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel.initRecentlyPost(10, new Consumer<ArrayList<Post>>() {
            @Override
            public void accept(ArrayList<Post> posts) {
                postCardsAdapter.setPosts(posts);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume() {
        PostDAO.destroy();
        mViewModel.initRecentlyPost(10, new Consumer<ArrayList<Post>>() {
            @Override
            public void accept(ArrayList<Post> posts) {
                postCardsAdapter.setPosts(posts);
            }
        });
        super.onResume();
    }
}