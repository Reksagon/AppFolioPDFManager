package com.ivanandevs.providers.fragmentmanagement;

import android.content.Intent;
import android.os.Bundle;

import com.ivanandevs.AppManagerActivity;
import com.ivanandevs.FileManagerActivity;
import com.ivanandevs.SettingsController;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.widget.Toast;

import java.util.Objects;

import com.ivanandevs.appfoliopdf.BuildConfig;
import com.ivanandevs.appfoliopdf.R;
import com.ivanandevs.appfoliopdf.fragment.AboutUsFragment;
import com.ivanandevs.appfoliopdf.fragment.AddImagesFragment;
import com.ivanandevs.appfoliopdf.fragment.AddTextFragment;
import com.ivanandevs.appfoliopdf.fragment.ExceltoPdfFragment;
import com.ivanandevs.appfoliopdf.fragment.ExtractTextFragment;
import com.ivanandevs.appfoliopdf.fragment.FAQFragment;
import com.ivanandevs.appfoliopdf.fragment.FavouritesFragment;
import com.ivanandevs.appfoliopdf.fragment.HistoryFragment;
import com.ivanandevs.appfoliopdf.fragment.HomeFragment;
import com.ivanandevs.appfoliopdf.fragment.ImageToPdfFragment;
import com.ivanandevs.appfoliopdf.fragment.InvertPdfFragment;
import com.ivanandevs.appfoliopdf.fragment.MergeFilesFragment;
import com.ivanandevs.appfoliopdf.fragment.PdfToImageFragment;
import com.ivanandevs.appfoliopdf.fragment.QrBarcodeScanFragment;
import com.ivanandevs.appfoliopdf.fragment.RemoveDuplicatePagesFragment;
import com.ivanandevs.appfoliopdf.fragment.RemovePagesFragment;
import com.ivanandevs.appfoliopdf.fragment.SettingsFragment;
import com.ivanandevs.appfoliopdf.fragment.SplitFilesFragment;
import com.ivanandevs.appfoliopdf.fragment.ViewFilesFragment;
import com.ivanandevs.appfoliopdf.fragment.ZipToPdfFragment;
import com.ivanandevs.appfoliopdf.fragment.texttopdf.TextToPdfFragment;
import com.ivanandevs.appfoliopdf.util.FeedbackUtils;
import com.ivanandevs.appfoliopdf.util.FragmentUtils;

import static com.ivanandevs.appfoliopdf.util.Constants.ACTION_MERGE_PDF;
import static com.ivanandevs.appfoliopdf.util.Constants.ACTION_SELECT_IMAGES;
import static com.ivanandevs.appfoliopdf.util.Constants.ACTION_TEXT_TO_PDF;
import static com.ivanandevs.appfoliopdf.util.Constants.ACTION_VIEW_FILES;
import static com.ivanandevs.appfoliopdf.util.Constants.ADD_IMAGES;
import static com.ivanandevs.appfoliopdf.util.Constants.ADD_PWD;
import static com.ivanandevs.appfoliopdf.util.Constants.ADD_WATERMARK;
import static com.ivanandevs.appfoliopdf.util.Constants.BUNDLE_DATA;
import static com.ivanandevs.appfoliopdf.util.Constants.COMPRESS_PDF;
import static com.ivanandevs.appfoliopdf.util.Constants.EXTRACT_IMAGES;
import static com.ivanandevs.appfoliopdf.util.Constants.OPEN_SELECT_IMAGES;
import static com.ivanandevs.appfoliopdf.util.Constants.PDF_TO_IMAGES;
import static com.ivanandevs.appfoliopdf.util.Constants.REMOVE_PAGES;
import static com.ivanandevs.appfoliopdf.util.Constants.REMOVE_PWd;
import static com.ivanandevs.appfoliopdf.util.Constants.REORDER_PAGES;
import static com.ivanandevs.appfoliopdf.util.Constants.ROTATE_PAGES;

