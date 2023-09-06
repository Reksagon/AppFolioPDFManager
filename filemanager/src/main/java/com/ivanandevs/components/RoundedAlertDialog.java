package com.ivanandevs.components;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ivanandevs.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class RoundedAlertDialog extends DialogFragment {

    public interface AlertDialogListener {
        void show();
        void dismiss();
    }

    private View view;
    private AlertDialogListener listener;

    public static RoundedAlertDialog getInstance(View view, AlertDialogListener listener) {
        return new RoundedAlertDialog(view, listener);
    }

    private RoundedAlertDialog(View view, AlertDialogListener listener) {
        this.view = view;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new MaterialAlertDialogBuilder(requireActivity(), R.style.MaterialAlertDialog_rounded).setView(view).create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (listener != null) listener.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listener != null) listener.show();
    }

    public void dismissSilently() {
        listener = null;
        dismiss();
    }
}
