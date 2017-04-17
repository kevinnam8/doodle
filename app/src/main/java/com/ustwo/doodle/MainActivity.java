package com.ustwo.doodle;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This app enables the user to draw a doodle using finger-touch with additional features
 * @author Kevin Nam
 */
public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String MEDIA_DIRECTORY_NAME = "Doodle";
    private static final int REQUEST_PERMISSION_WRITE_STORAGE   = 1000;
    private static final int WALLPAPER_SET_NOTIFICATION_ID      = 2001;

    Activity mActivity;
    View mLayout;
    DoodleCanvas mCanvasView;
    int mBackgroundColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mLayout = findViewById(R.id.container);
        mCanvasView = (DoodleCanvas)findViewById(R.id.viewDoodleCanvas);

        mCanvasView.setDrawingCacheEnabled(true);

        mBackgroundColor = ContextCompat.getColor(this, R.color.colorWhite);
        mCanvasView.setBackgroundColor(mBackgroundColor);
        mCanvasView.setPaintColor(ContextCompat.getColor(this, R.color.colorBlack));
    }

    @Override
    protected void onResume() {
        super.onResume();

        // check the storage permission
        boolean hasStoragePermission = false;
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            hasStoragePermission = true;
        }

        if (!hasStoragePermission) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Snackbar.make(mLayout, getResources().getString(R.string.permission_storage_access),
                        Snackbar.LENGTH_INDEFINITE).setAction(getResources().getString(R.string.text_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Request the permission
                        ActivityCompat.requestPermissions(mActivity,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_PERMISSION_WRITE_STORAGE);
                    }
                }).show();

            } else {
                Snackbar.make(mLayout,
                        getResources().getString(R.string.permission_not_available_request_storage),
                        Snackbar.LENGTH_LONG).show();

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION_WRITE_STORAGE);
            }
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    finish();
                    startActivity(getIntent());
                } else {
                    Toast.makeText(mActivity, mActivity.getString(R.string.permission_consider_allow_storage), Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

// region bottom navigation
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_set_paint_color:
                    showPaintColorPicker();
                    return true;
                case R.id.navigation_set_background_color:
                    showBackgroundColorPicker();
                    return true;

                case R.id.navigation_save_doodle:
                    saveDoodle();
                    return true;

                case R.id.navigation_set_as_wallpaper:
                    setAsWallpaper();
                    return true;
            }
            return false;
        }

    };

    int mPaintColorItemIndex = 0;
    /**
     * Shows the color picker dialog to choose a paint colour
     */
    void showPaintColorPicker() {
        ColorPickerDialog.showColorPickerDialog(mActivity,
                getString(R.string.choose_paint_color),
                mPaintColorItemIndex,
                new ColorItemAdapter.ItemSelectedListener() {

                    @Override
                    public void onItemSelected(int color, int position) {
                        mPaintColorItemIndex = position;
                        mCanvasView.setPaintColor(color);
                    }
                });

    }

    int mBackgroundColorItemIndex = 1;
    /**
     * Shows the color picker dialog to choose a background colour
     */
    void showBackgroundColorPicker() {
        ColorPickerDialog.showColorPickerDialog(mActivity,
                getString(R.string.choose_background_color),
                mBackgroundColorItemIndex,
                new ColorItemAdapter.ItemSelectedListener() {

                    @Override
                    public void onItemSelected(int color, int position) {
                        mBackgroundColorItemIndex = position;
                        mBackgroundColor = color;
                        mCanvasView.setBackgroundColor(mBackgroundColor);
                    }
                });
    }

    /**
     * Requests the media scanner to scan a file
     * @param file - File to be scanned.
     */
    public void scanFileToGallery(final File file) {
        String[] filePaths = {file.getAbsolutePath()};
        MediaScannerConnection.scanFile(mActivity.getApplicationContext(),
                filePaths,
                null,
                new MediaScannerConnection.OnScanCompletedListener() {

            @Override
            public void onScanCompleted(String path, Uri uri) {
                Log.i(LOG_TAG, "onScanCompleted() path="+ path);
            }
        });
    }

    /**
     * Get the App's DCIM directory where doodle images are saved
     * @return DCIM directory file
     */
    public static File getAppDCIMDirectory() {
        File externalStorage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File file = new File(externalStorage, MEDIA_DIRECTORY_NAME);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    /**
     * Save the current image to JPEG, then execute a media scanner
     */
    void saveDoodle() {
        mCanvasView.buildDrawingCache();
        Bitmap bitmap = mCanvasView.getDrawingCache();
        File dcimDir = getAppDCIMDirectory();

        // generate a file name
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String fileName = String.format("%s.jpg", sdf.format(new Date()));
        final File file = new File(dcimDir, fileName);
        try
        {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, new FileOutputStream(file));
            mCanvasView.invalidate();

            Toast.makeText(mActivity,
                    mActivity.getString(R.string.message_doodle_was_saved, fileName),
                    Toast.LENGTH_LONG).show();

            scanFileToGallery(file);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        mCanvasView.destroyDrawingCache();
    }


    /**
     * Show a notification after setting a wallpaper
     * @param id - notification id
     * @param titleResId - title string resource id
     * @param mainText - main text
     * @param intent - action for user's tapping on the notification
     */
    public void showNotification(int id, int titleResId, String mainText, PendingIntent intent) {
        NotificationManager notificationManager = (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        String title = getString(titleResId);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mActivity)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(mainText)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setTicker(title);

        if(intent != null) {
            builder.setContentIntent(intent);
        }
        notificationManager.notify(id, builder.build());
    }

    /**
     * Set the current image as a phone's wallpaper
     */
    void setAsWallpaper() {
        WallpaperManager wallpaperManager
                = WallpaperManager.getInstance(getApplicationContext());
        try {
            mCanvasView.buildDrawingCache();
            Bitmap bitmap = mCanvasView.getDrawingCache();
            wallpaperManager.setBitmap(bitmap);

            showNotification(WALLPAPER_SET_NOTIFICATION_ID, R.string.app_name, getString(R.string.message_doodle_was_set_as_wallpaper), null);
            mCanvasView.destroyDrawingCache();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
// endregion

}
