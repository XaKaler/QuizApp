package com.eziamtech.malwapathshala.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eziamtech.malwapathshala.BuildConfig;
import com.eziamtech.malwapathshala.Model.Blog.Result;
import com.eziamtech.malwapathshala.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class BlogDetail extends AppCompatActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private TextView tvBlogTitle,tvBlogInDetail, txtToolbarTitle, txtBack;
    private ImageView ivBlogImage;
    LinearLayout lyBack, lyToolbar;
    BottomNavigationView bottomNavigationView;
    Result result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_detail);

        init();
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
        switch (view.getId()){
            case R.id.lyBack:
                finish();
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.btmShare:
                ShareMe();
                break;
        }
        return true;
    }

    private void ShareMe() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "" + getResources().getString(R.string.app_name));
            String shareMessage = result.getTitle()+"\n\nhttps://app.mysarthi.com/quiz/api/home/get_blog/"+result.getId();
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "" + getResources().getString(R.string.share_with)));
        } catch (Exception e) {
            //e.toString();
        }
    }
}