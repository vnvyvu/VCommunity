package com.vyvu.vcommunity.view.home;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.vyvu.vcommunity.R;
import com.vyvu.vcommunity.databinding.ActivityPostCreationBinding;
import com.vyvu.vcommunity.firebase.PostDAO;
import com.vyvu.vcommunity.firebase.TagDAO;
import com.vyvu.vcommunity.firebase.UserDAO;
import com.vyvu.vcommunity.model.Post;
import com.vyvu.vcommunity.model.Tag;
import com.vyvu.vcommunity.utils.ComponentUtils;
import com.vyvu.vcommunity.viewmodel.home.PostCreationViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.function.Consumer;

public class PostCreation extends AppCompatActivity {
    private ActivityPostCreationBinding postCreationBinding;
    private PostCreationViewModel mViewModel;
    private Tag selectedTag;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postCreationBinding=DataBindingUtil.setContentView(this, R.layout.activity_post_creation);
        mViewModel=new ViewModelProvider(this).get(PostCreationViewModel.class);

        //Bind post and viewmodel to view
        postCreationBinding.setPost(PostDAO.getPost());
        postCreationBinding.setMViewModel(mViewModel);

        //Listen message change
        mViewModel.getMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                ComponentUtils.pushSnackBarInform(postCreationBinding.getRoot(), s, 5000).show();
            }
        });

        //Listen tag input
        mViewModel.getIsTagNotFound().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                //If validator false, fire the error to user
                if(aBoolean) postCreationBinding.inTag.setError("Error. Tag not found!");
                else postCreationBinding.inTag.setError(null);
            }
        });

        postCreationBinding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //Initial AutoCompleteTextView by set all tags got from database in previous Activity
        ((AutoCompleteTextView)postCreationBinding.inTag.getEditText())
                .setAdapter(new ArrayAdapter<>(
                        this,
                        R.layout.tag_autocomplete_textview,
                        new ArrayList<>(TagDAO.getTags().values())
                ));
        //Catch text changed, ComponentUtils.afterCall return a TextWatcher
        //Purpose is just neat and can be reused in future
        postCreationBinding.inTag.getEditText().addTextChangedListener(ComponentUtils.afterCall(new Consumer<String>() {
            @Override
            public void accept(String s) {
                //Check tag, tag must be in list
                mViewModel.validateAutoCompleteTextView(s);
            }
        }));

        //Get selectedTag
        ((AutoCompleteTextView)postCreationBinding.inTag.getEditText()).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedTag=(Tag) parent.getAdapter().getItem(position);
            }
        });

        postCreationBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check tag input validation is error
                if(postCreationBinding.inTag.isErrorEnabled()) {
                    mViewModel.getMessage().setValue("Select a tag in list");
                    return;
                }
                //Avoid user double click
                postCreationBinding.btnSubmit.setEnabled(false);
                Post p=new Post();
                //Check post received in Intent is not null, it means the author want to update it
                if(PostDAO.getPost()!=null){
                    p=PostDAO.getPost();
                    if(selectedTag==null) TagDAO.getTags().get(p.getTagID());
                }else {
                    //User want to create new
                    p.setCreatedDate(new Date());
                    p.setUserID(UserDAO.getUser().getId());
                }
                p.setEditedDate(new Date());
                p.setDetail(postCreationBinding.inDetail.getEditText().getText().toString());
                p.setImage(postCreationBinding.inImage.getEditText().getText().toString());
                p.setName(postCreationBinding.inName.getEditText().getText().toString());
                if(selectedTag!=null) p.setTagID(selectedTag.getId());
                p.setShortInfo(postCreationBinding.inShortInfo.getEditText().getText().toString());
                //Validate empty fields
                if (TextUtils.isEmpty(p.getDetail())||TextUtils.isEmpty(p.getName())||TextUtils.isEmpty(p.getTagID())||TextUtils.isEmpty(p.getShortInfo())) {
                    mViewModel.getMessage().setValue("Don't leave any require fields blank");
                    postCreationBinding.btnSubmit.setEnabled(true);
                }else{
                    //Submit post
                    mViewModel.submitPost(p, new Consumer<Post>() {
                        @Override
                        public void accept(Post post) {
                            postCreationBinding.btnSubmit.setEnabled(true);
                            finish();
                        }
                    });
                }
            }
        });

        postCreationBinding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.delete(PostDAO.getPost(), new Consumer<Void>() {
                    @Override
                    public void accept(Void unused) {
                        finish();
                    }
                });
            }
        });
    }

}