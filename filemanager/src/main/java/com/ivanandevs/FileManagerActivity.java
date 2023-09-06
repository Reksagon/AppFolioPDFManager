package com.ivanandevs;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ivanandevs.components.blurry.Blurry;
import com.ivanandevs.components.RoundedAlertDialog;
import com.ivanandevs.components.ThemeEditText;
import com.ivanandevs.components.ThemeTextView;
import com.ivanandevs.databinding.FileManagerActivityBinding;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class FileManagerActivity extends AppCompatActivity {
    public static String BuildConfig_APPLICATION_ID = "";
    private View contentLayout;
    private HorizontalScrollView pathScrollView;
    private ThemeTextView pathTextView;
    private CardView allModeBtnCardView;
    private CardView hideModeBtnCardView;
    private ThemeTextView allBtnTextView;
    private ThemeTextView hideModeBtnTextView;
    private RecyclerView folderRecyclerView;
    private RecyclerView imageRecyclerView;
    private RecyclerView videoRecyclerView;
    private RecyclerView musicRecyclerView;
    private RecyclerView documentRecyclerView;
    private FloatingActionButton fab;
    private ExtendedFloatingActionButton extendedFab;
    private View shadowView;
    private View tabLayout;
    private View tabFolder;
    private ImageView tabFolderImageView;
    private View tabImage;
    private ImageView tabImageImageView;
    private View tabVideo;
    private ImageView tabVideoImageView;
    private View tabMusic;
    private ImageView tabMusicImageView;
    private View tabDocument;
    private ImageView tabDocumentImageView;
    private View popupMenuLayout;
    private ImageView popupMenuShadow;
    public CardView popupMenuCardView;
    private View hideBtn, copyBtn, moveBtn, renameBtn, deleteBtn, infoBtn;
    public View fileClickLayout;
    public ImageView fileThumbnailImageView;
    public ThemeTextView fileNameTextView;
    public ThemeTextView fileInfoTextView;
    public View fileSelectView1;
    public ImageView fileSelectView2;
    public CardView fileDotsCardView;
    public View fileBackgroundView;
    public View fileDividerView;
    public View fileLoadingView;
    private View selectionModLayout;
    private View toolbarModLayout;
    private View selectionModCloseBtn;
    private View selectionModDotsBtn;
    private View selectionModAllBtn;
    private ThemeTextView selectionModTextView;
    private ThemeTextView hideBtnTextView;
    private FolderAdapter folderAdapter;
    private GalleryAdapter imageAdapter;
    private GalleryAdapter videoAdapter;
    private FolderAdapter musicAdapter;
    private FolderAdapter documentAdapter;
    private RoundedAlertDialog permissionAlertDialog;
    private final int OPEN_FILE_REQUEST_CODE = 86420;
    private final int PERMISSION_REQUEST_CODE = 97531;
    private final int PERMISSION_REQUEST_INSTALL = 12021;
    private final int KEYGUARD_REQUEST_ID = 12023;
    public Utilities.Mod currentMod;
    private Utilities.Tab currentTab;
    private Utilities.Sort currentSort = Utilities.Sort.Date;
    private Utilities.Order currentOrder = Utilities.Order.DESC;
    private boolean selectionMod;
    private ArrayList<String> selected = new ArrayList<>();
    private ArrayList<File> copyFiles = new ArrayList<>();
    private boolean moveFile = false;
    private boolean isHideTabLayout = false;
    private String currentAllPath;
    private String currentHidePath;
    private ScaleGestureDetector imageScaleGestureDetector;
    private ScaleGestureDetector videoScaleGestureDetector;

    private static final int VIEW_TYPE_UNKNOWN = 0;
    private static final int VIEW_TYPE_AD = 1;
    private static final int VIEW_TYPE_DATA = 2;
    private FileManagerActivityBinding binding;
    public static Context context;
    public static SettingsController settings;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FileManagerActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utilities.init(this);
        Utilities.makeFile(Utilities.getHidePath());
        refreshStatusBar(!Utilities.isNightTheme());
        setNavigationBarColor(getResources().getColor(R.color.colorWhite), false);


        contentLayout = binding.contentLayout;
        pathScrollView = findViewById(R.id.pathScrollView);
        pathTextView = findViewById(R.id.pathTextView);
        folderRecyclerView = findViewById(R.id.folderRecyclerView);
        imageRecyclerView = findViewById(R.id.imageRecyclerView);
        videoRecyclerView = findViewById(R.id.videoRecyclerView);
        musicRecyclerView = findViewById(R.id.musicRecyclerView);
        documentRecyclerView = findViewById(R.id.documentRecyclerView);
        allModeBtnCardView = findViewById(R.id.allModeBtnCardView);
        hideModeBtnCardView = findViewById(R.id.hideModeBtnCardView);
        allBtnTextView = findViewById(R.id.allBtnTextView);
        hideModeBtnTextView = findViewById(R.id.hideModeBtnTextView);
        popupMenuLayout = findViewById(R.id.popupMenuLayout);
        popupMenuShadow = findViewById(R.id.popupMenuShadow);
        popupMenuCardView = findViewById(R.id.popupMenuCardView);
        hideBtn = findViewById(R.id.hideBtn);
        copyBtn = findViewById(R.id.copyBtn);
        moveBtn = findViewById(R.id.moveBtn);
        renameBtn = findViewById(R.id.renameBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        infoBtn = findViewById(R.id.infoBtn);
        hideBtnTextView = findViewById(R.id.hideBtnTextView);
        shadowView = findViewById(R.id.shadowView);
        tabLayout = findViewById(R.id.tabLayout);
        tabFolder = findViewById(R.id.tabFolder);
        tabFolderImageView = findViewById(R.id.tabFolderImageView);
        tabImage = findViewById(R.id.tabImage);
        tabImageImageView = findViewById(R.id.tabImageImageView);
        tabVideo = findViewById(R.id.tabVideo);
        tabVideoImageView = findViewById(R.id.tabVideoImageView);
        tabMusic = findViewById(R.id.tabMusic);
        tabMusicImageView = findViewById(R.id.tabMusicImageView);
        tabDocument = findViewById(R.id.tabDocument);
        tabDocumentImageView = findViewById(R.id.tabDocumentImageView);
        fab = findViewById(R.id.fab);
        extendedFab = findViewById(R.id.extendedFab);
        fileClickLayout = findViewById(R.id.fileClickLayout);
        fileThumbnailImageView = findViewById(R.id.fileThumbnailImageView);
        fileNameTextView = findViewById(R.id.fileNameTextView);
        fileInfoTextView = findViewById(R.id.fileInfoTextView);
        fileDotsCardView = findViewById(R.id.fileDotsCardView);
        fileBackgroundView = findViewById(R.id.fileBackgroundView);
        fileDividerView = findViewById(R.id.fileDividerView);
        fileLoadingView = findViewById(R.id.fileLoadingView);
        fileSelectView1 = findViewById(R.id.fileSelectView1);
        fileSelectView2 = findViewById(R.id.fileSelectView2);
        selectionModLayout = findViewById(R.id.selectionModLayout);
        toolbarModLayout = findViewById(R.id.toolbarModLayout);
        selectionModDotsBtn = findViewById(R.id.selectionModDotsBtn);
        selectionModAllBtn = findViewById(R.id.selectionModAllBtn);
        selectionModCloseBtn = findViewById(R.id.selectionModCloseBtn);
        selectionModTextView = findViewById(R.id.selectionModTextView);





        hideModeBtnCardView.setOnClickListener(view -> {
            disableSelectionMod();
            if (currentMod == Utilities.Mod.All) {
                setHideMode(Utilities.Mod.Hide);
                updatePathTextView();
            } else {
                updateAdaptersData(Utilities.getHidePath());
            }
        });
        selectionModCloseBtn.setOnClickListener(view -> {
            disableSelectionMod();
        });
        selectionModDotsBtn.setOnClickListener(view -> {
            ArrayList<File> files = new ArrayList<>();
            for (int i = 0; i < selected.size(); i++) {
                files.add(new File(selected.get(i)));
            }
            float x = Utilities.getScreenX() - Utilities.dp(155);
            float y = Utilities.getStatusBarHeight(getApplicationContext()) + Utilities.dp(8);
            showPopupMenu(files, x, y, currentTab);
            fileClickLayout.setVisibility(View.GONE);
        });
        selectionModAllBtn.setOnClickListener(view -> {
            ArrayList<Folder> items = new ArrayList<>();
            if (currentTab == Utilities.Tab.Folder) {
                items = folderAdapter.items;
            } else if (currentTab == Utilities.Tab.Image) {
                items = imageAdapter.items;
            } else if (currentTab == Utilities.Tab.Video) {
                items = videoAdapter.items;
            } else if (currentTab == Utilities.Tab.Music) {
                items = musicAdapter.items;
            } else if (currentTab == Utilities.Tab.Document) {
                items = documentAdapter.items;
            }
            for (int i = 0; i < items.size(); i++) {
                Folder item = items.get(i);
                if (item.type != Folder.Type.Loading && item.type != Folder.Type.Back) {
                    String path = items.get(i).file.getPath();
                    if (!selected.contains(path)) {
                        selected.add(path);
                    }
                }
            }
            updateAdaptersView();
            updateSelectionMode();
        });

        popupMenuLayout.animate().setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                popupMenuLayout.setVisibility(View.VISIBLE);
                popupMenuShadow.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (popupMenuLayout.getAlpha() < 0.5) {
                    popupMenuLayout.setAlpha(0);
                    popupMenuLayout.setVisibility(View.GONE);
                    setBlurry(false);
                } else {
                    popupMenuLayout.setAlpha(1);
                }
            }
        });
        popupMenuShadow.setOnClickListener(view -> {
            hidePopupMenu();
        });

        folderAdapter = new FolderAdapter(Utilities.Tab.Folder);
        folderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        folderRecyclerView.setAdapter(folderAdapter);
        folderRecyclerView.setHasFixedSize(true);
        folderRecyclerView.addOnScrollListener(getOnScrollListener());

        imageAdapter = new GalleryAdapter(Utilities.Tab.Image, "GridImages");
        GridLayoutManager imageLayoutManager = new GridLayoutManager(this, settings.get("GridImages", 4));
        imageLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (imageAdapter.getItemViewType(position)) {
                    case VIEW_TYPE_AD:
                        return imageLayoutManager.getSpanCount();
                    case VIEW_TYPE_DATA:
                    default:
                        return 1;
                }
            }
        });
        imageRecyclerView.setLayoutManager(imageLayoutManager);
        imageRecyclerView.setAdapter(imageAdapter);
        imageRecyclerView.setHasFixedSize(true);
        imageRecyclerView.addOnScrollListener(getOnScrollListener());
        imageScaleGestureDetector = getScaleGestureDetector(imageRecyclerView, "GridImages");
        imageRecyclerView.setOnTouchListener((view, event) -> !imageScaleGestureDetector.onTouchEvent(event));

        videoAdapter = new GalleryAdapter(Utilities.Tab.Video, "GridVideos");
        GridLayoutManager videoLayoutManager = new GridLayoutManager(this, settings.get("GridVideos", 4));
        videoLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (imageAdapter.getItemViewType(position)) {
                    case VIEW_TYPE_AD:
                        return videoLayoutManager.getSpanCount();
                    case VIEW_TYPE_DATA:
                    default:
                        return 1;
                }
            }
        });
        videoRecyclerView.setLayoutManager(videoLayoutManager);
        videoRecyclerView.setAdapter(videoAdapter);
        videoRecyclerView.setHasFixedSize(true);
        videoRecyclerView.addOnScrollListener(getOnScrollListener());
        videoScaleGestureDetector = getScaleGestureDetector(videoRecyclerView, "GridVideos");
        videoRecyclerView.setOnTouchListener((view, event) -> !videoScaleGestureDetector.onTouchEvent(event));

        musicAdapter = new FolderAdapter(Utilities.Tab.Music);
        musicRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        musicRecyclerView.setAdapter(musicAdapter);
        musicRecyclerView.setHasFixedSize(true);
        musicRecyclerView.addOnScrollListener(getOnScrollListener());

        documentAdapter = new FolderAdapter(Utilities.Tab.Document);
        documentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        documentRecyclerView.setAdapter(documentAdapter);
        documentRecyclerView.setHasFixedSize(true);
        documentRecyclerView.addOnScrollListener(getOnScrollListener());

        tabFolder.setOnClickListener(view -> {
            selectTab(Utilities.Tab.Folder);
        });
        tabImage.setOnClickListener(view -> {
            selectTab(Utilities.Tab.Image);
        });
        tabVideo.setOnClickListener(view -> {
            selectTab(Utilities.Tab.Video);
        });
        tabMusic.setOnClickListener(view -> {
            selectTab(Utilities.Tab.Music);
        });
        tabDocument.setOnClickListener(view -> {
            selectTab(Utilities.Tab.Document);
        });
        selectTab(Utilities.Tab.Folder);
        showTabLayout(true);
        fab.setOnClickListener(view -> {
            disableSelectionMod();
            if (copyFiles.size() > 0) {
                setCopyMode(null, false);
            } else {
                setBlurry(true);
                LayoutInflater layoutInflater = LayoutInflater.from(this);
                View dialogView = layoutInflater.inflate(R.layout.view_alert_create, null);
                RoundedAlertDialog alertDialog = RoundedAlertDialog.getInstance(dialogView, new RoundedAlertDialog.AlertDialogListener() {
                    @Override
                    public void show() {

                    }

                    @Override
                    public void dismiss() {
                        hidePopupMenu();
                    }
                });
                ThemeEditText alertEditText = dialogView.findViewById(R.id.alertEditText);
                CardView alertOkBtn = dialogView.findViewById(R.id.alertOkBtn);
                CardView alertCancelBtn = dialogView.findViewById(R.id.alertCancelBtn);
                alertEditText.setText("");
                alertOkBtn.setOnClickListener(view2 -> {
                    String name = "";
                    if (alertEditText.getText() != null) {
                        name = alertEditText.getText().toString();
                    }
                    if (name.length() == 0) {
                        Toast.makeText(getApplicationContext(), "The folder name cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    File file = new File(getCurrentPath(), name);
                    if (file.exists()) {
                        Toast.makeText(getApplicationContext(), "This folder is already exists", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    boolean success = file.mkdir();
                    if (success) {
                        updateAdaptersData(file.getParent());
                    } else {
                        Toast.makeText(getApplicationContext(), "Sorry, folder creation operation failed", Toast.LENGTH_SHORT).show();
                    }
                    alertDialog.dismiss();
                });
                alertCancelBtn.setOnClickListener(view1 -> {
                    alertDialog.dismiss();
                });
                alertDialog.show(getSupportFragmentManager(), "CREATE");
                popupMenuCardView.setVisibility(View.GONE);
                fileClickLayout.setVisibility(View.GONE);
            }
        });
        extendedFab.setOnClickListener(view -> {
            disableSelectionMod();
            SuperThread task = new SuperThread(FileManagerActivity.this, "Copy");
            task.setListener(new SuperThread.ESLThreadListener() {
                @Override
                public HashMap<String, Object> onProgress() {
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("success", true);
                    for (int i = 0; i < copyFiles.size(); i++) {
                        File copyFile = copyFiles.get(i);
                        if (copyFile != null) {
                            String name = copyFile.getName();
                            File to = new File(getCurrentPath(), name);
                            while (to.exists()) {
                                int dot = name.lastIndexOf(".");
                                if (dot > 0) {
                                    name = name.substring(0, dot) + " copy" + name.substring(dot);
                                } else {
                                    name = name + " copy";
                                }
                                to = new File(to.getParent(), name);
                            }
                            int progress = i * 100 / copyFiles.size();
                            task.setProgress("Coping file " + to.getName() + " ...", progress);
                            data.put("path", to.getParent());
                            boolean success = Utilities.copyFile(copyFile.getPath(), to.getParent(), to.getName(), Utilities.Crypto.None);
                            Utilities.broadCastPaths.add(to.getPath());
                            Utilities.broadCastPaths.add(copyFile.getPath());
                            if (success) {
                                if (moveFile) {
                                    success = Utilities.deleteFile(copyFile);
                                }
                                if (!success) {
                                    data.put("success", false);
                                    return data;
                                }
                            }
                        }
                    }
                    return data;
                }

                @Override
                public void onEnd(HashMap<String, Object> data) {
                    String path = "";
                    boolean success = false;
                    if (data != null) {
                        success = (boolean) data.get("success");
                        path = (String) data.get("path");
                    }
                    if (success) {
                        updateAdaptersData(path);
                    } else {
                        Toast.makeText(getApplicationContext(), "Sorry, file copy operation failed", Toast.LENGTH_SHORT).show();
                    }
                    setCopyMode(null, false);
                }
            });
            task.execute();
        });
        setCopyMode(null, false);
        fab.show();

        setHideMode(Utilities.Mod.All);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            checkPermission();
        } else if (requestCode == OPEN_FILE_REQUEST_CODE) {
            Utilities.deleteFile(new File(Utilities.getTempPath()));
        }
    }





    @Override
    protected void onResume() {
        super.onResume();
        askStoragePermission();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }



    private void updatePathTextView() {
        String path = getCurrentPath();
        if (path != null && currentTab == Utilities.Tab.Folder) {
            pathTextView.setText(Utilities.formatPath(path));
        } else {
            pathTextView.setText(currentMod == Utilities.Mod.All ? currentTab.title : currentTab.hideTitle);
        }
    }

    private ScaleGestureDetector getScaleGestureDetector(RecyclerView recyclerView, String key) {
        return new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            private GridLayoutManager layoutManager3 = new GridLayoutManager(getApplicationContext(), 3);
            private GridLayoutManager layoutManager4 = new GridLayoutManager(getApplicationContext(), 4);
            private GridLayoutManager layoutManager5 = new GridLayoutManager(getApplicationContext(), 5);

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                GridLayoutManager currentLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                if (currentLayoutManager == null) {
                    return false;
                }
                int current = (int) detector.getCurrentSpan();
                int previous = (int) detector.getPreviousSpan();
                int sub = current - previous;
                boolean zoomOut = detector.getCurrentSpan() - detector.getPreviousSpan() < -16;
                boolean zoomIn = detector.getCurrentSpan() - detector.getPreviousSpan() > 16;
                //Utilities.Loge("xxxxxxx", " sub:" + sub + " " + (zoomIn ? "in" : zoomOut ? "out" : "none"));
                if (detector.getCurrentSpan() > 200 && detector.getTimeDelta() > 200) {
                    if (zoomOut) {
                        if (currentLayoutManager.getSpanCount() == 3) {
                            recyclerView.setLayoutManager(layoutManager4);
                            settings.set(key, 4);
                            return true;
                        } else if (currentLayoutManager.getSpanCount() == 4 && sub < -64) {
                            recyclerView.setLayoutManager(layoutManager5);
                            settings.set(key, 5);
                            return true;
                        }
                    } else if (zoomIn) {
                        if (currentLayoutManager.getSpanCount() == 5) {
                            recyclerView.setLayoutManager(layoutManager4);
                            settings.set(key, 4);
                            return true;
                        } else if (currentLayoutManager.getSpanCount() == 4 && sub > 64) {
                            recyclerView.setLayoutManager(layoutManager3);
                            settings.set(key, 3);
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }

    private RecyclerView.OnScrollListener getOnScrollListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int scrollX, int scrollY) {
                super.onScrolled(recyclerView, scrollX, scrollY);
                /*if (currentTab == Utilities.Tab.Folder) {
                    fab.setAlpha(1f);
                    extendedFab.setAlpha(copyFiles.size() > 0 ? 1f : 0f);
                } else {
                    fab.setAlpha(0f);
                    extendedFab.setAlpha(0f);
                }*/
                if (scrollY > 0) {
                    hideTabLayout(false);
                } else {
                    showTabLayout(false);
                }
            }
        };
    }

    private void disableSelectionMod() {
        if (selectionMod || selected.size() > 0) {
            selected.clear();
            selectionMod = false;
            updateAdaptersView();
            updateSelectionMode();
        }
    }

    private void updateSelectionMode() {
        if (selectionMod) {
            toolbarModLayout.setVisibility(View.GONE);
            selectionModLayout.setVisibility(View.VISIBLE);
        } else {
            toolbarModLayout.setVisibility(View.VISIBLE);
            selectionModLayout.setVisibility(View.GONE);
        }
        selectionModTextView.setText(selected.size() + "");
    }

    private void selectTab(Utilities.Tab tab) {
        if (currentTab == tab) {
            return;
        }
        currentTab = tab;
        disableSelectionMod();
        int colorMuted = Utilities.getColor(R.color.colorGray);
        int colorActive = Utilities.getColor(R.color.colorAccent);
        tabFolderImageView.setImageTintList(ColorStateList.valueOf(colorMuted));
        tabImageImageView.setImageTintList(ColorStateList.valueOf(colorMuted));
        tabVideoImageView.setImageTintList(ColorStateList.valueOf(colorMuted));
        tabMusicImageView.setImageTintList(ColorStateList.valueOf(colorMuted));
        tabDocumentImageView.setImageTintList(ColorStateList.valueOf(colorMuted));
        folderRecyclerView.setVisibility(View.GONE);
        imageRecyclerView.setVisibility(View.GONE);
        videoRecyclerView.setVisibility(View.GONE);
        musicRecyclerView.setVisibility(View.GONE);
        documentRecyclerView.setVisibility(View.GONE);
        updatePathTextView();
        if (currentTab == Utilities.Tab.Folder) {
            tabFolderImageView.setImageTintList(ColorStateList.valueOf(colorActive));
            folderRecyclerView.setVisibility(View.VISIBLE);
            fab.show();
        } else if (currentTab == Utilities.Tab.Image) {
            tabImageImageView.setImageTintList(ColorStateList.valueOf(colorActive));
            imageRecyclerView.setVisibility(View.VISIBLE);
            fab.hide();
        } else if (currentTab == Utilities.Tab.Video) {
            tabVideoImageView.setImageTintList(ColorStateList.valueOf(colorActive));
            videoRecyclerView.setVisibility(View.VISIBLE);
            fab.hide();
        } else if (currentTab == Utilities.Tab.Music) {
            tabMusicImageView.setImageTintList(ColorStateList.valueOf(colorActive));
            musicRecyclerView.setVisibility(View.VISIBLE);
            fab.hide();
        } else if (currentTab == Utilities.Tab.Document) {
            tabDocumentImageView.setImageTintList(ColorStateList.valueOf(colorActive));
            documentRecyclerView.setVisibility(View.VISIBLE);
            fab.hide();
        }
    }

    private void showTabLayout(boolean force) {
        if (!isHideTabLayout && !force) {
            return;
        }
        isHideTabLayout = false;
        if (currentTab == Utilities.Tab.Folder) {
            fab.show();
            extendedFab.extend();
        }
        tabLayout.animate().translationY(0).setDuration(150);
        shadowView.animate().translationY(Utilities.dp(2)).setDuration(150);
    }

    private void hideTabLayout(boolean force) {
        if (isHideTabLayout && !force) {
            return;
        }
        isHideTabLayout = true;
        fab.hide();
        extendedFab.shrink();
        tabLayout.animate().translationY(-Utilities.dp(42)).setDuration(150);
        shadowView.animate().translationY(-Utilities.dp(42) + Utilities.dp(2)).setDuration(150);
    }

    public void setBlurry(boolean enable) {
        if (enable) {
            popupMenuShadow.setImageDrawable(null);
            popupMenuShadow.setVisibility(View.VISIBLE);
            if (canBlurry()) {
                Blurry.with(this).radius(12).animate(1000).capture(contentLayout).into(popupMenuShadow);
            } else {
                new Handler().postDelayed(() -> {
                    setBlurry(true);
                }, 50);
            }
        } else {
            popupMenuShadow.setVisibility(View.GONE);
        }
    }

    private boolean canBlurry() {
        contentLayout.setDrawingCacheEnabled(true);
        contentLayout.destroyDrawingCache();
        contentLayout.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        Bitmap cache = contentLayout.getDrawingCache();
        return cache != null;
    }

    private void showPopupMenu(ArrayList<File> files, float x, float y, Utilities.Tab tab) {
        setBlurry(true);
        popupMenuCardView.setX(x);
        popupMenuCardView.setY(y);
        popupMenuLayout.setAlpha(0);
        popupMenuLayout.animate().alpha(1).setDuration(150);
        fileClickLayout.setVisibility(files.size() == 1 && tab == Utilities.Tab.Folder ? View.VISIBLE : View.GONE);
        popupMenuCardView.setVisibility(View.VISIBLE);
        hideBtnTextView.setText(currentMod == Utilities.Mod.Hide ? "Unhide it" : "Hide it");
        if (tab == Utilities.Tab.Folder) {
            moveBtn.setVisibility(View.VISIBLE);
            copyBtn.setVisibility(View.VISIBLE);
        } else {
            moveBtn.setVisibility(View.GONE);
            copyBtn.setVisibility(View.GONE);
        }
        if (files.size() == 1) {
            renameBtn.setVisibility(View.VISIBLE);
            infoBtn.setVisibility(View.VISIBLE);
        } else {
            renameBtn.setVisibility(View.GONE);
            infoBtn.setVisibility(View.GONE);
        }
        hideBtn.setOnClickListener(view -> {
            String title = currentMod == Utilities.Mod.Hide ? "Unhide" : "Hide";
            SuperThread task = new SuperThread(FileManagerActivity.this, title);
            task.setListener(new SuperThread.ESLThreadListener() {
                @Override
                public HashMap<String, Object> onProgress() {
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("success", true);
                    for (int i = 0; i < files.size(); i++) {
                        File file = files.get(i);
                        if (file != null) {
                            String newPath = file.getParent();
                            if (newPath != null) {
                                if (currentMod == Utilities.Mod.Hide) {
                                    newPath = newPath.replace(Utilities.getHidePath(), Utilities.getAllPath());
                                } else {
                                    newPath = newPath.replace(Utilities.getAllPath(), Utilities.getHidePath());
                                }
                                int progress = i * 100 / files.size();
                                task.setProgress(title + "ing file " + file.getName() + " ...", progress);
                                //boolean success = Utilities.copyFile(file.getPath(), newPath, file.getName(), currentMod == Utilities.Mod.Hide ? Utilities.Crypto.Decrypt : Utilities.Crypto.Encrypt);
                                boolean success = Utilities.copyFile(file.getPath(), newPath, file.getName(), Utilities.Crypto.None);
                                Utilities.broadCastPaths.add(file.getPath());
                                Utilities.broadCastPaths.add(new File(newPath, file.getName()).getPath());
                                if (success) {
                                    success = Utilities.deleteFile(file);
                                    if (success) {
                                        Utilities.deleteEmptyFolders(file);
                                    }
                                }
                                if (!success) {
                                    data.put("success", false);
                                    return data;
                                }
                            }
                        }
                    }
                    return data;
                }

                @Override
                public void onEnd(HashMap<String, Object> data) {
                    boolean success = false;
                    if (data != null) {
                        success = (boolean) data.get("success");
                    }
                    if (success) {
                        updateAdaptersData(getCurrentPath());
                    } else {
                        Toast.makeText(getApplicationContext(), "Sorry, The hide operation failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            task.execute();
            hidePopupMenu();
            disableSelectionMod();
        });
        copyBtn.setOnClickListener(view -> {
            setCopyMode(files, false);
            hidePopupMenu();
            disableSelectionMod();
        });
        moveBtn.setOnClickListener(view -> {
            setCopyMode(files, true);
            hidePopupMenu();
            disableSelectionMod();
        });
        renameBtn.setOnClickListener(view -> {
            File file = files.get(0);
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View dialogView = layoutInflater.inflate(R.layout.view_alert_rename, null);
            RoundedAlertDialog alertDialog = RoundedAlertDialog.getInstance(dialogView, new RoundedAlertDialog.AlertDialogListener() {
                @Override
                public void show() {

                }

                @Override
                public void dismiss() {
                    hidePopupMenu();
                }
            });
            ThemeEditText alertEditText = dialogView.findViewById(R.id.alertEditText);
            CardView alertOkBtn = dialogView.findViewById(R.id.alertOkBtn);
            CardView alertCancelBtn = dialogView.findViewById(R.id.alertCancelBtn);
            alertEditText.setText(file.getName());
            alertEditText.setSelection(file.getName().length());
            alertOkBtn.setOnClickListener(view2 -> {
                String type = file.isDirectory() ? "folder" : "file";
                String name = "";
                if (alertEditText.getText() != null) {
                    name = alertEditText.getText().toString();
                }
                if (name.length() == 0) {
                    Toast.makeText(getApplicationContext(), "The " + type + " name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                File from = new File(file.getPath());
                File to = new File(from.getParent(), name);
                if (to.exists()) {
                    Toast.makeText(getApplicationContext(), "This " + type + " name is already exists", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean success = Utilities.renameFile(from, name);
                if (success) {
                    updateAdaptersData(from.getParent());
                } else {
                    Toast.makeText(getApplicationContext(), "Sorry, " + type + " rename operation failed", Toast.LENGTH_SHORT).show();
                }
                disableSelectionMod();
                alertDialog.dismiss();
            });
            alertCancelBtn.setOnClickListener(view1 -> {
                alertDialog.dismiss();
            });
            alertDialog.show(getSupportFragmentManager(), "RENAME");
            popupMenuCardView.setVisibility(View.GONE);
            //fileClickLayout.setVisibility(files.size() == 1 && tab == Utilities.Tab.Folder ? View.VISIBLE : View.GONE);
        });
        deleteBtn.setOnClickListener(view -> {
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View dialogView = layoutInflater.inflate(R.layout.view_alert_delete, null);
            RoundedAlertDialog alertDialog = RoundedAlertDialog.getInstance(dialogView, new RoundedAlertDialog.AlertDialogListener() {
                @Override
                public void show() {

                }

                @Override
                public void dismiss() {
                    hidePopupMenu();
                }
            });
            ThemeTextView messageTextView = dialogView.findViewById(R.id.alertMessageTextView);
            CardView alertOkBtn = dialogView.findViewById(R.id.alertOkBtn);
            CardView alertCancelBtn = dialogView.findViewById(R.id.alertCancelBtn);
            String name = files.size() + " Files";
            if (files.size() == 1) {
                name = files.get(0).getName();
            }
            messageTextView.setText("Do you want to delete \"" + name + "\" forever?");
            alertOkBtn.setOnClickListener(view2 -> {
                SuperThread task = new SuperThread(FileManagerActivity.this, "Delete");
                task.setListener(new SuperThread.ESLThreadListener() {
                    @Override
                    public HashMap<String, Object> onProgress() {
                        HashMap<String, Object> data = new HashMap<>();
                        data.put("success", true);
                        for (int i = 0; i < files.size(); i++) {
                            File file = files.get(i);
                            if (file != null) {
                                int progress = i * 100 / files.size();
                                task.setProgress("Deleting file " + file.getName() + " ...", progress);
                                boolean success = Utilities.deleteFile(file);
                                if (!success) {
                                    data.put("success", false);
                                    return data;
                                }
                            }
                        }
                        return data;
                    }

                    @Override
                    public void onEnd(HashMap<String, Object> data) {
                        boolean success = false;
                        if (data != null) {
                            success = (boolean) data.get("success");
                        }
                        if (success) {
                            updateAdaptersData(getCurrentPath());
                        } else {
                            Toast.makeText(getApplicationContext(), "Sorry, file deletion operation failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                task.execute();
                disableSelectionMod();
                alertDialog.dismiss();
            });
            alertCancelBtn.setOnClickListener(view1 -> {
                alertDialog.dismiss();
            });
            alertDialog.show(getSupportFragmentManager(), "DELETE");
            popupMenuCardView.setVisibility(View.GONE);
            //fileClickLayout.setVisibility(files.size() == 1 && tab == Utilities.Tab.Folder ? View.VISIBLE : View.GONE);
        });
        infoBtn.setOnClickListener(view -> {
            if (files.size() != 1) {
                return;
            }
            File file = files.get(0);
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View dialogView = layoutInflater.inflate(R.layout.view_alert_permission, null);
            ThemeTextView alertTitleTextView = dialogView.findViewById(R.id.alertTitleTextView);
            ThemeTextView alertMessageTextView = dialogView.findViewById(R.id.alertMessageTextView);
            alertTitleTextView.setText("Details");
            String size = Utilities.formatFileSize(file.length());
            if (file.isDirectory()) {
                size = Utilities.formatFileSize(Utilities.getFolderSize(file));
            }
            DateFormat dateFormat = new DateFormat();
            CharSequence date = dateFormat.format("yyyy-MM-dd hh:mm:ss a", new Date(file.lastModified()));
            alertMessageTextView.setText(
                    "Name : " + file.getName() + "\n" +
                            "Size : " + size + "\n" +
                            "Type : " + (file.isDirectory() ? "Directory" : "File") + "\n" +
                            "Date : " + date.toString() + "\n" +
                            "Path : " + file.getPath()
            );
            RoundedAlertDialog alertDialog = RoundedAlertDialog.getInstance(dialogView, new RoundedAlertDialog.AlertDialogListener() {
                @Override
                public void show() {

                }

                @Override
                public void dismiss() {
                    hidePopupMenu();
                }
            });
            CardView alertOkBtn = dialogView.findViewById(R.id.alertOkBtn);
            alertOkBtn.setOnClickListener(view2 -> {
                alertDialog.dismiss();
            });
            alertDialog.show(getSupportFragmentManager(), "INFO");
            popupMenuCardView.setVisibility(View.GONE);
        });
    }

    private void setCopyMode(ArrayList<File> files, boolean move) {
        moveFile = move;
        copyFiles.clear();
        if (files != null) {
            copyFiles.addAll(files);
            fab.show();
            extendedFab.show();
            fab.setRotation(0);
            fab.animate().rotation(45).setDuration(250);
        } else {
            extendedFab.hide();
            fab.setRotation(45);
            fab.animate().rotation(0).setDuration(150);
        }
        extendedFab.extend();
        String title = moveFile ? "Move here" : "Paste here";
        if (copyFiles.size() > 1) {
            title += " (" + copyFiles.size() + ")";
        }
        extendedFab.setText(title);
    }

    public void hidePopupMenu() {
        popupMenuLayout.setAlpha(1);
        popupMenuLayout.animate().alpha(0).setDuration(150);
    }

    private void setHideMode(Utilities.Mod mode) {
        currentMod = mode;
        if (currentAllPath == null) {
            currentAllPath = Utilities.getAllPath();
        }
        if (currentHidePath == null) {
            currentHidePath = Utilities.getHidePath();
        }
        if (currentMod == Utilities.Mod.Hide) {
            allModeBtnCardView.setCardBackgroundColor(Utilities.getColor(R.color.colorDividerLight));
            hideModeBtnCardView.setCardBackgroundColor(Utilities.getColor(R.color.colorSecondary));
            allBtnTextView.setTextColor(Utilities.getColor(R.color.colorGrayDark));
            hideModeBtnTextView.setTextColor(Utilities.getColor(R.color.colorIcon));
        } else {
            allModeBtnCardView.setCardBackgroundColor(Utilities.getColor(R.color.colorSecondary));
            hideModeBtnCardView.setCardBackgroundColor(Utilities.getColor(R.color.colorDividerLight));
            allBtnTextView.setTextColor(Utilities.getColor(R.color.colorIcon));
            hideModeBtnTextView.setTextColor(Utilities.getColor(R.color.colorGrayDark));
        }
        updateAdaptersData(getCurrentPath());
    }

    private void setCurrentPath(String path) {
        if (currentMod == Utilities.Mod.Hide) {
            currentHidePath = path;
        } else {
            currentAllPath = path;
        }
    }

    private String getCurrentPath() {
        return currentMod == Utilities.Mod.Hide ? currentHidePath : currentAllPath;
    }

    private void goToBack() {
        if (selectionMod) {
            disableSelectionMod();
            return;
        }
        String path = getCurrentPath();
        if (path.equals(Utilities.getAllPath()) || path.equals(Utilities.getHidePath())) {
            finish();
            return;
        }
        int pos = path.lastIndexOf("/");
        if (pos > 0) {
            String lastPath = path.substring(0, pos);
            goToPath(lastPath);
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        goToBack();
    }

    public boolean openFile(File file, boolean hidden) {
        if (file.getName().endsWith(".apk") && !settings.get("RequestInstallPackages", false)) {
            File _file = file;
            boolean _hidden = hidden;
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View dialogView = layoutInflater.inflate(R.layout.view_alert_permission_2, null);
            RoundedAlertDialog alertDialog = RoundedAlertDialog.getInstance(dialogView, new RoundedAlertDialog.AlertDialogListener() {
                @Override
                public void show() {
                    setBlurry(true);
                }

                @Override
                public void dismiss() {
                    hidePopupMenu();
                    setBlurry(false);
                }
            });
            CardView alertOkBtn = dialogView.findViewById(R.id.alertOkBtn);
            CardView alertCancelBtn = dialogView.findViewById(R.id.alertCancelBtn);
            CardView alertPolicyBtn = dialogView.findViewById(R.id.alertPolicyBtn);
            alertOkBtn.setOnClickListener(view2 -> {
                alertDialog.dismiss();
                settings.set("RequestInstallPackages", true);
                openFile(_file, _hidden);
            });
            alertCancelBtn.setOnClickListener(view -> {
                alertDialog.dismiss();
            });
            alertPolicyBtn.setOnClickListener(view -> {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://pages.flycricket.io/hideit/privacy.html")));
            });
            alertDialog.show(getSupportFragmentManager(), "PERMISSION");
            popupMenuCardView.setVisibility(View.GONE);
            fileClickLayout.setVisibility(View.GONE);
            return false;
        }
        Utilities.Loge("xxxxxxx", "path: " + file.getPath());
        hidden = false;
        if (file != null && file.exists()) {
            boolean success = true;
            String tempPath = file.getParent();
            if (hidden) {
                tempPath = Utilities.getTempPath();
                success = Utilities.copyFile(file.getPath(), tempPath, file.getName(), Utilities.Crypto.None);
                Utilities.broadCastPaths.add(file.getPath());
                Utilities.broadCastPaths.add(new File(tempPath, file.getName()).getPath());
            }
            if (success) {
                file = new File(tempPath, file.getName());
                if (hidden) {
                    success = Utilities.decrypt(file);
                }
                if (success) {
                    String realMimeType = null;
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    MimeTypeMap myMime = MimeTypeMap.getSingleton();
                    int idx = file.getName().lastIndexOf('.');
                    if (idx != -1) {
                        String ext = file.getName().substring(idx + 1);
                        realMimeType = myMime.getMimeTypeFromExtension(ext.toLowerCase());
                        if (realMimeType == null) {
                            realMimeType = getMimeType(Uri.fromFile(file));
                            if (realMimeType == null || realMimeType.length() == 0) {
                                realMimeType = null;
                            }
                        }
                    }
                    if (realMimeType != null && realMimeType.equals("application/vnd.android.package-archive")) {
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uriFromFile(file), "application/vnd.android.package-archive");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        try {
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        intent.setDataAndType(FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", file), realMimeType != null ? realMimeType : "text/plain");
                    } else {
                        intent.setDataAndType(Uri.fromFile(file), realMimeType != null ? realMimeType : "text/plain");
                    }
                    if (realMimeType != null) {
                        try {
                            startActivityForResult(intent, OPEN_FILE_REQUEST_CODE);
                        } catch (Exception e) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                intent.setDataAndType(FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", file), "text/plain");
                            } else {
                                intent.setDataAndType(Uri.fromFile(file), "text/plain");
                            }
                            startActivityForResult(intent, OPEN_FILE_REQUEST_CODE);
                        }
                    } else {
                        startActivityForResult(intent, OPEN_FILE_REQUEST_CODE);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private Uri uriFromFile(File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(this, BuildConfig_APPLICATION_ID + ".provider", file);
        } else {
            return Uri.fromFile(file);
        }
    }

    public String getMimeType(Uri uri) {
        String mimeType;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            ContentResolver cr = getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    public class FolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Utilities.Tab tab;
        private ArrayList<Folder> items = new ArrayList<>();
        private int id = 0;

        public FolderAdapter(Utilities.Tab tab) {
            this.tab = tab;
            update();
        }

        public void update() {
            if (tab != Utilities.Tab.Folder) {
                update("");
            }
        }

        public void update(String path) {
            if (tab == Utilities.Tab.Folder) {
                setLoading(path);
                setData(path);
            } else {
                setLoading();
                setData();
            }
        }

        public void setLoading() {
            if (tab != Utilities.Tab.Folder) {
                setLoading("");
            }
        }

        public void setLoading(String path) {
            if (tab == Utilities.Tab.Folder) {
                clearItems();
                setCurrentPath(path);
                for (int i = 0; i < 10; i++) {
                    items.add(Folder.getInstance(null, Folder.Type.Loading));
                }
                notifyDataSetChanged();
            } else {
                clearItems();
                for (int i = 0; i < 10; i++) {
                    items.add(Folder.getInstance(null, Folder.Type.Loading));
                }
                notifyDataSetChanged();
            }
            pathScrollView.scrollTo(pathScrollView.getMaxScrollAmount() * 2, 0);
            Utilities.runOnUi(FileManagerActivity.this, () -> {
                pathScrollView.scrollTo(pathScrollView.getMaxScrollAmount() * 2, 0);
            }, 100);
        }

        public void setData() {
            if (tab != Utilities.Tab.Folder) {
                setData("");
            }
        }

        public void setData(String path) {
            int mId = ++id;
            if (tab == Utilities.Tab.Folder) {
                SuperThread task = new SuperThread(FileManagerActivity.this);
                task.setListener(new SuperThread.ESLThreadListener() {
                    @Override
                    public HashMap<String, Object> onProgress() {
                        ArrayList<Folder> mDirs = new ArrayList<>();
                        ArrayList<Folder> mFiles = new ArrayList<>();
                        File[] list = new File(path).listFiles();
                        if (list != null) {
                            for (File file : list) {
                                if (file.getName().startsWith(".")) {
                                    //continue;
                                }
                                if (currentMod == Utilities.Mod.All && file.getPath().startsWith(Utilities.getHidePath())) {
                                    continue;
                                }
                                if (file.isDirectory()) {
                                    mDirs.add(Folder.getInstance(file, Folder.Type.Directory));
                                } else {
                                    mFiles.add(Folder.getInstance(file, Folder.Type.File));
                                }
                            }
                        }
                        Collections.sort(mDirs, new Comparator<Folder>() {
                            @Override
                            public int compare(Folder item1, Folder item2) {
                                return item1.file.getName().toLowerCase().compareTo(item2.file.getName().toLowerCase());
                            }
                        });
                        Collections.sort(mFiles, new Comparator<Folder>() {
                            @Override
                            public int compare(Folder item1, Folder item2) {
                                return item1.file.getName().toLowerCase().compareTo(item2.file.getName().toLowerCase());
                            }
                        });
                        HashMap<String, Object> data = new HashMap<>();
                        data.put("dirs", mDirs);
                        data.put("files", mFiles);
                        return data;
                    }

                    @Override
                    public void onEnd(HashMap<String, Object> data) {
                        if (mId == id) {
                            clearItems();
                            if (!path.equals(Utilities.getAllPath()) && !path.equals(Utilities.getHidePath())) {
                                items.add(Folder.getInstance(null, Folder.Type.Back));
                            }
                            ArrayList<Folder> mDirs = (ArrayList<Folder>) data.get("dirs");
                            ArrayList<Folder> mFiles = (ArrayList<Folder>) data.get("files");
                            if (mDirs != null) items.addAll(mDirs);
                            if (mFiles != null) items.addAll(mFiles);
                            notifyDataSetChanged();
                        }
                    }
                });
                task.execute();
            } else {
                new Thread(() -> {
                    ArrayList<Folder> files = new ArrayList<>(getFileList(tab));
                    if (mId == id) {
                        runOnUiThread(() -> {
                            clearItems();
                            items.addAll(files);
                            notifyDataSetChanged();
                        });
                    }
                }).start();
            }
        }

        private void clearItems() {
            items.clear();
        }




        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new DataViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_folder, parent, false));
        }

        @Override
        public int getItemViewType(int position) {
            if (items.size() <= position) {
                return VIEW_TYPE_UNKNOWN;
            }
            return VIEW_TYPE_DATA;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder mHolder, int position) {
            if (getItemViewType(position) == VIEW_TYPE_AD) {
                AdViewHolder holder = (AdViewHolder) mHolder;
                ViewGroup adCardView = (ViewGroup) holder.itemView;
                if (adCardView.getChildCount() > 0) {
                    adCardView.removeAllViews();
                }

            } else if (getItemViewType(position) == VIEW_TYPE_DATA) {
                String tag = "ID" + position;
                DataViewHolder holder = (DataViewHolder) mHolder;
                holder.fileThumbnailImageView.setTag(tag);
                Folder item = items.get(position);
                holder.fileThumbnailImageView.setPadding(Utilities.dp(8), Utilities.dp(8), Utilities.dp(8), Utilities.dp(8));
                fileDividerView.setVisibility(View.VISIBLE);
                if (item.type == Folder.Type.Loading) {
                    holder.fileLoadingView.setVisibility(View.VISIBLE);
                    holder.fileDotsCardView.setVisibility(View.GONE);
                    holder.fileThumbnailImageView.setVisibility(View.GONE);
                    holder.fileNameTextView.setText("");
                    holder.fileInfoTextView.setText("");
                    holder.fileClickLayout.setClickable(false);
                    holder.fileClickLayout.setFocusable(false);
                } else if (item.type == Folder.Type.Back) {
                    holder.fileLoadingView.setVisibility(View.GONE);
                    holder.fileDotsCardView.setVisibility(View.GONE);
                    holder.fileThumbnailImageView.setVisibility(View.VISIBLE);
                    holder.fileThumbnailImageView.setImageDrawable(Utilities.getDrawable(R.drawable.ic_back));
                    holder.fileThumbnailImageView.setImageTintList(ColorStateList.valueOf(Utilities.getColor(R.color.colorGray)));
                    holder.fileNameTextView.setText("Back");
                    holder.fileInfoTextView.setText("Back to previous folder");
                    holder.fileClickLayout.setClickable(true);
                    holder.fileClickLayout.setFocusable(true);
                } else {
                    int count = 0;
                    File[] list = item.file.listFiles();
                    if (list != null) {
                        count = list.length;
                    }
                    Date lastModified = new Date(item.file.lastModified());
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String formatDate = formatter.format(lastModified);
                    if (item.type == Folder.Type.Directory) {
                        holder.fileInfoTextView.setText(Utilities.formatFileSize(item.file.length()) + "  " + count + "  " + formatDate);
                        if (count == 0) {
                            holder.fileThumbnailImageView.setImageDrawable(Utilities.getDrawable(R.drawable.ic_folder_empty));
                        } else {
                            holder.fileThumbnailImageView.setImageDrawable(Utilities.getDrawable(R.drawable.ic_folder));
                        }
                    } else {
                        holder.fileInfoTextView.setText(Utilities.formatFileSize(item.file.length()) + "  " + formatDate);
                        if (Utilities.isExtensionPhoto(item.file.getName())) {
                            holder.fileThumbnailImageView.setImageDrawable(Utilities.getDrawable(R.drawable.ic_file));
                            new Thread(() -> {
                                Bitmap bitmap = Utilities.getThumbnailPhoto(getApplicationContext(), item.file);
                                if (bitmap != null) {
                                    runOnUiThread(() -> {
                                        if (holder.fileThumbnailImageView.getTag().equals(tag)) {
                                            holder.fileThumbnailImageView.setImageBitmap(Utilities.getCircularBitmap(bitmap));
                                            holder.fileThumbnailImageView.setPadding(0, 0, 0, 0);
                                        }
                                    });
                                }
                            }).start();
                        } else if (item.file.getName().toLowerCase().endsWith(".apk")) {
                            holder.fileThumbnailImageView.setImageDrawable(Utilities.getDrawable(R.drawable.ic_file));
                            new Thread(() -> {
                                Bitmap bitmap = Utilities.getThumbnailApp(getApplicationContext(), item.file);
                                if (bitmap != null) {
                                    runOnUiThread(() -> {
                                        if (holder.fileThumbnailImageView.getTag().equals(tag)) {
                                            holder.fileThumbnailImageView.setImageBitmap(Utilities.getCircularBitmap(bitmap));
                                            holder.fileThumbnailImageView.setPadding(0, 0, 0, 0);
                                        }
                                    });
                                }
                            }).start();
                        } else {
                            if (Utilities.isExtensionMusic(item.file.getName())) {
                                holder.fileThumbnailImageView.setImageDrawable(Utilities.getDrawable(R.drawable.ic_music));
                            } else if (Utilities.isExtensionVideo(item.file.getName())) {
                                holder.fileThumbnailImageView.setImageDrawable(Utilities.getDrawable(R.drawable.ic_movie));
                            } else if (Utilities.isExtensionWord(item.file.getName())) {
                                holder.fileThumbnailImageView.setImageDrawable(Utilities.getDrawable(R.drawable.ic_word));
                            } else if (Utilities.isExtensionPowerpoint(item.file.getName())) {
                                holder.fileThumbnailImageView.setImageDrawable(Utilities.getDrawable(R.drawable.ic_powerpoint));
                            } else if (Utilities.isExtensionExcel(item.file.getName())) {
                                holder.fileThumbnailImageView.setImageDrawable(Utilities.getDrawable(R.drawable.ic_excel));
                            } else if (Utilities.isExtensionZip(item.file.getName())) {
                                holder.fileThumbnailImageView.setImageDrawable(Utilities.getDrawable(R.drawable.ic_zip));
                            } else if (item.file.getName().toLowerCase().endsWith(".pdf")) {
                                holder.fileThumbnailImageView.setImageDrawable(Utilities.getDrawable(R.drawable.ic_pdf));
                            } else {
                                holder.fileThumbnailImageView.setImageDrawable(Utilities.getDrawable(R.drawable.ic_file));
                            }
                            new Thread(() -> {
                                Bitmap bitmap = Utilities.getThumbnailVideo(item.file);
                                if (bitmap != null) {
                                    runOnUiThread(() -> {
                                        if (holder.fileThumbnailImageView.getTag().equals(tag)) {
                                            holder.fileThumbnailImageView.setImageBitmap(Utilities.getCircularBitmap(bitmap));
                                            holder.fileThumbnailImageView.setPadding(0, 0, 0, 0);
                                        }
                                    });
                                }
                            }).start();
                        }
                    }
                    holder.fileThumbnailImageView.setImageTintList(null);
                    holder.fileThumbnailImageView.setVisibility(View.VISIBLE);
                    holder.fileLoadingView.setVisibility(View.GONE);
                    holder.fileDotsCardView.setVisibility(View.VISIBLE);
                    holder.fileNameTextView.setText(item.file.getName());
                    holder.fileClickLayout.setClickable(true);
                    holder.fileClickLayout.setFocusable(true);
                    int screenX = getResources().getDisplayMetrics().widthPixels;
                    int screenY = getResources().getDisplayMetrics().heightPixels;
                    holder.fileDotsCardView.setOnClickListener(view -> {
                        if (selectionMod) {
                            holder.fileClickLayout.performClick();
                            return;
                        }
                        float extra = Utilities.getStatusBarHeight(getApplicationContext()) / 2f;
                        float x = screenX - Utilities.dp(155);
                        float y = holder.fileClickLayout.getY() + Utilities.dp(68) + extra;
                        if (y > screenY - Utilities.dp(tab == Utilities.Tab.Folder ? 260 : 130)) {
                            y = screenY - Utilities.dp(tab == Utilities.Tab.Folder ? 260 : 130);
                        }
                        ArrayList<File> files = new ArrayList<>();
                        files.add(item.file);
                        showPopupMenu(files, x, y, tab);
                        fileSelectView1.setVisibility(holder.fileSelectView1.getVisibility());
                        fileSelectView2.setVisibility(holder.fileSelectView2.getVisibility());
                        fileSelectView2.setImageDrawable(holder.fileSelectView2.getDrawable());
                        fileSelectView2.setImageTintList(holder.fileSelectView2.getBackgroundTintList());
                        fileDotsCardView.setCardBackgroundColor(holder.fileDotsCardView.getCardBackgroundColor());
                        fileBackgroundView.setBackground(holder.fileBackgroundView.getBackground());
                        fileSelectView1.setBackground(holder.fileSelectView1.getBackground());
                        fileThumbnailImageView.setImageDrawable(holder.fileThumbnailImageView.getDrawable());
                        fileThumbnailImageView.setPadding(holder.fileThumbnailImageView.getPaddingLeft(), holder.fileThumbnailImageView.getPaddingTop(), holder.fileThumbnailImageView.getPaddingRight(), holder.fileThumbnailImageView.getPaddingBottom());
                        fileNameTextView.setText(holder.fileNameTextView.getText());
                        fileInfoTextView.setText(holder.fileInfoTextView.getText());
                        fileDotsCardView.setClickable(false);
                        fileDotsCardView.setFocusable(false);
                        fileDividerView.setVisibility(View.GONE);
                        fileLoadingView.setVisibility(View.GONE);
                        fileClickLayout.setTranslationY(holder.fileClickLayout.getY() + Utilities.dp(68) + extra);
                        fileClickLayout.setClickable(false);
                        fileClickLayout.setFocusable(false);
                    });
                }

                holder.fileClickLayout.setOnClickListener(view -> {
                    if (item.type == Folder.Type.Loading) {
                        return;
                    }
                    if (item.type == Folder.Type.Back) {
                        goToBack();
                    } else if (selectionMod) {
                        if (selected.contains(item.file.getPath())) {
                            selected.remove(item.file.getPath());
                        } else {
                            selected.add(item.file.getPath());
                        }
                        boolean oldSelectionMod = selectionMod;
                        selectionMod = selected.size() > 0;
                        if (oldSelectionMod != selectionMod) {
                            notifyDataSetChanged();
                        } else {
                            notifyItemChanged(position);
                        }
                        updateSelectionMode();
                    } else if (item.type == Folder.Type.Directory) {
                        goToPath(item.file.getPath());
                    } else {
                        openFile(item.file, currentMod == Utilities.Mod.Hide);
                    }
                });
                holder.fileClickLayout.setOnLongClickListener((View.OnLongClickListener) view -> {
                    if (item.type == Folder.Type.Loading || item.type == Folder.Type.Back) {
                        return false;
                    }
                    if (selected.contains(item.file.getPath())) {
                        selected.remove(item.file.getPath());
                    } else {
                        selected.add(item.file.getPath());
                    }
                    boolean oldSelectionMod = selectionMod;
                    selectionMod = selected.size() > 0;
                    if (oldSelectionMod != selectionMod) {
                        notifyDataSetChanged();
                    } else {
                        notifyItemChanged(position);
                    }
                    updateSelectionMode();
                    return true;
                });
                if (selectionMod) {
                    holder.fileSelectView1.setVisibility(View.VISIBLE);
                    if (item.file != null && selected.contains(item.file.getPath())) {
                        holder.fileBackgroundView.setBackgroundColor(Utilities.getColor(R.color.colorSelected));
                        holder.fileSelectView1.setBackgroundColor(Utilities.getColor(R.color.colorSelected));
                        holder.fileDotsCardView.setCardBackgroundColor(Utilities.getColor(R.color.colorSelected));
                        holder.fileSelectView2.setImageDrawable(Utilities.getDrawable(R.drawable.ic_select_on));
                        holder.fileSelectView2.setImageTintList(ColorStateList.valueOf(Utilities.getColor(R.color.colorGrayDark)));
                    } else {
                        holder.fileBackgroundView.setBackgroundColor(Utilities.getColor(R.color.colorBackground));
                        holder.fileSelectView1.setBackgroundColor(Utilities.getColor(R.color.colorBackground));
                        holder.fileDotsCardView.setCardBackgroundColor(Utilities.getColor(R.color.colorBackground));
                        holder.fileSelectView2.setImageDrawable(Utilities.getDrawable(R.drawable.ic_select_off));
                        holder.fileSelectView2.setImageTintList(ColorStateList.valueOf(Utilities.getColor(R.color.colorGray)));
                    }
                } else {
                    holder.fileSelectView1.setVisibility(View.GONE);
                    holder.fileBackgroundView.setBackgroundColor(Utilities.getColor(R.color.colorBackground));
                    holder.fileDotsCardView.setCardBackgroundColor(Utilities.getColor(R.color.colorBackground));
                }
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class AdViewHolder extends RecyclerView.ViewHolder {

            AdViewHolder(View itemView) {
                super(itemView);

            }
        }

        public class DataViewHolder extends RecyclerView.ViewHolder {
            public View fileClickLayout;
            public ImageView fileThumbnailImageView;
            public ThemeTextView fileNameTextView;
            public ThemeTextView fileInfoTextView;
            public CardView fileDotsCardView;
            public View fileLoadingView;
            public View fileSelectView1;
            public ImageView fileSelectView2;
            public View fileBackgroundView;

            DataViewHolder(View itemView) {
                super(itemView);
                fileClickLayout = itemView.findViewById(R.id.fileClickLayout);
                fileThumbnailImageView = itemView.findViewById(R.id.fileThumbnailImageView);
                fileNameTextView = itemView.findViewById(R.id.fileNameTextView);
                fileInfoTextView = itemView.findViewById(R.id.fileInfoTextView);
                fileDotsCardView = itemView.findViewById(R.id.fileDotsCardView);
                fileLoadingView = itemView.findViewById(R.id.fileLoadingView);
                fileSelectView1 = itemView.findViewById(R.id.fileSelectView1);
                fileSelectView2 = itemView.findViewById(R.id.fileSelectView2);
                fileBackgroundView = itemView.findViewById(R.id.fileBackgroundView);
            }
        }
    }

    public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Utilities.Tab tab;
        private String key;
        private ArrayList<Folder> items = new ArrayList<>();
        private int id = 0;

        public GalleryAdapter(Utilities.Tab tab, String key) {
            this.tab = tab;
            this.key = key;
            update();
        }

        public void update() {
            setLoading();
            setData();
        }

        public void setLoading() {
            clearItems();
            int col = settings.get("GridImages", 4);
            for (int i = 0; i < col * 10; i++) {
                items.add(Folder.getInstance(null, Folder.Type.Loading));
            }
            notifyDataSetChanged();
        }

        public void setData() {
            int mId = ++id;
            new Thread(() -> {
                ArrayList<Folder> files = new ArrayList<>(getFileList(tab));
                if (mId == id) {
                    runOnUiThread(() -> {
                        clearItems();
                        items.addAll(files);
                        notifyDataSetChanged();
                    });
                }
            }).start();
        }

        private void clearItems() {
            items.clear();
        }





        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new DataViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_gallery, parent, false));

        }

        @Override
        public int getItemViewType(int position) {
            if (items.size() <= position) {
                return VIEW_TYPE_UNKNOWN;
            }
            return VIEW_TYPE_DATA;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder mHolder, int position) {
            String tag = "ID" + position;
            if (getItemViewType(position) == VIEW_TYPE_AD) {
                AdViewHolder holder = (AdViewHolder) mHolder;
                ViewGroup adCardView = (ViewGroup) holder.itemView;
                if (adCardView.getChildCount() > 0) {
                    adCardView.removeAllViews();
                }

            } else if (getItemViewType(position) == VIEW_TYPE_DATA) {
                DataViewHolder holder = (DataViewHolder) mHolder;
                if (items.size() <= position) {
                    holder.galleryShadowView.setVisibility(View.GONE);
                    holder.galleryPlayImageView.setVisibility(View.GONE);
                    holder.galleryClickLayout.setClickable(false);
                    holder.galleryClickLayout.setFocusable(false);
                    return;
                }
                Folder item = items.get(position);
                holder.galleryThumbnailImageView.setTag(tag);
                holder.galleryThumbnailImageView.setAlpha(0f);
                if (item.type == Folder.Type.Loading) {
                    holder.galleryShadowView.setVisibility(View.GONE);
                    holder.galleryPlayImageView.setVisibility(View.GONE);
                    holder.galleryClickLayout.setClickable(false);
                    holder.galleryClickLayout.setFocusable(false);
                } else if (tab == Utilities.Tab.Image) {
                    holder.galleryShadowView.setVisibility(View.GONE);
                    holder.galleryPlayImageView.setVisibility(View.GONE);
                    holder.galleryClickLayout.setClickable(true);
                    holder.galleryClickLayout.setFocusable(true);
                    new Thread(() -> {
                        Bitmap thumb = Utilities.getThumbnailPhoto(getApplicationContext(), item.file);
                        if (thumb != null) {
                            runOnUiThread(() -> {
                                if (holder.galleryThumbnailImageView.getTag().equals(tag)) {
                                    holder.galleryThumbnailImageView.setImageBitmap(thumb);
                                    holder.galleryThumbnailImageView.setAlpha(1f);
                                }
                            });
                        }
                    }).start();
                } else if (tab == Utilities.Tab.Video || tab == Utilities.Tab.Music) {
                    holder.galleryShadowView.setVisibility(View.VISIBLE);
                    holder.galleryPlayImageView.setVisibility(View.VISIBLE);
                    holder.galleryClickLayout.setClickable(true);
                    holder.galleryClickLayout.setFocusable(true);
                    new Thread(() -> {
                        Bitmap bitmap = Utilities.getThumbnailVideo(item.file);
                        if (bitmap != null) {
                            runOnUiThread(() -> {
                                if (holder.galleryThumbnailImageView.getTag().equals(tag)) {
                                    holder.galleryThumbnailImageView.setImageBitmap(bitmap);
                                    holder.galleryThumbnailImageView.setAlpha(1f);
                                }
                            });
                        }
                    }).start();
                }
                holder.galleryClickLayout.setOnClickListener(view -> {
                    if (item.type == Folder.Type.Loading || item.type == Folder.Type.Back) {
                        return;
                    }
                    if (selectionMod) {
                        if (selected.contains(item.file.getPath())) {
                            selected.remove(item.file.getPath());
                        } else {
                            selected.add(item.file.getPath());
                        }
                        selectionMod = selected.size() > 0;
                        notifyItemChanged(position);
                        updateSelectionMode();
                    } else {
                        openFile(item.file, currentMod == Utilities.Mod.Hide);
                    }
                });
                holder.galleryClickLayout.setOnLongClickListener((View.OnLongClickListener) view -> {
                    if (item.type == Folder.Type.Loading) {
                        return false;
                    }
                    if (selected.contains(item.file.getPath())) {
                        selected.remove(item.file.getPath());
                    } else {
                        selected.add(item.file.getPath());
                    }
                    selectionMod = selected.size() > 0;
                    notifyItemChanged(position);
                    updateSelectionMode();
                    return true;
                });
                if (item.file != null && selected.contains(item.file.getPath())) {
                    holder.gallerySelectView1.setVisibility(View.VISIBLE);
                    holder.gallerySelectView2.setVisibility(View.VISIBLE);
                } else {
                    holder.gallerySelectView1.setVisibility(View.GONE);
                    holder.gallerySelectView2.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class AdViewHolder extends RecyclerView.ViewHolder {

            AdViewHolder(View itemView) {
                super(itemView);
            }
        }

        public class DataViewHolder extends RecyclerView.ViewHolder {
            public View galleryClickLayout;
            public ImageView galleryThumbnailImageView;
            public ImageView galleryPlayImageView;
            public View galleryShadowView;
            public View gallerySelectView1;
            public View gallerySelectView2;

            DataViewHolder(View itemView) {
                super(itemView);
                galleryClickLayout = itemView.findViewById(R.id.galleryClickLayout);
                galleryThumbnailImageView = itemView.findViewById(R.id.galleryThumbnailImageView);
                galleryPlayImageView = itemView.findViewById(R.id.galleryPlayImageView);
                galleryShadowView = itemView.findViewById(R.id.galleryShadowView);
                gallerySelectView1 = itemView.findViewById(R.id.gallerySelectView1);
                gallerySelectView2 = itemView.findViewById(R.id.gallerySelectView2);
            }
        }
    }

    public void refreshStatusBar(boolean blackIcon) {
        navigationBarBlackIcon = blackIcon;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Utilities.getColor(R.color.colorWhite));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            if (!blackIcon) {
                getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        } else {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (isLightNavigationBar()) {
                getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            } else {
                getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            }
        }
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    private int navigationBarColor;
    private boolean navigationBarBlackIcon;

    private boolean isLightNavigationBar() {
        if (Utilities.isNightTheme()) {
            return false;
        }
        return navigationBarColor == Utilities.getColor(R.color.colorWhite) ||
                navigationBarColor == Utilities.getColor(R.color.colorDivider) ||
                navigationBarColor == Utilities.getColor(R.color.colorDividerLight) ||
                navigationBarColor == Utilities.getColor(R.color.colorBackground);
    }

    public void setNavigationBarColor(int color, boolean force) {
        if (Build.VERSION.SDK_INT >= 27 || force) {
            navigationBarColor = color;
        } else {
            navigationBarColor = Color.parseColor("#000000");
        }
        getWindow().setNavigationBarColor(navigationBarColor);
        refreshStatusBar(navigationBarBlackIcon);
    }

    public boolean isStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //int result = ContextCompat.checkSelfPermission(MainActivity.this, MANAGE_EXTERNAL_STORAGE);
            //return result == PackageManager.PERMISSION_GRANTED;
            return Environment.isExternalStorageManager();
        } else {
            int result = ContextCompat.checkSelfPermission(FileManagerActivity.this, WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
    }

    public void askStoragePermission() {

        if (!isStoragePermission()) {
            LayoutInflater layoutInflater = LayoutInflater.from(this);

            //  permissionAlertDialog.dismissSilently();
            if (isStoragePermission()) {
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s", getPackageName())));
                    startActivityForResult(intent, PERMISSION_REQUEST_CODE);
                } catch (Exception e) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivityForResult(intent, PERMISSION_REQUEST_CODE);
                }
            } else {
                ActivityCompat.requestPermissions(FileManagerActivity.this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            checkPermission();
        }
    }

    private void checkPermission() {
        if (isStoragePermission()) {
            if (permissionAlertDialog != null) {
                permissionAlertDialog.dismissSilently();
            }
            hidePopupMenu();
            setBlurry(false);
            setHideMode(Utilities.Mod.All);
            updateAdaptersData(Utilities.getAllPath());
        } else {
            // Toast.makeText(this, "Please allow permission for storage access!", Toast.LENGTH_SHORT).show();
        }
    }

    public ArrayList<Folder> getImages() {
        ArrayList<Folder> images = new ArrayList<>();
        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        Cursor cursor = getApplicationContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy + " DESC");
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            String path = cursor.getString(dataColumnIndex);
            boolean isHidden = path.startsWith(Utilities.getHidePath());
            if ((!isHidden && currentMod == Utilities.Mod.All)
                    || (isHidden && currentMod == Utilities.Mod.Hide)) {
                images.add(Folder.getInstance(new File(path), Folder.Type.File));
            }
        }
        return images;
    }

    public ArrayList<Folder> getVideos() {
        ArrayList<Folder> videos = new ArrayList<>();
        final String[] columns = {MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID};
        final String orderBy = MediaStore.Video.Media.DATE_TAKEN;
        Cursor cursor = getApplicationContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy + " DESC");
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
            String path = cursor.getString(dataColumnIndex);
            boolean isHidden = path.startsWith(Utilities.getHidePath());
            if ((!isHidden && currentMod == Utilities.Mod.All)
                    || (isHidden && currentMod == Utilities.Mod.Hide)) {
                videos.add(Folder.getInstance(new File(path), Folder.Type.File));
            }
        }
        return videos;
    }

    public ArrayList<Folder> getMusics() {
        ArrayList<Folder> musics = new ArrayList<>();
        String[] columns = {MediaStore.Audio.Media.DATA, MediaStore.Audio.Media._ID};
        String orderBy = MediaStore.Audio.Media.DATE_ADDED;
        Cursor cursor = getApplicationContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy + " DESC");
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            String path = cursor.getString(dataColumnIndex);
            boolean isHidden = path.startsWith(Utilities.getHidePath());
            if ((!isHidden && currentMod == Utilities.Mod.All)
                    || (isHidden && currentMod == Utilities.Mod.Hide)) {
                musics.add(Folder.getInstance(new File(path), Folder.Type.File));
            }
        }
        return musics;
    }

    private ArrayList<Folder> getFileList(Utilities.Tab tab) {
        if (currentMod == Utilities.Mod.Hide) {
            if (tab == Utilities.Tab.Image) {
                return Utilities.typedFiles.images;
            } else if (tab == Utilities.Tab.Video) {
                return Utilities.typedFiles.videos;
            } else if (tab == Utilities.Tab.Music) {
                return Utilities.typedFiles.musics;
            } else if (tab == Utilities.Tab.Document) {
                return Utilities.typedFiles.documents;
            }
        }
        ArrayList<Folder> documents = new ArrayList<>();
        Uri collection;
        String[] projection = new String[]{
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.SIZE,
        };
        String sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " " + currentOrder.name;
        if (currentSort == Utilities.Sort.Name) {
            sortOrder = MediaStore.Files.FileColumns.DISPLAY_NAME + " " + currentOrder.name;
        } else if (currentSort == Utilities.Sort.Date) {
            sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " " + currentOrder.name;
        } else if (currentSort == Utilities.Sort.Size) {
            sortOrder = MediaStore.Files.FileColumns.SIZE + " " + currentOrder.name;
        }
        String[] selectionArgs = new String[]{};
        if (tab == Utilities.Tab.Image) {
            selectionArgs = new String[]{
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("gif"),
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("png"),
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("jpg"),
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("jpeg"),
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("webp")
            };
        } else if (tab == Utilities.Tab.Video) {
            selectionArgs = new String[]{
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp4"),
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("mkv"),
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("avi"),
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("mov"),
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("flv"),
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("wmv"),
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("m4v")
            };
        } else if (tab == Utilities.Tab.Music) {
            selectionArgs = new String[]{
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3"),
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("ogg"),
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("wav")
            };
        } else if (tab == Utilities.Tab.Document) {
            selectionArgs = new String[]{
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf"),
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("txt"),
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("doc"),
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("docx"),
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("xls"),
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("xlsx"),
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("ppt"),
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("pptx")
            };
        }
        StringBuilder selection = new StringBuilder();
        for (int i = 0; i < selectionArgs.length; i++) {
            selection.append(MediaStore.Files.FileColumns.MIME_TYPE + " = ?");
            if (i != selectionArgs.length - 1) {
                selection.append(" OR ");
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Files.getContentUri("external");
        }
        ArrayList<String> paths = new ArrayList<>();
        try (Cursor cursor = getContentResolver().query(collection, projection, selection.toString(), selectionArgs, sortOrder)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnData = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                int columnName = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
                do {
                    String path = cursor.getString(columnData);
                    //if (path.endsWith(".wav")) {
                    //Utilities.Loge("oooooo", path + " " + Utilities.getHidePath());
                    //}
                    boolean isHidden = path.startsWith(Utilities.getHidePath());
                    if (currentMod == Utilities.Mod.All) {
                        if (!isHidden) {
                            paths.add(path);
                            documents.add(Folder.getInstance(new File(path), Folder.Type.File));
                        }
                    } else {
                        if (isHidden) {
                            paths.add(path);
                            documents.add(Folder.getInstance(new File(path), Folder.Type.File));
                        }
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Utilities.Loge("eeeeeee", e.getMessage());
        }
        ArrayList<Folder> extra = new ArrayList<>();
        if (tab == Utilities.Tab.Image && documents.size() == 0) {
            extra = getImages();
        } else if (tab == Utilities.Tab.Video && documents.size() == 0) {
            extra = getVideos();
        } else if (tab == Utilities.Tab.Music && documents.size() == 0) {
            extra = getMusics();
        }
        for (int i = 0; i < extra.size(); i++) {
            if (!paths.contains(extra.get(i).file.getPath())) {
                documents.add(extra.get(i));
            }
        }
        return documents;
    }

    private void updateAdaptersData(String path) {
        Utilities.callBroadCast(new Utilities.BroadCastListener() {
            @Override
            public void onStart() {
                Utilities.runOnUi(FileManagerActivity.this, () -> {
                    folderAdapter.setLoading(path);
                    imageAdapter.setLoading();
                    videoAdapter.setLoading();
                    musicAdapter.setLoading();
                    documentAdapter.setLoading();
                    updatePathTextView();
                });
            }

            @Override
            public void onDone() {
                Utilities.loadFileList(FileManagerActivity.this, (Utilities.LoadFileListener) () -> {
                    Utilities.runOnUi(FileManagerActivity.this, () -> {
                        folderAdapter.setData(path);
                        imageAdapter.setData();
                        videoAdapter.setData();
                        musicAdapter.setData();
                        documentAdapter.setData();
                        updatePathTextView();
                    });
                });
            }
        });
    }

    private void goToPath(String path) {
        folderAdapter.update(path);
        updatePathTextView();
    }

    private void updateAdaptersView() {
        Utilities.runOnUi(this, () -> {
            folderAdapter.notifyDataSetChanged();
            imageAdapter.notifyDataSetChanged();
            videoAdapter.notifyDataSetChanged();
            musicAdapter.notifyDataSetChanged();
            documentAdapter.notifyDataSetChanged();
        });
    }

}