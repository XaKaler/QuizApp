package com.eziamtech.malwapathshala.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.eziamtech.malwapathshala.Activity.BlogDetail;
import com.eziamtech.malwapathshala.Model.Blog.BlogListingModel;
import com.eziamtech.malwapathshala.Model.Blog.Result;
import com.eziamtech.malwapathshala.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BlogListingAdapter extends RecyclerView.Adapter<BlogListingAdapter.BlogListingViewHolder> {

    List<Result> data;
    Context context;

    public BlogListingAdapter(List<Result> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public BlogListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_blog_listing, parent, false);
        return new BlogListingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogListingViewHolder holder, int position) {
        holder.tvBlogListTitle.setText(data.get(position).getTitle());
        Picasso.get().load(data.get(position).getImage()).into(holder.imgBlogList);

        holder.clSingleBlogList.setOnClickListener(v -> {
            Intent intent = new Intent(context, BlogDetail.class);
            intent.putExtra("blog", data.get(position));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        /*holder.tvBlogListShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "" + context.getResources().getString(R.string.app_name));
            String shareMessage = data.get(position).getTitle() + "\n\nhttps://app.mysarthi.com/quiz/api/home/get_blog/" + data.get(position).getId();
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(Intent.createChooser(shareIntent,
                            "" + context.getResources().getString(R.string.share_with)));
        });*/
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class BlogListingViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgBlogList;
        private TextView tvBlogListTitle, tvBlogListShare;
        private ConstraintLayout clSingleBlogList;

        public BlogListingViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBlogList = itemView.findViewById(R.id.imgBlogList);
            tvBlogListTitle = itemView.findViewById(R.id.tvBlogListTitle);
            clSingleBlogList = itemView.findViewById(R.id.clSingleBlogList);
            tvBlogListShare = itemView.findViewById(R.id.tvBlogListShare);
        }
    }
}
