package com.vyvu.vcommunity.view.home;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.SearchView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;
import com.vyvu.vcommunity.R;
import com.vyvu.vcommunity.databinding.ActivityTagSelectionBinding;
import com.vyvu.vcommunity.firebase.TagDAO;
import com.vyvu.vcommunity.firebase.UserDAO;
import com.vyvu.vcommunity.model.Tag;
import com.vyvu.vcommunity.utils.ComponentUtils;
import com.vyvu.vcommunity.viewmodel.home.TagSelectionViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class TagSelection extends AppCompatActivity {
    private ActivityTagSelectionBinding activityTagSelectionBinding;
    private TagSelectionViewModel mViewModel;
    private HashMap<Chip, Tag> chipStringHashMap;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTagSelectionBinding=DataBindingUtil.setContentView(this, R.layout.activity_tag_selection);
        mViewModel=new ViewModelProvider(this).get(TagSelectionViewModel.class);
        //Init background and effect
        activityTagSelectionBinding.setUrl("https://cdn.dribbble.com/users/648922/screenshots/6887377/attachments/1466542/3_night_1125_2436_wallpaper.jpg?compress=1&resize="+activityTagSelectionBinding.layout.getWidth()+"x"+activityTagSelectionBinding.layout.getHeight());
        ComponentUtils.makeCoolView(activityTagSelectionBinding.title, AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein));
        activityTagSelectionBinding.setTagCountMax(mViewModel);

        //Init viewmodel events
        mViewModel.getMaxOfTags().observe(this, new Observer<Integer>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(Integer integer) {
                if(integer==TagSelectionViewModel.MAX) {
                    activityTagSelectionBinding.btnSave.setVisibility(View.GONE);
                }
                else {
                    activityTagSelectionBinding.btnSave.setVisibility(View.VISIBLE);
                }
                activityTagSelectionBinding.setTagCountMax(mViewModel);
            }
        });

        activityTagSelectionBinding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.saveUserTags(chipStringHashMap, new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        finish();
                        return null;
                    }
                });
            }
        });

        chipStringHashMap=new HashMap<>();
        ArrayList<String> checkedTag=UserDAO.getUser().getTagIDs();
        if(checkedTag==null){
            for (Tag tag: TagDAO.getTags().values()) {
                Chip chip=createChip();
                chip.setText(tag.getName()+"("+tag.getPostCount()+")");
                chipStringHashMap.put(chip, tag);
                activityTagSelectionBinding.tagsGroup.addView(chip);
            }
        }else{
            for (Tag tag: TagDAO.getTags().values()) {
                Chip chip=createChip();
                chip.setText(tag.getName()+"("+tag.getPostCount()+")");
                chip.setChecked(checkedTag.contains(tag.getId()));
                chipStringHashMap.put(chip, tag);
                activityTagSelectionBinding.tagsGroup.addView(chip);
            }
        }

        activityTagSelectionBinding.btnSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                for (Map.Entry<Chip, Tag> chipStringEntry:chipStringHashMap.entrySet()) {
                    if(chipStringEntry.getValue().getName().contains(newText)||newText.contains(chipStringEntry.getValue().getName())){
                        chipStringEntry.getKey().setVisibility(View.VISIBLE);
                    }else chipStringEntry.getKey().setVisibility(View.GONE);
                }
                return false;
            }
        });
    }

    private Chip createChip() {
        Chip chip = (Chip) getLayoutInflater().inflate(R.layout.chip_layout_tag_selection, activityTagSelectionBinding.tagsGroup, false);
        chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chip.isChecked()) {
                    if(mViewModel.getMaxOfTags().getValue()==0) chip.setChecked(false);
                    else {
                        mViewModel.getMaxOfTags().setValue(mViewModel.getMaxOfTags().getValue()-1);
                    }
                } else {
                    mViewModel.getMaxOfTags().setValue(mViewModel.getMaxOfTags().getValue()+1);
                }
            }
        });
        return chip;
    }

}