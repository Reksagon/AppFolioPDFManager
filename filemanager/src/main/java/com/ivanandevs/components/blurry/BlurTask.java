package com.ivanandevs.components.blurry;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlurTask {

  public interface Callback {
    void done(Bitmap bitmap);
  }

  private final WeakReference<Context> contextWeakRef;
  private final BlurFactor factor;
  private final Bitmap bitmap;
  private final Callback callback;
  private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

  public BlurTask(View target, BlurFactor factor, Callback callback) {
    this.factor = factor;
    this.callback = callback;
    this.contextWeakRef = new WeakReference<>(target.getContext());

    target.setDrawingCacheEnabled(true);
    target.destroyDrawingCache();
    target.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
    bitmap = target.getDrawingCache();
  }

  public BlurTask(Context context, Bitmap bitmap, BlurFactor factor, Callback callback) {
    this.factor = factor;
    this.callback = callback;
    this.contextWeakRef = new WeakReference<>(context);

    this.bitmap = bitmap;
  }

  public void execute() {
    THREAD_POOL.execute(new Runnable() {
      @Override
      public void run() {
        Context context = contextWeakRef.get();
        if (callback != null) {
          new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
              callback.done(Blur.of(context, bitmap, factor));
            }
          });
        }
      }
    });
  }
}
