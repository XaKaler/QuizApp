package com.eziamtech.malwapathshala.Adapter;

import android.content.Context;
import android.content.Intent;
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
import com.eziamtech.malwapathshala.Model.Blog.BlogStatusModel;
import com.eziamtech.malwapathshala.Model.Blog.Result;
import com.eziamtech.malwapathshala.Model.BlogFeatures.BlogFeaturesModel;
import com.eziamtech.malwapathshala.R;
import com.eziamtech.malwapathshala.Webservice.BaseURL;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlogListingAdapter extends RecyclerView.Adapter<BlogListingAdapter.BlogListingViewHolder> {

    List<Result> data;
    List<com.eziamtech.malwapathshala.Model.BlogFeatures.Result> featureData;
    Context context;

    Boolean isSelected = false, isLikeAdd = false;
    int likeCount = 0, shareCount = 0, watchCount = 0;
    String lang_id = "3";

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

        // get like/comment/share of current position blog

        Call<BlogFeaturesModel> blogFeatureCall = BaseURL.getVideoAPI().getBlogFeatures();
        blogFeatureCall.enqueue(new Callback<BlogFeaturesModel>() {
            @Override
            public void onResponse(Call<BlogFeaturesModel> call, Response<BlogFeaturesModel> response) {
                if(response.code() == 200 && response.body().getStatus() == 200) {

                    // if like/share/comment is available
                    if(response.body().getResult().size() > 0) {
                        // initialize like/share/watch count
                        likeCount = Integer.parseInt(response.body().getResult().get(0).getLikes());
                        shareCount = Integer.parseInt(response.body().getResult().get(0).getShare());
                        watchCount = Integer.parseInt(response.body().getResult().get(0).getWatch());

                        //set text in like/comment/share
                        holder.tvBlogListLike.setText(response.body().getResult().get(0).getLikes());
                        holder.tvBlogListComment.setText(response.body().getResult().get(0).getWatch());
                        holder.tvBlogListShare.setText(response.body().getResult().get(0).getShare());
                    }
                }
            }

            @Override
            public void onFailure(Call<BlogFeaturesModel> call, Throwable t) {
                t.printStackTrace();
            }
        });

        // set blog title and image
        holder.tvBlogListTitle.setText(data.get(position).getTitle());
        Picasso.get().load(data.get(position).getImage()).into(holder.imgBlogList);

        // if last blog in list then remove line
        if(position == data.size()-1){
            holder.lineView.setVisibility(View.GONE);
        }

        // when click on any blog then show details
        holder.clSingleBlogList.setOnClickListener(v -> {
            Intent intent = new Intent(context, BlogDetail.class);
            intent.putExtra("blog", data.get(position));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        // when user click on like button
        holder.tvBlogListLike.setOnClickListener(v->{

            // first check that user already like or not
            // if user not like yet
            if(!isSelected) {
                //1. Add like in db
                String blog_id = data.get(position).getId();
                String like = String.valueOf(likeCount+1);
                String share = String.valueOf(shareCount+1);
                String watch = String.valueOf(watchCount+1);
                String lang_id = "1";

                Call<BlogStatusModel> blogStatusModelCall = BaseURL.getVideoAPI().updateStatus();
                blogStatusModelCall.enqueue(new Callback<BlogStatusModel>() {
                    @Override
                    public void onResponse(Call<BlogStatusModel> call, Response<BlogStatusModel> response) {
                        // if like update in db successfully
                        if(response.code() == 200 & response.body().getStatus() == 200){
                            // change icon to selected
                            holder.tvBlogListLike.setSelected(true);
                            // change text to like count and increase like count value by 1
                            holder.tvBlogListLike.setText(String.valueOf(likeCount += 1));
                            isSelected = true;
                        }
                    }

                    @Override
                    public void onFailure(Call<BlogStatusModel> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }

            // if user like already
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

    private void updateStatus(String blog_id, String lang_id, String like) {
        Call<BlogStatusModel> blogStatusModelCall = BaseURL.getVideoAPI().updateStatus("1", "2", "3", "1", "2");
        blogStatusModelCall.enqueue(new Callback<BlogStatusModel>() {
            @Override
            public void onResponse(Call<BlogStatusModel> call, Response<BlogStatusModel> response) {
               // Log.d("500", response.body().getMessage());
                if(response.body().getStatus() == 200){
                    isLikeAdd = true;
                }
            }

            @Override
            public void onFailure(Call<BlogStatusModel> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class BlogListingViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgBlogList;
        private TextView tvBlogListTitle, tvBlogListShare, tvBlogListComment, tvBlogListLike;
        private ConstraintLayout clSingleBlogList;
        private View lineView;

        public BlogListingViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBlogList = itemView.findViewById(R.id.imgBlogList);
            tvBlogListTitle = itemView.findViewById(R.id.tvBlogListTitle);
            clSingleBlogList = itemView.findViewById(R.id.clSingleBlogList);
            tvBlogListShare = itemView.findViewById(R.id.tvBlogListShare);
            tvBlogListLike = itemView.findViewById(R.id.tvBlogListLike);
            tvBlogListComment = itemView.findViewById(R.id.tvBlogListComment);
            lineView = itemView.findViewById(R.id.lineView);
        }


    }
}
