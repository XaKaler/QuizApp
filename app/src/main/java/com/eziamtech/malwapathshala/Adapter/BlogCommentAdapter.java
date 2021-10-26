package com.eziamtech.malwapathshala.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eziamtech.malwapathshala.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogCommentAdapter extends RecyclerView.Adapter<BlogCommentAdapter.BlogCommentViewHolder> {

    List data;
    Context context;

    public BlogCommentAdapter(List data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public BlogCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_blog_comment, parent, false);
        return new BlogCommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogCommentViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class BlogCommentViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imgCommenterImage;
        private TextView tvCommenterName;
        private TextView tvComment;

        public BlogCommentViewHolder(@NonNull View itemView) {
            super(itemView);

            imgCommenterImage = (CircleImageView) itemView.findViewById(R.id.imgCommenterImage);
            tvCommenterName = (TextView) itemView.findViewById(R.id.tvCommenterName);
            tvComment = (TextView) itemView.findViewById(R.id.tvComment);

        }
    }
}
