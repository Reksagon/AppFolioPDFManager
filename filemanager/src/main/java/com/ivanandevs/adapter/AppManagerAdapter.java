package com.ivanandevs.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ivanandevs.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AppManagerAdapter extends RecyclerView.Adapter<AppManagerAdapter.ViewHolder> {
    private List<AppsBean> appsList;
    private LayoutInflater layoutInflater;

    public AppManagerAdapter(Context context, List<AppsBean> appsList) {
        this.appsList = appsList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public List<AppsBean> getData()
    {
        return appsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.app_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppsBean appsBean = appsList.get(position);
        holder.bind(appsBean);
    }

    @Override
    public int getItemCount() {
        return appsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        TextView tvTime;
        ImageView ivSelect;
        ImageView ivNormal;

        public ViewHolder(@NonNull View itemView ) {
            super(itemView);

        }

        @SuppressLint("NotifyDataSetChanged")
        public void bind(AppsBean appsBean)
        {
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvName = itemView.findViewById(R.id.tv_name);
            tvTime = itemView.findViewById(R.id.tv_time);
            ivSelect = itemView.findViewById(R.id.iv_select);
            ivNormal = itemView.findViewById(R.id.iv_normal);

            ivIcon.setImageDrawable(appsBean.getIcon());
            tvName.setText(appsBean.getName());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
            String installTime = simpleDateFormat.format(new Date(appsBean.getInstallTime()));
            tvTime.setText(installTime);

            ivSelect.setVisibility(appsBean.isSelect() ? View.VISIBLE : View.GONE);
            ivNormal.setVisibility(appsBean.isSelect() ? View.GONE : View.VISIBLE);
            itemView.setOnClickListener(v->
            {
                appsBean.setSelect(!appsBean.isSelect());
                notifyDataSetChanged();
            });
        }
    }
}