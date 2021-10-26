package com.eziamtech.malwapathshala.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eziamtech.malwapathshala.R;

public class BlogComments extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout lyToolbar;
    private LinearLayout lyBack;
    private TextView txtBack;
    private TextView txtToolbarTitle;
    private EditText edtEnterComment;
    private ImageView imgSend;
    private RecyclerView rvComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_comments);

        init();

        // set edit text focusable and open keyboard
        edtEnterComment.requestFocus();

        // set toolbar
        lyToolbar.setVisibility(View.VISIBLE);
        lyBack.setOnClickListener(this);
    }

    private void init() {
        lyToolbar = (LinearLayout) findViewById(R.id.lyToolbar);
        lyBack = (LinearLayout) findViewById(R.id.lyBack);
        txtBack = (TextView) findViewById(R.id.txtBack);
        txtToolbarTitle = (TextView) findViewById(R.id.txtToolbarTitle);
        rvComments = (RecyclerView) findViewById(R.id.rvComments);
        edtEnterComment = findViewById(R.id.edtEnterComment);
        imgSend = findViewById(R.id.imgSend);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.lyBack:
                finish();
                break;
        }
    }
}