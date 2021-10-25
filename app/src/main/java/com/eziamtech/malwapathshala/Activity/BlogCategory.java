package com.eziamtech.malwapathshala.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eziamtech.malwapathshala.Adapter.SingleBlogCategoryAdapter;
import com.eziamtech.malwapathshala.Model.BlogCategoryModel.BlogCategoryModel;
import com.eziamtech.malwapathshala.Model.BlogCategoryModel.Result;
import com.eziamtech.malwapathshala.R;
import com.eziamtech.malwapathshala.Webservice.BaseURL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlogCategory extends AppCompatActivity {

    private RecyclerView rvBlogCategory;
    TextView txtToolbarTitle, txtBack;
    LinearLayout lyBack, lyToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_category);

        init();

        txtToolbarTitle.setText("" + getString(R.string.BlogCategory));
        txtToolbarTitle.setTextColor(getResources().getColor(R.color.text_blue));
        txtBack.setBackgroundTintList(getResources().getColorStateList(R.color.text_blue));

        lyBack.setOnClickListener(v-> finish());
    }

    private void init() {
        lyToolbar = findViewById(R.id.lyToolbar);
        lyToolbar.setVisibility(View.VISIBLE);
        lyBack = findViewById(R.id.lyBack);
        txtToolbarTitle = findViewById(R.id.txtToolbarTitle);
        txtBack = findViewById(R.id.txtBack);
        rvBlogCategory = findViewById(R.id.rvBlogCategory);
        rvBlogCategory.setLayoutManager(new GridLayoutManager(this, 2));


        // get data
        Call<BlogCategoryModel> call = BaseURL.getVideoAPI().getBlogCategory();
        call.enqueue(new Callback<BlogCategoryModel>() {
            @Override
            public void onResponse(Call<BlogCategoryModel> call, Response<BlogCategoryModel> response) {
                SingleBlogCategoryAdapter adapter = new SingleBlogCategoryAdapter(response.body().getResult(), getApplicationContext());
                rvBlogCategory.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<BlogCategoryModel> call, Throwable t) {

            }
        });

    }
}