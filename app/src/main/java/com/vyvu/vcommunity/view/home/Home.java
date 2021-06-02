package com.vyvu.vcommunity.view.home;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.vyvu.vcommunity.R;
import com.vyvu.vcommunity.databinding.ActivityHomeBinding;
import com.vyvu.vcommunity.firebase.DAO;
import com.vyvu.vcommunity.utils.ComponentUtils;
import com.vyvu.vcommunity.view.MainActivity;
import com.vyvu.vcommunity.viewmodel.home.HomeViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class Home extends AppCompatActivity {
    private ActivityHomeBinding activityHomeBinding;
    private HomeViewModel mViewModel;
    //HashMap<fragmentLayoutID, Fragment>
    private HashMap<Integer, Fragment> fragmentHashMap;
    //Search view in AppBarTop
    private SearchView searchView;
    //Save current fragment to re-use
    private int currentFragment=R.id.itemRecently;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        mViewModel=new ViewModelProvider(this).get(HomeViewModel.class);

        //Add all fragments to map
        fragmentHashMap=new HashMap<>();
        fragmentHashMap.put(R.id.itemRecently, Recently.newInstance());
        fragmentHashMap.put(R.id.itemMyTags, MyTags.newInstance());
        fragmentHashMap.put(R.id.itemTop, Top.newInstance());
        fragmentHashMap.put(R.id.itemTags, Tags.newInstance());

        //Set custom ActionBar
        setSupportActionBar(activityHomeBinding.appTopBar);

        //If user has not selected any tags
        if(mViewModel.isUserTagEmpty()){
            //Get all tags for TagSelection activity
            mViewModel.initAllTags(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    startActivity(new Intent(getApplicationContext(), TagSelection.class));
                    return null;
                }
            });
        }

        //Listen message change
        mViewModel.getMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //Notice
                ComponentUtils.pushSnackBarInform(activityHomeBinding.getRoot(), s, 5000).show();
            }
        });

        mViewModel.getFragment().observe(this, new Observer<Fragment>() {
            @Override
            public void onChanged(Fragment fragment) {
                //Init all tags to avoid NullPointer
                mViewModel.initAllTags(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        ComponentUtils.changeToFragment(activityHomeBinding.container.getId(), fragment, getSupportFragmentManager());
                        return null;
                    }
                });
            }
        });

        //Default fragment is Recently
        if (mViewModel.getFragment().getValue() == null)
            mViewModel.getFragment().setValue(Recently.newInstance());

        activityHomeBinding.bottomHome.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                //Just eliminate user spam action
                if(currentFragment!=item.getItemId()){
                    currentFragment=item.getItemId();
                    mViewModel.getFragment().setValue(fragmentHashMap.get(currentFragment));
                    return true;
                }
                return false;
            }
        });

        activityHomeBinding.btnCreatePostFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Init all tags to avoid NullPointer
                mViewModel.initAllTags(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        startActivity(new Intent(getApplicationContext(), PostCreation.class));
                        return null;
                    }
                });
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_topbar_menu, menu);
        //Load user avatar
        Uri avatar=FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
        if(avatar!=null)
        ComponentUtils.loadImage(
                avatar,
                ComponentUtils.circleTransform(),
                new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) {
                        activityHomeBinding.appTopBar.getMenu()
                                .findItem(R.id.itemMe).setIcon(new BitmapDrawable(getResources(), bitmap));
                    }
                });
        else activityHomeBinding.appTopBar.getMenu()
                .findItem(R.id.itemMe).setIcon(R.drawable.ic_baseline_person_24);
        //Use SEARCH_SERVICE provided by Default
        searchView=((SearchView)activityHomeBinding.appTopBar.getMenu()
                .findItem(R.id.itemSearch)
                .getActionView());
        searchView.setSearchableInfo(
                ((SearchManager)getSystemService(SEARCH_SERVICE))
                        .getSearchableInfo(getComponentName())
        );
        //
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent i=new Intent(getApplicationContext(), Search.class);
                i.putExtra("search-key", query);
                startActivity(i);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.itemSearch:
                break;
            case R.id.itemEditInfo:
                startActivity(new Intent(this, UserInformation.class));
                return true;
            case R.id.itemSelectTags:
                mViewModel.initAllTags(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        startActivity(new Intent(getApplicationContext(), TagSelection.class));
                        return null;
                    }
                });
                return true;
            case R.id.itemLogout:
                //Custom sign out
                DAO.signOut();
                //Firebase sign out
                FirebaseAuth.getInstance().signOut();
                //Finish all activities
                finishAffinity();
                //Start MainActivity (Login)
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case R.id.itemMore:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}