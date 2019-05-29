package csie.aad.ast_album;

import android.app.ActionBar;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;

    private GridLayoutManager gridLayout_2 = new GridLayoutManager(this, 2);
    private GridLayoutManager gridLayout_4 = new GridLayoutManager(this, 4);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(gridLayout_4);

        //mAdapter = new ImageAdapter(this, SpacePhoto.getSpacePhotos());
        mAdapter = new ImageAdapter(this, readPhoto());

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.bigIcon:
                mRecyclerView.setLayoutManager(gridLayout_2);
                break;
            case R.id.smallIcon:
                mRecyclerView.setLayoutManager(gridLayout_4);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public ArrayList readPhoto(){

        ArrayList mSpacePhoto = new ArrayList<>();

        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projImage = { MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DISPLAY_NAME};

        Cursor mCursor = getContentResolver().query(
                mImageUri,
                projImage,
                MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[]{"image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED+" desc");

        String path, displayName;
        if(mCursor != null){
            while (mCursor.moveToNext()){
                path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                displayName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                mSpacePhoto.add(new SpacePhoto(path, displayName));
            }
        }
        //Toast.makeText(this, path, Toast.LENGTH_LONG).show();
        return mSpacePhoto;
    }
}
