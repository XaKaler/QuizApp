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

    Boolean likeSelected = false, isLikeAdd = false;
    int likeCount = 0, shareCount = 0, watchCount = 0;
    String lang_id = "3", blog_id;

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
        blog_id = data.get(position).getId();

        // set blog title and image
        holder.tvBlogListTitle.setText(data.get(position).getTitle());
        // if no image then hide image view
        if (data.get(position).getImage().equals("") || data.get(position).getImage() == null)
            holder.imgBlogList.setVisibility(View.GONE);
        else
            Picasso.get().load(data.get(position).getImage()).into(holder.imgBlogList);

        // if last blog in list then remove bottom line
        if (position == data.size() - 1) {
            holder.lineView.setVisibility(View.GONE);
        }

        // get like/comment/share of current position blog
        Log.d("9999", "blog_id = " + data.get(position).getId());

        Call<BlogFeaturesModel> blogFeatureCall = BaseURL.getVideoAPI().getBlogFeatures(blog_id,  lang_id);
        blogFeatureCall.enqueue(new Callback<BlogFeaturesModel>() {
            @Override
            public void onResponse(Call<BlogFeaturesModel> call, Response<BlogFeaturesModel> response) {
                if (response.code() == 200 && response.body().getStatus() == 200) {
                    // if like/share/comment is available
                    if (response.body().getResult().size() > 0) {
                        // initialize like/share/watch count
                        likeCount = Integer.parseInt(response.body().getResult().get(0).getLikes());
                        shareCount = Integer.parseInt(response.body().getResult().get(0).getShare());
                        watchCount = Integer.parseInt(response.body().getResult().get(0).getWatch());

                        //set text in like/comment/share
                       // holder.tvBlogListLike.setText(String.valueOf(likeCount));
                       /* holder.tvBlogListComment.setText(String.valueOf());
                        holder.tvBlogListShare.setText(String.valueOf(likeCount));*/
                    }
                }
                Log.d("9999", "blog_feature response = " + response.code() + " " + response.body().getStatus());
            }

            @Override
            public void onFailure(Call<BlogFeaturesModel> call, Throwable t) {
                t.printStackTrace();
            }
        });

        // when click on any blog then show details
        holder.clSingleBlogList.setOnClickListener(v -> {
            Intent intent = new Intent(context, BlogDetail.class);
            intent.putExtra("blog", data.get(position));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        // when user click on like button
        holder.tvBlogListLike.setOnClickListener(v -> {

           // Toast.makeText(context, data.get(position).getId(), Toast.LENGTH_SHORT).show();

            // first check that user already like or not

            // if user not like yet
            if (!holder.tvBlogListLike.isSelected()) {
                //1. Add like in db
                String blog_id = data.get(position).getId();
                String like = String.valueOf(likeCount += 1);
                String share = String.valueOf(shareCount);
                String watch = String.valueOf(watchCount);
                String lang_id = "1";

                Log.d("12345", blog_id + " " + like + " " + share + " " + watch + " " + lang_id);

                Call<BlogStatusModel> blogStatusModelCall = BaseURL.getVideoAPI().updateStatus("" + blog_id, "" + like, "" + share, "" + watch, "" + lang_id);
                blogStatusModelCall.enqueue(new Callback<BlogStatusModel>() {
                    @Override
                    public void onResponse(Call<BlogStatusModel> call, Response<BlogStatusModel> response) {
                        try {
                            // if like update in db successfully
                            if (response.code() == 200 & response.body().getStatus() == 200) {
                                holder.tvBlogListLike.setSelected(!holder.tvBlogListLike.isSelected());
                                //holder.tvBlogListLike.setText(like);
                            }
                            else{
                                Toast.makeText(context, "response is ->"+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            //Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();

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
                //decrease like in db
                String blog_id = data.get(position).getId();
                String like = String.valueOf(likeCount -= 1);
                String share = String.valueOf(shareCount);
                String watch = String.valueOf(watchCount);
                String lang_id = "1";

                Call<BlogStatusModel> blogStatusModelCall = BaseURL.getVideoAPI().updateStatus(blog_id, like, share, watch, lang_id);
                blogStatusModelCall.enqueue(new Callback<BlogStatusModel>() {
                    @Override
                    public void onResponse(Call<BlogStatusModel> call, Response<BlogStatusModel> response) {
                        try {
                            // if like update in db successfully
                            if (response.code() == 200 & response.body().getStatus() == 200) {
                                holder.tvBlogListLike.setSelected(!holder.tvBlogListLike.isSelected());
                               // holder.tvBlogListLike.setText(like);
                            }
                            else{
                                Toast.makeText(context, "response is ->"+response.body().getMessage(), Toast.LENGTH_SHORT).show();
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
        });

        // when user click on comment button
        holder.tvBlogListComment.setOnClickListener(v -> {
            Intent commentIntent = new Intent(context, BlogComments.class);
            commentIntent.putExtra("blog_id", data.get(position).getId());
           // Toast.makeText(context, data.get(position).getId(), Toast.LENGTH_SHORT).show();
            commentIntent.putExtra("lang_id", "1");
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

    static class BlogListingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

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


        @Override
        public void onClick(View view) {

        }
    }
}
