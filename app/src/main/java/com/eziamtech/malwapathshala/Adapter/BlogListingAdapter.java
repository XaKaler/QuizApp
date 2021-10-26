package com.eziamtech.malwapathshala.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.eziamtech.malwapathshala.Activity.BlogComments;
import com.eziamtech.malwapathshala.Activity.BlogDetail;
import com.eziamtech.malwapathshala.Model.Blog.BlogListingModel;
import com.eziamtech.malwapathshala.Model.Blog.Result;
import com.eziamtech.malwapathshala.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class BlogListingAdapter extends RecyclerView.Adapter<BlogListingAdapter.BlogListingViewHolder> {

    List<Result> data;
    List<com.eziamtech.malwapathshala.Model.BlogFeatures.Result> featureData;
    Context context;

    Boolean isSelected = false;
    int likeCount = 0;

    public BlogListingAdapter(List<Result> data, Context context, List<com.eziamtech.malwapathshala.Model.BlogFeatures.Result> featureData) {
        this.data = data;
        this.context = context;
        this.featureData = featureData;
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

        /*for(com.eziamtech.malwapathshala.Model.BlogFeatures.Result i : featureData){
            if(data.get(position).getId().equals(i.getBlogId())){
                holder.tvBlogListLike.setText(i.getLikes());
                holder.tvBlogListComment.setText(i.getWatch());
                holder.tvBlogListShare.setText(i.getShare());
                break;
            }
        }*/

        holder.clSingleBlogList.setOnClickListener(v -> {
            Intent intent = new Intent(context, BlogDetail.class);
            intent.putExtra("blog", data.get(position));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        // when user click on like button

        holder.tvBlogListLike.setOnClickListener(v->{
            if(!isSelected) {
                holder.tvBlogListLike.setSelected(true);
                holder.tvBlogListLike.setText(String.valueOf(likeCount+=1));

                isSelected = true;
            }
            else {
                holder.tvBlogListLike.setSelected(false);
                if(likeCount == 1){
                    holder.tvBlogListLike.setText(context.getString(R.string.like));
                    likeCount -= 1;
                }
                else {
                    holder.tvBlogListLike.setText(String.valueOf(likeCount-=1));
                }
                isSelected = false;
            }
        });

        // when user click on comment button
        holder.tvBlogListComment.setOnClickListener(v->{
            Intent commentIntent = new Intent(context, BlogComments.class);
            commentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(commentIntent);
        });
        /*holder.tvBlogListShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "" + context.getResources().getString(R.string.app_name));
            String shareMessage = data.get(position).getTitle() + "\n\nhttps://app.mysarthi.com/quiz/api/home/get_blog/" + data.get(position).getId();
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
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
        private TextView tvBlogListTitle, tvBlogListShare, tvBlogListComment, tvBlogListLike;
        private ConstraintLayout clSingleBlogList;

        public BlogListingViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBlogList = itemView.findViewById(R.id.imgBlogList);
            tvBlogListTitle = itemView.findViewById(R.id.tvBlogListTitle);
            clSingleBlogList = itemView.findViewById(R.id.clSingleBlogList);
            tvBlogListShare = itemView.findViewById(R.id.tvBlogListShare);
            tvBlogListLike = itemView.findViewById(R.id.tvBlogListLike);
            tvBlogListComment = itemView.findViewById(R.id.tvBlogListComment);
        }
    }
}
