package com.eziamtech.malwapathshala.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eziamtech.malwapathshala.Adapter.BlogCommentAdapter;
import com.eziamtech.malwapathshala.Model.BlogComment.AddComment;
import com.eziamtech.malwapathshala.Model.BlogComment.BlogCommentModel;
import com.eziamtech.malwapathshala.Model.BlogComment.Result;
import com.eziamtech.malwapathshala.R;
import com.eziamtech.malwapathshala.Webservice.BaseURL;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlogComments extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout lyToolbar;
    private LinearLayout lyBack;
    private TextView txtBack;
    private TextView txtToolbarTitle;
    private EditText edtEnterComment;
    private ImageView imgSend;
    private RecyclerView rvComments;
    private List<Result> commentList;

    String blog_id;
    String lang_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_comments);

        init();
        setAdapter(blog_id, lang_id);

        // set edit text focusable and open keyboard
        edtEnterComment.requestFocus();
    }

    private void setAdapter(String blog_id, String lang_id) {
        rvComments.setLayoutManager(new LinearLayoutManager(this));

        Log.d("11111111111", "hi hello");

        // get comment of particular blog
        Call<BlogCommentModel> call = BaseURL.getVideoAPI().getComments();
        call.enqueue(new Callback<BlogCommentModel>() {
            @Override
            public void onResponse(Call<BlogCommentModel> call, Response<BlogCommentModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        commentList = response.body().getResult();
                        BlogCommentAdapter adapter = new BlogCommentAdapter(response.body().getResult(), getApplicationContext());
                        rvComments.setAdapter(adapter);
                    } else {
                        Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Exception ==>", "" + e);
                }
            }

            @Override
            public void onFailure(Call<BlogCommentModel> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    private void init() {
        commentList = new ArrayList<>();

        lyToolbar = (LinearLayout) findViewById(R.id.lyToolbar);
        lyBack = (LinearLayout) findViewById(R.id.lyBack);
        txtBack = (TextView) findViewById(R.id.txtBack);
        txtToolbarTitle = (TextView) findViewById(R.id.txtToolbarTitle);
        rvComments = (RecyclerView) findViewById(R.id.rvComments);
        edtEnterComment = findViewById(R.id.edtEnterComment);
        imgSend = findViewById(R.id.imgSend);


        // set toolbar
        txtToolbarTitle.setText("" + getString(R.string.comments));
        txtToolbarTitle.setTextColor(getResources().getColor(R.color.text_blue));
        txtBack.setBackgroundTintList(getResources().getColorStateList(R.color.text_blue));
        lyToolbar.setVisibility(View.VISIBLE);

        // click listeners
        imgSend.setOnClickListener(this);
        lyBack.setOnClickListener(this);

        // get blog_id and lang_id from intent
        Intent intent = getIntent();
        blog_id = intent.getStringExtra("blog_id");
        lang_id = intent.getStringExtra("lang_id");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.lyBack:
                finish();
                break;
            case R.id.imgSend:
                addCommentToDb();
                break;
        }
    }

    private void addCommentToDb() {
        String comment = edtEnterComment.getText().toString();
        if(!comment.isEmpty()){
            Call<AddComment> addCommentCall = BaseURL.getVideoAPI().addComment(blog_id, lang_id, comment);
            addCommentCall.enqueue(new Callback<AddComment>() {
                @Override
                public void onResponse(Call<AddComment> call, Response<AddComment> response) {
                    try{
                        if(response.code() == 200 && response.body().getStatus() ==  200){
                            edtEnterComment.setText("");
                            setAdapter(blog_id, lang_id);
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Try again!", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        Log.d("response error ", e.getMessage());
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<AddComment> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
        else
            Toast.makeText(getApplicationContext(), "Enter comment first", Toast.LENGTH_SHORT).show();
    }
}