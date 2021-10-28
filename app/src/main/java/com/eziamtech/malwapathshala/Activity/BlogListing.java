package com.eziamtech.malwapathshala.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eziamtech.malwapathshala.Adapter.BlogListingAdapter;
import com.eziamtech.malwapathshala.Model.Blog.BlogListingModel;
import com.eziamtech.malwapathshala.Model.Blog.Result;
import com.eziamtech.malwapathshala.Model.BlogFeatures.BlogFeaturesModel;
import com.eziamtech.malwapathshala.R;
import com.eziamtech.malwapathshala.Webservice.BaseURL;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlogListing extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView rvBlogListing;
    TextView txtToolbarTitle, txtBack;
    LinearLayout lyBack, lyToolbar;

    List<Result> blogListingModelResponse;
    List<com.eziamtech.malwapathshala.Model.BlogFeatures.Result> blogFeatureModelResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_listing);

        init();

        txtToolbarTitle.setText("" + getString(R.string.blogs));
        txtToolbarTitle.setTextColor(getResources().getColor(R.color.text_blue));
        txtBack.setBackgroundTintList(getResources().getColorStateList(R.color.text_blue));

        setAdapter();

    }

    private void setAdapter() {
        rvBlogListing.setLayoutManager(new LinearLayoutManager(this));

        /*Intent intent = getIntent();
        List<Result> blogs = (List<Result>) intent.getSerializableExtra("blogs");
        txtToolbarTitle.setText(intent.getStringExtra("category_name");
        BlogListingAdapter adapter = new BlogListingAdapter(blogs, getApplicationContext(), getBlogFeature());
        rvBlogListing.setAdapter(adapter);*/

        // get data of blog listing
        Call<BlogListingModel> call = BaseURL.getVideoAPI().getBlogListing();
        call.enqueue(new Callback<BlogListingModel>() {
            @Override
            public void onResponse(Call<BlogListingModel> call, Response<BlogListingModel> response) {
                assert response.body() != null;
                BlogListingAdapter adapter = new BlogListingAdapter(response.body().getResult(), getApplicationContext());
                rvBlogListing.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<BlogListingModel> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void init() {

        try {
            rvBlogListing = findViewById(R.id.rvBlogListing);
            txtToolbarTitle = findViewById(R.id.txtToolbarTitle);
            txtBack = findViewById(R.id.txtBack);
            lyToolbar = findViewById(R.id.lyToolbar);
            lyToolbar.setVisibility(View.VISIBLE);
            lyBack = findViewById(R.id.lyBack);

            lyBack.setOnClickListener(this);
        } catch (Exception e) {
            Log.e("init Exception ==>", "" + e);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lyBack:
                finish();
                break;
        }
    }
}