/**
 * This is a fragment service that manages the fragments
 * mainly for the MainActivity.
 */
public class FragmentManagement implements IFragmentManagement {
    private final FragmentActivity mContext;
    private final NavigationView mNavigationView;
    private boolean mDoubleBackToExitPressedOnce = false;
    private final FeedbackUtils mFeedbackUtils;
    private final FragmentUtils mFragmentUtils;

    public FragmentManagement(FragmentActivity context, NavigationView navigationView) {
        mContext = context;
        mNavigationView = navigationView;
        mFeedbackUtils = new FeedbackUtils(mContext);
        mFragmentUtils = new FragmentUtils(mContext);
    }

    public void favouritesFragmentOption() {
        Fragment currFragment = mContext.getSupportFragmentManager().findFragmentById(R.id.content);

        Fragment fragment = new FavouritesFragment();
        FragmentManager fragmentManager = mContext.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction()
                .replace(R.id.content, fragment);
        if (!(currFragment instanceof HomeFragment)) {
            transaction.addToBackStack(mFragmentUtils.getFragmentName(currFragment));
        }
        transaction.commit();
    }

    public Fragment checkForAppShortcutClicked() {
        Fragment fragment = new HomeFragment();
        if (mContext.getIntent().getAction() != null) {
            switch (Objects.requireNonNull(mContext.getIntent().getAction())) {
                case ACTION_SELECT_IMAGES:
                    fragment = new ImageToPdfFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(OPEN_SELECT_IMAGES, true);
                    fragment.setArguments(bundle);
                    break;
                case ACTION_VIEW_FILES:
                    fragment = new ViewFilesFragment();
                    setNavigationViewSelection(R.id.nav_gallery);
                    break;
                case ACTION_TEXT_TO_PDF:
                    fragment = new TextToPdfFragment();
                    setNavigationViewSelection(R.id.nav_text_to_pdf);
                    break;
                case ACTION_MERGE_PDF:
                    fragment = new MergeFilesFragment();
                    setNavigationViewSelection(R.id.nav_merge);
                    break;
                default:
                    fragment = new HomeFragment(); // Set default fragment
                    break;
            }
        }
        if (areImagesReceived())
            fragment = new ImageToPdfFragment();

        mContext.getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();

        return fragment;
    }

    public boolean handleBackPressed() {
        Fragment currentFragment = mContext.getSupportFragmentManager()
                .findFragmentById(R.id.content);
        if (currentFragment instanceof HomeFragment) {
            return checkDoubleBackPress();
        } else {
            if (mFragmentUtils.handleFragmentBottomSheetBehavior(currentFragment))
                return false;
        }
        handleBackStackEntry();
        return false;
    }

