package com.eziamtech.malwapathshala.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eziamtech.malwapathshala.BuildConfig;
import com.eziamtech.malwapathshala.Model.Blog.Result;
import com.eziamtech.malwapathshala.R;
import com.eziamtech.malwapathshala.Util.LocaleUtils;
import com.eziamtech.malwapathshala.Util.PrefManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class BlogDetail extends AppCompatActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private TextView tvBlogTitle, tvBlogInDetail, txtToolbarTitle, txtBack, tvUrdhu, tvHindi, tvEnglish;
    private ImageView ivBlogImage;
    LinearLayout lyBack, lyToolbar;
    BottomNavigationView bottomNavigationView;
    private ExtendedFloatingActionButton efbSelectLanguage;
    private FloatingActionButton fbEnglish, fabHindi, fabUrdhu;
    Boolean isAllFabVisible;
    Result result;
    String currentLanguage = "en";

    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_detail);


        // get current language
        init();

        currentLanguage = prefManager.getValue("select_language");
        Log.e("lan_currentLan", "" + currentLanguage);

        currentLanguage = LocaleUtils.getSelectedLanguageId();
        Log.e("currentLanguage", "" + currentLanguage);


    }

    private void init() {
        prefManager = new PrefManager(BlogDetail.this);
        tvBlogInDetail = findViewById(R.id.tvBlogInDetail);
        tvBlogTitle = findViewById(R.id.tvBlogTitle);
        ivBlogImage = findViewById(R.id.ivBlogImage);
        txtToolbarTitle = findViewById(R.id.txtToolbarTitle);
        txtBack = findViewById(R.id.txtBack);
        lyToolbar = findViewById(R.id.lyToolbar);
        lyToolbar.setVisibility(View.VISIBLE);
        lyBack = findViewById(R.id.lyBack);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        efbSelectLanguage = findViewById(R.id.efbSelectLanguage);
        fbEnglish = findViewById(R.id.fbEnglish);
        fabHindi = findViewById(R.id.fabHindi);
        fabUrdhu = findViewById(R.id.fabUrdhu);
        tvHindi = findViewById(R.id.tvHindi);
        tvEnglish = findViewById(R.id.tvEnglish);
        tvUrdhu = findViewById(R.id.tvUrdhu);

        //floating action buttons
        fbEnglish.setVisibility(View.GONE);
        fabUrdhu.setVisibility(View.GONE);
        fabHindi.setVisibility(View.GONE);
        tvEnglish.setVisibility(View.GONE);
        tvHindi.setVisibility(View.GONE);
        tvUrdhu.setVisibility(View.GONE);
        isAllFabVisible = false;
        efbSelectLanguage.shrink();

        efbSelectLanguage.setOnClickListener(this);

        // get data from intent
        Intent intent = getIntent();
        result = (Result) intent.getSerializableExtra("blog");
        tvBlogTitle.setText(result.getTitle());
        tvBlogInDetail.setText(result.getDetail());
        Picasso.get().load(result.getImage()).into(ivBlogImage);

        /*txtToolbarTitle.setText(result.getTitle());
        txtToolbarTitle.setTextColor(getResources().getColor(R.color.text_blue));*/
        txtBack.setBackgroundTintList(getResources().getColorStateList(R.color.text_blue));
        lyBack.setOnClickListener(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lyBack:
                finish();
                break;

            case R.id.efbSelectLanguage:
                showLanguages();
                break;

            case R.id.fbEnglish:
                setLocale("en");
                break;

            case R.id.fabHindi:
                setLocale("hi");
                break;

            case R.id.fabUrdhu:
                setLocale("ar");
                break;

        }
    }

    private void setLocale(String language) {
        try {
            Log.e("=>lan_name", "" + language);
            Log.e("=>currentLanguage", "" + currentLanguage);
            if (!language.equals(currentLanguage)) {
                LocaleUtils.setSelectedLanguageId(language);
                Intent i = BlogDetail.this.getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(BlogDetail.this.getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            } else {
//                Toasty.info(Settings.this, "" + getResources().getString(R.string.language_already_selected),
//                        Toasty.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e("error_msg", "" + e.getMessage());
        }
    }

    private void showLanguages() {

        // set visibility
        if (!isAllFabVisible) {
            fbEnglish.setVisibility(View.VISIBLE);
            fabUrdhu.setVisibility(View.VISIBLE);
            fabHindi.setVisibility(View.VISIBLE);
            tvEnglish.setVisibility(View.VISIBLE);
            tvHindi.setVisibility(View.VISIBLE);
            tvUrdhu.setVisibility(View.VISIBLE);
            isAllFabVisible = true;
            efbSelectLanguage.extend();
        } else {
            fbEnglish.setVisibility(View.GONE);
            fabUrdhu.setVisibility(View.GONE);
            fabHindi.setVisibility(View.GONE);
            tvEnglish.setVisibility(View.GONE);
            tvHindi.setVisibility(View.GONE);
            tvUrdhu.setVisibility(View.GONE);
            isAllFabVisible = false;
            efbSelectLanguage.shrink();
        }

        // click listener on floating action buttons
        fbEnglish.setOnClickListener(this);
        fabHindi.setOnClickListener(this);
        fabUrdhu.setOnClickListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btmShare:
                ShareMe();
                break;

            case R.id.btmLike:
                if(item.getIcon().equals(R.drawable.ic_like)){
                    item.setIcon(R.drawable.ic_like_fill);
                }
                else{
                    item.setIcon(R.drawable.ic_like);
                }
                break;

        }
        return true;
    }

    private void ShareMe() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "" + getResources().getString(R.string.app_name));
            String shareMessage = result.getTitle() + "\n\nhttps://app.mysarthi.com/quiz/api/home/get_blog/" + result.getId();
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "" + getResources().getString(R.string.share_with)));
        } catch (Exception e) {
            //e.toString();
        }
    }
}