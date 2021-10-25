package com.eziamtech.malwapathshala.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.eziamtech.malwapathshala.Activity.BlogListing;
import com.eziamtech.malwapathshala.Model.BlogCategoryModel.Result;
import com.eziamtech.malwapathshala.R;

import java.util.List;

public class SingleBlogCategoryAdapter extends RecyclerView.Adapter<SingleBlogCategoryAdapter.SingleBlogCategoryViewHolder> {

    List<Result> data;
    Context context;

    public SingleBlogCategoryAdapter(List<Result> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public SingleBlogCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_blog_category, parent, false);
        return new SingleBlogCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SingleBlogCategoryViewHolder holder, int position) {
        holder.tvCategoryName.setText(data.get(position).getName());

        // click listeners
        holder.cvCategory.setOnClickListener(v->{
            Intent intent = new Intent(context, BlogListing.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
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
