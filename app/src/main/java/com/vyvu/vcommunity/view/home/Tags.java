package com.vyvu.vcommunity.view.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.vyvu.vcommunity.R;
import com.vyvu.vcommunity.databinding.TagsFragmentBinding;
import com.vyvu.vcommunity.firebase.TagDAO;
import com.vyvu.vcommunity.view.adapter.TagCardsAdapter;
import com.vyvu.vcommunity.viewmodel.home.TagsViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Tags extends Fragment {
    private TagsFragmentBinding fragmentBinding;
    private TagsViewModel mViewModel;
    private TagCardsAdapter tagCardsAdapter;

    public static Tags newInstance() {
        return new Tags();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        fragmentBinding= DataBindingUtil.inflate(inflater, R.layout.tags_fragment, container, false);
        fragmentBinding.setLifecycleOwner(getViewLifecycleOwner());
        mViewModel=new ViewModelProvider(this).get(TagsViewModel.class);

        return fragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentBinding.container.setAdapter(tagCardsAdapter=new TagCardsAdapter(getContext()));
        tagCardsAdapter.setTags(new ArrayList<>(TagDAO.getTags().values()));
        fragmentBinding.container.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}