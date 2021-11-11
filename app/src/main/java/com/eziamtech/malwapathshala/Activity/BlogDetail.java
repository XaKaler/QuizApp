package com.eziamtech.malwapathshala.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eziamtech.malwapathshala.Model.Blog.BlogStatusModel;
import com.eziamtech.malwapathshala.Model.Blog.Result;
import com.eziamtech.malwapathshala.Model.BlogFeatures.BlogFeaturesModel;
import com.eziamtech.malwapathshala.Model.BlogLanguage.BlogLanguageModel;
import com.eziamtech.malwapathshala.Model.ProfileModel.ProfileModel;
import com.eziamtech.malwapathshala.R;
import com.eziamtech.malwapathshala.Util.Utility;
import com.eziamtech.malwapathshala.Webservice.AppAPI;
import com.eziamtech.malwapathshala.Webservice.BaseURL;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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

    Boolean likeSelected = false;

    long likeCount = 0;
    int commentCount = 0;
    int shareCount = 0;
    int watchCount = 0;
    private String lang_id = "1", blog_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_detail);

        init();

        // get like/share/watch of current blog
        getBlogFeature();

        // set like count
        // if like value is greater than 0 then set like count value
        // otherwise keep it "Like"
        if (likeCount != 0)
            bottomNavigationView.getMenu().getItem(0).setTitle(String.valueOf(likeCount));
    }

    private void getBlogFeature() {
        // get like/comment/share of current blog
        Call<BlogFeaturesModel> blogFeatureCall = BaseURL.getVideoAPI().getBlogFeatures("" + result.getId(), "" + lang_id);
        Log.d("12345", "blog id and lang id is " + result.getId() + " - " + lang_id);
        blogFeatureCall.enqueue(new Callback<BlogFeaturesModel>() {
            @Override
            public void onResponse(Call<BlogFeaturesModel> call, Response<BlogFeaturesModel> response) {
                if (response.code() == 200 && response.body().getStatus() == 200) {
                    Log.d("12345", "blog_success_response" + response.body().getStatus().toString());
                    // if like/share/comment is available
                    if (response.body().getResult().size() > 0) {
                        // initialize like/share/watch count
                        likeCount = Long.parseLong(response.body().getResult().get(0).getLikes());
                        shareCount = Integer.parseInt(response.body().getResult().get(0).getShare());
                        watchCount = Integer.parseInt(response.body().getResult().get(0).getWatch())+1;

                        bottomNavigationView.getMenu().getItem(0).setTitle(String.valueOf(likeCount));
                    }
                }
                Log.d("12345", "blog_feature response = " + response.code() + " " + response.body().getStatus());
            }

            @Override
            public void onFailure(Call<BlogFeaturesModel> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    private void init() {
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
        tvBlogInDetail.setText(stripHtml(result.getDetail()));

        // if no image then hide image view
        if (result.getImage().equals("") || result.getImage() == null)
            ivBlogImage.setVisibility(View.GONE);
        else
            Picasso.get().load(result.getImage()).into(ivBlogImage);

        // initialize blog id for later use
        blog_id = result.getId();

        txtBack.setBackgroundTintList(getResources().getColorStateList(R.color.text_blue));
        lyBack.setOnClickListener(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    // strip or escape html tag
    private String stripHtml(String detail) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(detail, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            return Html.fromHtml(detail).toString();
        }
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
                tvBlogTitle.setText(result.getTitle());
                tvBlogInDetail.setText(stripHtml(result.getDetail()));
                Picasso.get().load(result.getImage()).into(ivBlogImage);
                showAndHideLanguage();
                break;

            case R.id.fabHindi:
                changeBlogLanguage("3", result.getId());
                showAndHideLanguage();
                break;

            case R.id.fabUrdhu:
                changeBlogLanguage("6", result.getId());
                showAndHideLanguage();
                break;

        }
    }

    private void changeBlogLanguage(String languageId, String blogId) {
        Call<BlogLanguageModel> questionLanguageModelCall = BaseURL.getVideoAPI().getChangedLanguageBlog(blogId, languageId);
        questionLanguageModelCall.enqueue(new Callback<BlogLanguageModel>() {
            @Override
            public void onResponse(Call<BlogLanguageModel> call, Response<BlogLanguageModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        if (response.body().getResult().size() > 0) {
                            List<com.eziamtech.malwapathshala.Model.BlogLanguage.Result> responseData = response.body().getResult();

                            // set question image if available otherwise hide image view
                            if (!responseData.get(0).getImage().equalsIgnoreCase(""))
                                Picasso.get().load(responseData.get(0).getImage()).into(ivBlogImage);
                            else
                                ivBlogImage.setVisibility(View.GONE);

                            tvBlogTitle.setText(stripHtml(responseData.get(0).getTitle()));
                            tvBlogInDetail.setText(stripHtml(responseData.get(0).getDetail()));

                            lang_id = responseData.get(0).getLangId();
                        }
                    } else {
                        Toasty.info(getApplicationContext(), "Blog is not available in this language",Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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

              //  Toast.makeText(this, blog_id, Toast.LENGTH_SHORT).show();
                // if user not like yet then
                if (!likeSelected) {

                    Log.e("12345", "blog_id - " + blog_id + "\n" + "like_count - " + likeCount + "\n" + "share_count - " + shareCount + "\n" + "watch_count - " + watchCount + "\n" + "lang_id - " + lang_id);

                    Call<BlogStatusModel> blogStatusModelCall = BaseURL.getVideoAPI().updateStatus("" + blog_id, "" + lang_id, String.valueOf(likeCount+1), String.valueOf(watchCount), String.valueOf(shareCount));
                    blogStatusModelCall.enqueue(new Callback<BlogStatusModel>() {
                        @Override
                        public void onResponse(Call<BlogStatusModel> call, Response<BlogStatusModel> response) {
                            try {
                                // if like update in db successfully
                                if (response.code() == 200 & response.body().getStatus() == 200) {
                                    item.setIcon(R.drawable.ic_like_fill);
                                    likeCount = likeCount+1;
                                    item.setTitle(String.valueOf(likeCount));
                                    likeSelected = true;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("add like exception", e.toString());
                            }
                        }

                        @Override
                        public void onFailure(Call<BlogStatusModel> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                }

                // if user already like
                else {
                    Call<BlogStatusModel> blogStatusModelCall = BaseURL.getVideoAPI()
                            .updateStatus("" + blog_id, "" + lang_id, String.valueOf(likeCount-1), String.valueOf(watchCount), String.valueOf(shareCount));
                    blogStatusModelCall.enqueue(new Callback<BlogStatusModel>() {
                        @Override
                        public void onResponse(Call<BlogStatusModel> call, Response<BlogStatusModel> response) {
                            try {
                                // if like update in db successfully
                                if (response.code() == 200 & response.body().getStatus() == 200) {
                                    item.setIcon(R.drawable.ic_like);
                                    likeCount = likeCount-1;
                                    item.setTitle(String.valueOf(likeCount));
                                    likeSelected = false;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("remove like exception", e.toString());
                            }
                        }

                        @Override
                        public void onFailure(Call<BlogStatusModel> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                }
                break;

            case R.id.btmComment:
                Intent commentIntent = new Intent(this, BlogComments.class);
                commentIntent.putExtra("blog_id", result.getId());
                commentIntent.putExtra("lang_id", lang_id);
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
            String shareMessage = result.getTitle() + "\n\nhttp://malwapathshala.com/api/home/get_blog/" + result.getId();
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "" + getResources().getString(R.string.share_with)));
        } catch (Exception e) {
            //e.toString();
        }
    }
}