package csie.aad.ast_album;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

    private int gridSpanCnt = 4;
    final private int minGridSpanCnt = 2;
    final private int maxGridSpanCnt = 5;
    private static final int CUSTOM_NUMBER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridSpanCnt));

        getPermission();
    }


    private void getPermission(){
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permissionCheck!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CUSTOM_NUMBER);
        }else{
            setAdapter();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CUSTOM_NUMBER) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setAdapter();
            }else {
                this.finish();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setAdapter(){
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
            case R.id.zoomIn:
                if(gridSpanCnt>minGridSpanCnt)
                    mRecyclerView.setLayoutManager(new GridLayoutManager(this, --gridSpanCnt));
                break;
            case R.id.zoomOut:
                if(gridSpanCnt<maxGridSpanCnt)
                    mRecyclerView.setLayoutManager(new GridLayoutManager(this, ++gridSpanCnt));
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
