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
import androidx.recyclerview.widget.RecyclerView;

import com.vyvu.vcommunity.R;
import com.vyvu.vcommunity.databinding.MyTagItemsBinding;
import com.vyvu.vcommunity.databinding.MyTagsFragmentBinding;
import com.vyvu.vcommunity.firebase.TagDAO;
import com.vyvu.vcommunity.firebase.UserDAO;
import com.vyvu.vcommunity.model.Post;
import com.vyvu.vcommunity.model.Tag;
import com.vyvu.vcommunity.view.adapter.PostTinyCardsAdapter;
import com.vyvu.vcommunity.viewmodel.home.MyTagsViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.function.Consumer;

public class MyTags extends Fragment {
    private MyTagsFragmentBinding fragmentBinding;
    private MyTagsViewModel mViewModel;

    public static MyTags newInstance() {
        return new MyTags();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        fragmentBinding=DataBindingUtil.inflate(inflater, R.layout.my_tags_fragment, container, false);
        fragmentBinding.setLifecycleOwner(getViewLifecycleOwner());
        mViewModel=new ViewModelProvider(this).get(MyTagsViewModel.class);
        return fragmentBinding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Tag myPost=new Tag();
        myPost.setName("My posts");
        //Bind tag data to view
        fragmentBinding.myPosts.setTag(myPost);
        //Init Recyclerview
        fragmentBinding.myPosts.container.setAdapter(new PostTinyCardsAdapter(getContext()));
        //Get all user's posts and put to recyclerview after
        mViewModel.initMyPosts(new Consumer<ArrayList<Post>>() {
            @Override
            public void accept(ArrayList<Post> posts) {
                ((PostTinyCardsAdapter)fragmentBinding.myPosts.container.getAdapter()).setPosts(posts);
            }
        });
        //Recyclerview horizontal
        fragmentBinding.myPosts.container.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        //Init user's tags, 1 recyclerview each tag
        ArrayList<String> tagIDs=UserDAO.getUser().getTagIDs();
        switch (tagIDs.size()){
            case 5:
                initItem(fragmentBinding.myTag1, tagIDs.get(4));
            case 4:
                initItem(fragmentBinding.myTag2, tagIDs.get(3));
            case 3:
                initItem(fragmentBinding.myTag3, tagIDs.get(2));
            case 2:
                initItem(fragmentBinding.myTag4, tagIDs.get(1));
            case 1:
                initItem(fragmentBinding.myTag5, tagIDs.get(0));
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initItem(MyTagItemsBinding item, String tagID){
        //Bind tag data to view
        item.setTag(TagDAO.getTags().get(tagID));
        //Init Recyclerview
        item.container.setAdapter(new PostTinyCardsAdapter(getContext()));
        //Get all user's post by tag and put to recyclerview after
        mViewModel.initMyTag(tagID, new Consumer<ArrayList<Post>>() {
            @Override
            public void accept(ArrayList<Post> posts) {
                ((PostTinyCardsAdapter)item.container.getAdapter()).setPosts(posts);
            }
        });
        item.container.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        //Make it visible
        item.getRoot().setVisibility(View.VISIBLE);
    }
}