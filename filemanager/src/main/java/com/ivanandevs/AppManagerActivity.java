package com.ivanandevs;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ivanandevs.adapter.AppManagerAdapter;
import com.ivanandevs.databinding.ActivityAppManagerBinding;
import com.ivanandevs.adapter.AppsBean;

import java.util.ArrayList;

public class AppManagerActivity extends FragmentActivity {

    public AppManagerAdapter adapter;
    private ActivityAppManagerBinding binding;


    public void deleteOver() {
    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ActivityAppManagerBinding inflate = ActivityAppManagerBinding.inflate(getLayoutInflater());
        this.binding = inflate;
        setContentView((View) inflate.getRoot());

        init();
    }

    private void init() {
        this.binding.ivBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AppManagerActivity.this.finish();
            }
        });
        new ArrayList();
        ArrayList arrayList = new ArrayList();
        for (AppsBean next : FileTool.getAppList(this)) {
            if (!next.isSystem()) {
                AppsBean appsBean = new AppsBean();
                appsBean.setIcon(FileTool.getAppIcon(next.getInfo(), (Context) this));
                appsBean.setName(FileTool.getAppName(next.getInfo(), this));
                appsBean.setPackageName(next.getPackageName());
                appsBean.setInstallTime(next.getInstallTime());
                arrayList.add(appsBean);
            }
        }
        int i = 0;
        this.binding.mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        this.adapter = new AppManagerAdapter(this, arrayList);
        this.binding.mRecyclerView.setAdapter(this.adapter);
//        this.adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
//            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
//                List data = baseQuickAdapter.getData();
//                ((AppsBean) data.get(i)).setSelect(!((AppsBean) data.get(i)).isSelect());
//                baseQuickAdapter.setNewData(data);
//            }
//        });
        TextView textView = this.binding.tvDelete;
        if (arrayList.isEmpty()) {
            i = 8;
        }
        textView.setVisibility(i);
        this.binding.tvDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {
                        for (final AppsBean appsBean : AppManagerActivity.this.adapter.getData()) {
                            if (appsBean.isSelect()) {
                                AppManagerActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        Intent intent = new Intent("android.intent.action.DELETE", Uri.parse("package:" + appsBean.getPackageName()));
                                        intent.putExtra("android.intent.extra.RETURN_RESULT", true);
                                        AppManagerActivity.this.startActivityForResult(intent, 1);
                                    }
                                });
                            }
                        }
                    }
                }).start();
            }
        });
    }


    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 1 && i2 == -1) {
            init();
        }
    }
}
