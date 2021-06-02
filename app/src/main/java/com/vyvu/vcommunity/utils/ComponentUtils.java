package com.vyvu.vcommunity.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.annotation.MenuRes;
import androidx.annotation.RequiresApi;
import androidx.databinding.BindingAdapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.common.base.Strings;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;
import com.vyvu.vcommunity.R;

import java.util.function.Consumer;

public class ComponentUtils {
    public static Snackbar pushSnackBarInform(View view, String content, int duration) {
        Snackbar snackbar = Snackbar.make(view, content, duration);
        snackbar.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        return snackbar;
    }

    public static Transformation circleTransform() {
        return new Transformation() {
            @Override
            public Bitmap transform(Bitmap source) {
                int size = Math.min(source.getWidth(), source.getHeight());

                int x = (source.getWidth() - size) / 2;
                int y = (source.getHeight() - size) / 2;

                Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
                if (squaredBitmap != source) {
                    source.recycle();
                }

                Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

                Canvas canvas = new Canvas(bitmap);
                Paint paint = new Paint();
                BitmapShader shader = new BitmapShader(squaredBitmap,
                        Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                paint.setShader(shader);
                paint.setAntiAlias(true);

                float r = size / 2f;
                canvas.drawCircle(r, r, r, paint);

                squaredBitmap.recycle();
                return bitmap;
            }

            @Override
            public String key() {
                return "circle";
            }
        };
    }
    
    public static Transformation roundTransform(float radiusInPx){
        return new Transformation() {
            @Override
            public Bitmap transform(Bitmap source) {
                Bitmap bitmap = Bitmap.createBitmap(source.getWidth(), source.getHeight(), source.getConfig());
                Canvas canvas = new Canvas(bitmap);
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                BitmapShader shader = new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                paint.setShader(shader);
                RectF rect = new RectF(0.0f, 0.0f, source.getWidth(), source.getHeight());
                canvas.drawRoundRect(rect, radiusInPx, radiusInPx, paint);
                source.recycle();
                return bitmap;
            }

            @Override
            public String key() {
                return "round_corners";
            }
        };
    }

    public static void loadImage(Uri url, Transformation transformation, Consumer<Bitmap> consumer){
        if(transformation==null) transformation=new Transformation() {
            @Override
            public Bitmap transform(Bitmap source) {
                return source;
            }

            @Override
            public String key() {
                return "";
            }
        };
        Picasso.get().load(url).transform(transformation).into(new Target() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                consumer.accept(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    public static void makeCoolView(View view, Animation animation){
        animation.reset();
        view.clearAnimation();
        view.startAnimation(animation);
    }

    public static void changeToFragment(int containerId, Object f, FragmentManager fm) {
        fm.beginTransaction()
                .setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
                .replace(containerId, (Fragment) f)
                .addToBackStack(f.getClass().getSimpleName())
                .commit();
    }
    public static void changeToFragment(int containerId, Object f, String nameInStack, FragmentManager fm) {
        fm.beginTransaction()
                .setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
                .replace(containerId, (Fragment) f)
                .addToBackStack(nameInStack)
                .commit();
    }
    public static void reloadFragment(Object f, FragmentManager fm){
        fm.beginTransaction()
                .setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
                .detach((Fragment) f)
                .attach((Fragment) f)
                .commit();
    }

    public static PopupMenu getPopupMenu(Context context, View v, @MenuRes int id, PopupMenu.OnMenuItemClickListener clickItemEvent){
        PopupMenu popupMenu=new PopupMenu(context, v);
        popupMenu.getMenuInflater().inflate(id, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(clickItemEvent);
        return popupMenu;
    }

    public static TextWatcher afterCall(Consumer<String> consumer){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void afterTextChanged(Editable s) {
                consumer.accept(s.toString());
            }
        };

    }

    @BindingAdapter("imgURL")
    public static void loadImage(ImageView view, String url){
        if(!Strings.isNullOrEmpty(url)) Picasso.get().load(url).into(view);
    }
    @BindingAdapter("avatarURL")
    public static void loadAvatar(ImageView view, String url){
        if(!Strings.isNullOrEmpty(url)) {
            Picasso.get().load(url).transform(circleTransform()).into(view);
        }else view.setBackgroundResource(R.drawable.ic_baseline_person_24);
    }
    @BindingAdapter("roundAvatarURL")
    public static void loadRoundAvatar(ImageView view, String url){
        if(!Strings.isNullOrEmpty(url)) {
            Picasso.get().load(url).transform(roundTransform(20)).into(view);
        }
    }
    @BindingAdapter("backgroundURL")
    public static void loadBackground(ViewGroup view, String url){
        if(url!=null) Picasso.get().load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                view.setBackground(new BitmapDrawable(view.getResources(), bitmap));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }
}
