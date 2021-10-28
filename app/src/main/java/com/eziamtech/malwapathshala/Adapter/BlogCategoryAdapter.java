package com.eziamtech.malwapathshala.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.eziamtech.malwapathshala.Activity.BlogListing;
import com.eziamtech.malwapathshala.Model.Blog.BlogListingModel;
import com.eziamtech.malwapathshala.Model.BlogCategoryModel.Result;
import com.eziamtech.malwapathshala.R;
import com.eziamtech.malwapathshala.Webservice.BaseURL;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlogCategoryAdapter extends RecyclerView.Adapter<BlogCategoryAdapter.SingleBlogCategoryViewHolder> {

    List<Result> data;
    Context context;
    boolean blogAvailable = false;
    List<com.eziamtech.malwapathshala.Model.Blog.Result> categoryBlog;

    public BlogCategoryAdapter(List<Result> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public SingleBlogCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_blog_category, parent, false);
        return new SingleBlogCategoryViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull SingleBlogCategoryViewHolder holder, int position) {

        String categoryName = data.get(position).getName();
        holder.tvCategoryName.setText(categoryName);

        // click listeners
        holder.cvCategory.setOnClickListener(v -> {
            getBlog(data.get(position).getId());

           /* if (blogAvailable) {
                Intent intent = new Intent(context, BlogListing.class);
                intent.putExtra("blogs", (Parcelable) categoryBlog);
                intent.putExtra("category_name",  data.get(position).getName());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
            else Toast.makeText(context, "No blog available for "+categoryName, Toast.LENGTH_SHORT).show();*/

            Intent intent = new Intent(context, BlogListing.class);
            intent.putExtra("cat_id", data.get(position).getId());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    private void getBlog(String id) {
        Call<BlogListingModel> call = BaseURL.getVideoAPI().getBlogListing();
        call.enqueue(new Callback<BlogListingModel>() {
            @Override
            public void onResponse(Call<BlogListingModel> call, Response<BlogListingModel> response) {
                if(response.code() == 200 && response.body().getStatus() == 200) {
                    if(response.body().getResult().size()>0) {
                        blogAvailable = true;
                        categoryBlog = response.body().getResult();
                    }
                }
            }

            @Override
            public void onFailure(Call<BlogListingModel> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class SingleBlogCategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCategoryName;
        private CardView cvCategory;

        public SingleBlogCategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            cvCategory = itemView.findViewById(R.id.cvCategory);
        }
    }
}
