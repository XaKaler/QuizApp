package com.eziamtech.malwapathshala.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eziamtech.malwapathshala.Interface.RecycleViewClickListener;
import com.eziamtech.malwapathshala.Model.language.Result;
import com.eziamtech.malwapathshala.R;

import java.util.List;

public class SelectLanguageAdapter extends RecyclerView.Adapter<SelectLanguageAdapter.SelectLanguageViewHolder> {

    private Context context;
    private List<Result> data;
    RecycleViewClickListener listener;

    public SelectLanguageAdapter(Context context, List<Result> data, RecycleViewClickListener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SelectLanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_language, parent, false);
        return new SelectLanguageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectLanguageViewHolder holder, int position) {
        //holder.rbSingleLanguage.setText(data.get(position).getLanguage());

        holder.bind(data.get(position));
        /*holder.rbSingleLanguage.setOnClickListener(v->{
            dialog.dismiss();
        });*/
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class SelectLanguageViewHolder extends RecyclerView.ViewHolder {

        private final RadioButton rbSingleLanguage;

        public SelectLanguageViewHolder(@NonNull View itemView) {
            super(itemView);
            rbSingleLanguage = itemView.findViewById(R.id.rbSingleLanguage);
        }

        public void bind(Result languages){
            rbSingleLanguage.setText(languages.getLanguage());
            rbSingleLanguage.setOnClickListener(v->{
                listener.recycleOnClick(getBindingAdapterPosition());
            });
        }
    }
}
