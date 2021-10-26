package com.eziamtech.malwapathshala.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eziamtech.malwapathshala.Model.Blog.Result;
import com.eziamtech.malwapathshala.Model.BlogLanguage.BlogLanguageModel;
import com.eziamtech.malwapathshala.R;
import com.eziamtech.malwapathshala.Util.LocaleUtils;
import com.eziamtech.malwapathshala.Util.PrefManager;
import com.eziamtech.malwapathshala.Webservice.BaseURL;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    Boolean likeSelected = false;

    PrefManager prefManager;

    int likeCount = 0, commentCount = 5;

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

        //for temporary use
        bottomNavigationView.setSelectedItemId(R.id.btmShare);

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
                showAndHideLanguage();
                break;

            case R.id.fbEnglish:
                changeQuestionLanguage("2", result.getId());
                showAndHideLanguage();
                break;

            case R.id.fabHindi:
                changeQuestionLanguage("3", result.getId());
                showAndHideLanguage();
                break;

            case R.id.fabUrdhu:
                changeQuestionLanguage("6", result.getId());
                showAndHideLanguage();
                break;

        }
    }

    private void changeQuestionLanguage(String languageId, String blogId) {
        Call<BlogLanguageModel> questionLanguageModelCall = BaseURL.getVideoAPI().getChangedLanguageBlog(blogId, languageId);
        questionLanguageModelCall.enqueue(new Callback<BlogLanguageModel>() {
            @Override
            public void onResponse(Call<BlogLanguageModel> call, Response<BlogLanguageModel> response) {
                if (response.code() == 200 && response.body().getStatus() == 200) {
                    if (response.body().getResult().size() > 0) {
                        List<com.eziamtech.malwapathshala.Model.BlogLanguage.Result> responseData = response.body().getResult();

                        // set question image if available otherwise hide image view
                        if (!responseData.get(0).getImage().equalsIgnoreCase("")) {
                            tvBlogTitle.setText(responseData.get(0).getTitle());
                            Picasso.get().load(responseData.get(0).getImage()).into(ivBlogImage);
                            tvBlogInDetail.setText(responseData.get(0).getDetail());
                        }
                    } else {
                        Toasty.error(getApplicationContext(), "This blog is not available in this language", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toasty.error(getApplicationContext(), "No blog found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<BlogLanguageModel> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void showAndHideLanguage() {

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
                //View item1 = findViewById(R.id.btmComment);
                if (likeSelected) {
                    item.setIcon(R.drawable.ic_like);
                    likeSelected = false;

                    if (likeCount == 1) {
                        item.setTitle("Like");
                    } else {
                        item.setTitle(String.valueOf(likeCount + 1));
                    }
                    likeCount -= 1;

                } else {
                    item.setIcon(R.drawable.ic_like_fill);
                    item.setTitle(String.valueOf(likeCount + 1));
                    likeSelected = true;
                    likeCount += 1;
                }
                break;

            case R.id.btmComment:
                Intent commentIntent = new Intent(this, BlogComments.class);
                startActivity(commentIntent);
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