package com.vyvu.vcommunity.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
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
import com.vyvu.vcommunity.databinding.ActivityMainBinding;
import com.vyvu.vcommunity.firebase.UserDAO;
import com.vyvu.vcommunity.utils.ComponentUtils;
import com.vyvu.vcommunity.view.auth.ForgotPassword;
import com.vyvu.vcommunity.view.auth.Login;
import com.vyvu.vcommunity.view.auth.SignUp;
import com.vyvu.vcommunity.view.home.Home;
import com.vyvu.vcommunity.viewmodel.MainActivityViewModel;

import java.util.HashMap;
import java.util.concurrent.Callable;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding activityMainBinding;
    private MainActivityViewModel mainActivityViewModel;
    //HashMap<fragmentLayoutID, Fragment>
    private HashMap<Integer, Fragment> fragmentHashMap;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance=this;
        new UserDAO();
        activityMainBinding=DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        //Check user logged in
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            mainActivityViewModel.initExtraUserInformation(FirebaseAuth.getInstance().getCurrentUser().getUid(), new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    Intent i = new Intent(getApplicationContext(), Home.class);
                    startActivity(i);
                    return null;
                }
            });
        }

        //Add all fragments to map
        fragmentHashMap=new HashMap<>();
        fragmentHashMap.put(R.id.itemLogin, Login.newInstance());
        fragmentHashMap.put(R.id.itemSignUp, SignUp.newInstance());
        fragmentHashMap.put(R.id.itemForgotPassword, ForgotPassword.newInstance());

        //Catch event when Fragment is set value
        mainActivityViewModel.getFragment().observe(this, new Observer<Fragment>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChanged(Fragment fragment) {
                ComponentUtils.changeToFragment(
                        activityMainBinding.container.getId(),
                        fragment,
                        getSupportFragmentManager()
                );
            }
        });

        //The default fragment is Login
        if (mainActivityViewModel.getFragment().getValue() == null)
            mainActivityViewModel.getFragment().setValue(Login.newInstance());

        //Detect and change background based on device's theme
        initBackground(R.drawable.login_bg_light, R.drawable.login_bg_dark);
        //Set logo to Action Bar Top
        getSupportActionBar().setIcon(getDrawable(R.drawable.logo));
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        activityMainBinding.bottomAuth.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            private int previousItem = R.id.itemLogin;
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //Just eliminate user spam action
                if (previousItem != item.getItemId()){
                    previousItem=item.getItemId();
                    //Set value for live variable, after this, onChanged() will be call
                    mainActivityViewModel.getFragment().setValue(
                            fragmentHashMap.get(item.getItemId())
                    );
                    return true;
                }
                return false;
            }
        });
    }

    private void initBackground(int lightID, int nightID) {
        switch (this.getBaseContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Light mode is active
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    activityMainBinding.loginBackground.setBackground(getDrawable(lightID));
                }
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    activityMainBinding.loginBackground.setBackground(getDrawable(nightID));
                }
                break;
        }
    }

    //Just for getSharedPreferences()
    private static MainActivity instance;
    public static MainActivity getInstance(){
        return instance;
    }

    //Just for exit with double press back
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