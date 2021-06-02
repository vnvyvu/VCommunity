package com.vyvu.vcommunity.view.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.vyvu.vcommunity.R;
import com.vyvu.vcommunity.viewmodel.home.TopViewModel;

public class Top extends Fragment {

    private TopViewModel mViewModel;

    public static Top newInstance() {
        return new Top();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.top_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TopViewModel.class);
        // TODO: Use the ViewModel
    }

}