    public boolean handleNavigationItemSelected(int itemId) {
        Fragment fragment = null;
        FragmentManager fragmentManager = mContext.getSupportFragmentManager();
        Bundle bundle = new Bundle();

        switch (itemId) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                break;
            case R.id.nav_camera:
                fragment = new ImageToPdfFragment();
                break;
            case R.id.nav_qrcode:
                fragment = new QrBarcodeScanFragment();
                break;
            case R.id.nav_gallery:
                fragment = new ViewFilesFragment();
                break;
            case R.id.nav_merge:
                fragment = new MergeFilesFragment();
                break;
            case R.id.nav_split:
                fragment = new SplitFilesFragment();
                break;
            case R.id.nav_text_to_pdf:
                fragment = new TextToPdfFragment();
                break;
            case R.id.nav_history:
                fragment = new HistoryFragment();
                break;
            case R.id.nav_add_text:
                fragment = new AddTextFragment();
                break;
            case R.id.nav_add_password:
                fragment = new RemovePagesFragment();
                bundle.putString(BUNDLE_DATA, ADD_PWD);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_remove_password:
                fragment = new RemovePagesFragment();
                bundle.putString(BUNDLE_DATA, REMOVE_PWd);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_share:
                mFeedbackUtils.shareApplication();
                break;
            case R.id.nav_about:
                fragment = new AboutUsFragment();
                break;
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                break;
            case R.id.nav_extract_images:
                fragment = new PdfToImageFragment();
                bundle.putString(BUNDLE_DATA, EXTRACT_IMAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_pdf_to_images:
                fragment = new PdfToImageFragment();
                bundle.putString(BUNDLE_DATA, PDF_TO_IMAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_excel_to_pdf:
                fragment = new ExceltoPdfFragment();
                break;
            case R.id.nav_remove_pages:
                fragment = new RemovePagesFragment();
                bundle.putString(BUNDLE_DATA, REMOVE_PAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_rearrange_pages:
                fragment = new RemovePagesFragment();
                bundle.putString(BUNDLE_DATA, REORDER_PAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_compress_pdf:
                fragment = new RemovePagesFragment();
                bundle.putString(BUNDLE_DATA, COMPRESS_PDF);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_add_images:
                fragment = new AddImagesFragment();
                bundle.putString(BUNDLE_DATA, ADD_IMAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_help:

                break;
            case R.id.nav_remove_duplicate_pages:
                fragment = new RemoveDuplicatePagesFragment();
                break;
            case R.id.nav_invert_pdf:
                fragment = new InvertPdfFragment();
                break;
            case R.id.nav_add_watermark:
                fragment = new ViewFilesFragment();
                bundle.putInt(BUNDLE_DATA, ADD_WATERMARK);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_zip_to_pdf:
                fragment = new ZipToPdfFragment();
                break;
            case R.id.nav_file_manager:
                FileManagerActivity.context = mContext;
                FileManagerActivity.settings = SettingsController.getInstance(mContext);
                FileManagerActivity.BuildConfig_APPLICATION_ID = BuildConfig.APPLICATION_ID;
                Intent in = new Intent(mContext, FileManagerActivity.class);
                mContext.startActivity(in);
                break;
            case R.id.nav_rotate_pages:
                fragment = new ViewFilesFragment();
                bundle.putInt(BUNDLE_DATA, ROTATE_PAGES);
                fragment.setArguments(bundle);
                break;
            case R.id.nav_text_extract:
                fragment = new ExtractTextFragment();
                break;
            case R.id.nav_faq:
                fragment = new FAQFragment();
                break;
            case R.id.nav_app_manager:
                Intent intent2 = new Intent(mContext, AppManagerActivity.class);
                mContext.startActivity(intent2);
                break;
        }

        try {
            if (fragment != null)
                fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // if help or share or what's new is clicked then return false, as we don't want
        // them to be selected
        return itemId != R.id.nav_share && itemId != R.id.nav_help;
                /*&& itemId != R.id.nav_whatsNew*/
    }

    /**
     * Closes the app only when double clicked
     */
    private boolean checkDoubleBackPress() {
        if (mDoubleBackToExitPressedOnce) {
            return true;
        }
        mDoubleBackToExitPressedOnce = true;
        Toast.makeText(mContext, R.string.confirm_exit_message, Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     *  Back stack count will be 1 when we open a item from favourite menu
     *  on clicking back, return back to fav menu and change title
     */
    private void handleBackStackEntry() {
        int count = mContext.getSupportFragmentManager().getBackStackEntryCount();
        if (count > 0) {
            String s = mContext.getSupportFragmentManager().getBackStackEntryAt(count - 1).getName();
            mContext.setTitle(s);
            mContext.getSupportFragmentManager().popBackStack();
        } else {
            Fragment fragment = new HomeFragment();
            mContext.getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
            mContext.setTitle(R.string.app_name);
            setNavigationViewSelection(R.id.nav_home);
        }
    }

    private boolean areImagesReceived() {
        Intent intent = mContext.getIntent();
        String type = intent.getType();
        return type != null && type.startsWith("image/");
    }

    private void setNavigationViewSelection(int id) {
        mNavigationView.setCheckedItem(id);
    }
}
