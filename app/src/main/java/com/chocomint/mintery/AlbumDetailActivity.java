package com.chocomint.mintery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class AlbumDetailActivity extends AppCompatActivity {
    ArrayList<Media> arrayList;
    Toolbar mainToolbar;
    TextView toolBarText;
    Fragment mediaFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);
        setContentView(R.layout.activity_album_detail);
        getView();
        setSupportActionBar(mainToolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String albumTitle = getIntent().getExtras().getString("title");
        toolBarText.setText(albumTitle);
        new LoadDataThread().execute(albumTitle);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    private void getView() {
        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolBarText = (TextView) findViewById(R.id.toolbar_text);
        mediaFrag = new AlbumDetailFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.album_detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about_us:
                startActivity(new Intent(this, AboutUsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class LoadDataThread extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {

            arrayList = new ArrayList<Media>();
            String[] query = { MediaStore.Images.ImageColumns._ID,
                    MediaStore.Images.ImageColumns.DATE_MODIFIED,
                    MediaStore.Images.ImageColumns.DISPLAY_NAME,
                    MediaStore.Images.ImageColumns.DATA,
                    MediaStore.Images.ImageColumns.SIZE
            };
            Cursor filter = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    query,
                    MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + " = ?",
                    new String[] {strings[0]},
                    MediaStore.Images.ImageColumns.DATE_MODIFIED + " DESC" // Sort order.
            );

            int image_column_index_1 = filter.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID);
            int date_column_index_1 = filter.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_MODIFIED);
            int data_column_index_1 = filter.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
            int size_column_index_1 = filter.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.SIZE);
            int name_column_index_1 = filter.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME);

            int countquery = filter.getCount();
            Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
            for (int i = 0; i < countquery; i++) {
                filter.moveToPosition(i);
                int id = filter.getInt(image_column_index_1);

                Long currDate = filter.getLong(date_column_index_1);
                calendar.setTimeInMillis(currDate*1000L);
                Date day = calendar.getTime();

                Long size = filter.getLong(size_column_index_1);
                String sizeStr = String.format("%.2f", (float) size / (1024 * 1024));
                String name = filter.getString(name_column_index_1);

                String filePath = filter.getString(data_column_index_1);
                Media media = new Media(id, filePath, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE, day, (long) 0, name, sizeStr, strings[0], false);
                arrayList.add(media);
            }

            String[] videoColumns = { MediaStore.Video.VideoColumns._ID,
                    MediaStore.Video.VideoColumns.DATE_MODIFIED,
                    MediaStore.Video.VideoColumns.DURATION,
                    MediaStore.Video.VideoColumns.DATA,
                    MediaStore.Video.VideoColumns.DISPLAY_NAME,
                    MediaStore.Video.VideoColumns.SIZE,
            };

            Cursor videocursor = getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    videoColumns,
                    MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME + " = ?",
                    new String[] {strings[0]},
                    MediaStore.Video.VideoColumns.DATE_MODIFIED + " DESC" // Sort order.
            );

            int video_column_index = videocursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns._ID);
            int video_duration_column_index = videocursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION);
            int video_date_column_index = videocursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATE_MODIFIED);
            int video_data_column_index = videocursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATA);
            int video_size_column_index = videocursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.SIZE);
            int video_name_column_index = videocursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DISPLAY_NAME);

            int videoCount = videocursor.getCount();
            for (int i = 0; i < videoCount; i++) {
                videocursor.moveToPosition(i);
                int id = videocursor.getInt(video_column_index);

                Long currDate = videocursor.getLong(video_date_column_index);
                calendar.setTimeInMillis(currDate*1000L);
                Date day = calendar.getTime();

                Long size = videocursor.getLong(video_size_column_index);
                String sizeStr = String.format("%.2f", (float) size / (1024 * 1024));
                String name = videocursor.getString(video_name_column_index);

                Long duration = videocursor.getLong(video_duration_column_index);

                String filePath = videocursor.getString(video_data_column_index);
                Media media = new Media(id, filePath, MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO, day, duration, name, sizeStr, strings[0], false);
                arrayList.add(media);
            }
            videocursor.close();

            Collections.sort(arrayList, new SortByModified());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("list", arrayList);
            mediaFrag.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_photo, mediaFrag, "media").commit();
        }
    }
}