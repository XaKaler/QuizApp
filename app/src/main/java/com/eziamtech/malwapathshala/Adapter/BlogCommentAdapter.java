package com.eziamtech.malwapathshala.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eziamtech.malwapathshala.Model.BlogComment.BlogCommentModel;
import com.eziamtech.malwapathshala.Model.BlogComment.Result;
import com.eziamtech.malwapathshala.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogCommentAdapter extends RecyclerView.Adapter<BlogCommentAdapter.BlogCommentViewHolder> {

    List<Result> data;
    Context context;


    public BlogCommentAdapter(List<Result> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public BlogCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.wtf("11111111111", "hi hello");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_blog_comment, parent, false);
        return new BlogCommentViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull BlogCommentViewHolder holder, int position) {
        // if last comment then remove bottom line
        if(position == data.size()-1)
            holder.btmLine.setVisibility(View.GONE);

        holder.tvComment.setText(data.get(position).getMessage());
        holder.tvCommentTiming.setText(data.get(position).getCreatedAt());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class BlogCommentViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imgCommenterImage;
        private TextView tvCommenterName;
        private TextView tvComment, tvCommentTiming;
        private View btmLine;

        public BlogCommentViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCommenterName = (TextView) itemView.findViewById(R.id.tvCommenterName);
            tvComment = (TextView) itemView.findViewById(R.id.tvComment);
            tvCommentTiming = itemView.findViewById(R.id.tvCommentTiming);
            btmLine= itemView.findViewById(R.id.btmLine);

        }
    }
}
