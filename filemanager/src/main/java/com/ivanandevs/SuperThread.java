package com.ivanandevs;

import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;

import com.ivanandevs.components.RoundedAlertDialog;
import com.ivanandevs.components.ThemeTextView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.HashMap;

public class SuperThread extends AsyncTask<Void, Void, HashMap<String, Object>> {

    public interface ESLThreadListener {
        HashMap<String, Object> onProgress();
        void onEnd(HashMap<String, Object> data);
    }

    private FileManagerActivity activity;
    private RoundedAlertDialog alertDialog;
    private LinearProgressIndicator alertProgressbar;
    private ThemeTextView alertTitleTextView;
    private ThemeTextView alertMessageTextView;
    private ESLThreadListener listener;

    public SuperThread(FileManagerActivity activity) {
        this.activity = activity;
        this.alertDialog = null;
    }

    public SuperThread(FileManagerActivity activity, String title) {
        this.activity = activity;
        activity.setBlurry(true);
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View dialogView = layoutInflater.inflate(R.layout.view_alert_progress, null);
        alertDialog = RoundedAlertDialog.getInstance(dialogView, null);
        alertTitleTextView = dialogView.findViewById(R.id.alertTitleTextView);
        alertMessageTextView = dialogView.findViewById(R.id.alertMessageTextView);
        alertProgressbar = dialogView.findViewById(R.id.alertProgressbar);
        alertDialog.setCancelable(false);
        alertTitleTextView.setText(title);
        alertMessageTextView.setText("Please wait until the processing is complete");
        alertDialog.show(activity.getSupportFragmentManager(), "PROGRESS");
        activity.popupMenuCardView.setVisibility(View.GONE);
        activity.fileClickLayout.setVisibility(View.GONE);
    }

    @Override
    protected HashMap<String, Object> doInBackground(Void... params) {
        return listener.onProgress();
    }

    @Override
    protected void onPostExecute(HashMap<String, Object> data) {
        Utilities.runOnUi(activity, () -> {
            if (alertDialog != null) {
                alertDialog.dismiss();
                activity.hidePopupMenu();
            }
            listener.onEnd(data);
        });
    }

    public void setListener(ESLThreadListener mListener) {
        this.listener = mListener;
    }

    public void setProgress(String message, int progress) {
        if (alertDialog == null) {
            return;
        }
        Utilities.runOnUi(activity, () -> {
            if (alertProgressbar != null && alertMessageTextView != null) {
                alertProgressbar.setProgressCompat(progress, true);
                alertMessageTextView.setText(message);
                Utilities.Loge("xxxxxxxx", message + " " + progress);
            }
        });
    }
}