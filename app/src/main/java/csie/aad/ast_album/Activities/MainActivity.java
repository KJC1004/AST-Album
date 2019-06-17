package csie.aad.ast_album.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import csie.aad.ast_album.Adapters.ImageAdapter;
import csie.aad.ast_album.Models.SpacePhoto;
import csie.aad.ast_album.R;

public class MainActivity extends AppCompatActivity {

    static final String GRID_COUNT = "Grid Count";
    private static final int CUSTOM_NUMBER = 1;
    final private int minGridSpanCnt = 2;
    final private int maxGridSpanCnt = 6;
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private int gridSpanCnt = 4;
    private EditText mEditText;
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "csie.aad.ast_album.sharedprefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditText = findViewById(R.id.urlEditText);
        //mEditText.setText("http://i.imgur.com/zuG2bGQ.jpg");
        mEditText.setImeOptions(EditorInfo.IME_ACTION_SEND);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    closeKeyboard();
                    checkInternet();
                }
                return false;
            }
        });

        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        gridSpanCnt = mPreferences.getInt(GRID_COUNT, 4);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridSpanCnt));
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPermission();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putInt(GRID_COUNT, gridSpanCnt);
        preferencesEditor.apply();
    }

    public void onBtnSearch(View view) {
        checkInternet();
    }

    public void checkInternet() {
        // Check the status of the network connection.
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }

        String url = mEditText.getText().toString();
        mEditText.setText("");

        // If the network is available, connected, and the search field is not empty, start a BookLoader AsyncTask.
        if (networkInfo != null && networkInfo.isConnected() && !url.isEmpty()) {
            searchPhoto(url);
        } else {
            if (url.isEmpty()) {
                Toast.makeText(this, "Please input website", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "NO Internet", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void searchPhoto(String url) {
        SpacePhoto spacePhoto = new SpacePhoto(url, "search photo");
        Intent intent = new Intent(this, StylizeActivity.class);
        intent.putExtra(StylizeActivity.EXTRA_PHOTO, spacePhoto);
        startActivity(intent);
    }

    public void closeKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void getPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CUSTOM_NUMBER);
        } else {
            setAdapter();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CUSTOM_NUMBER) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setAdapter();
            } else {
                this.finish();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setAdapter() {
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
        switch (item.getItemId()) {
            case R.id.zoomIn:
                if (gridSpanCnt > minGridSpanCnt)
                    mRecyclerView.setLayoutManager(new GridLayoutManager(this, --gridSpanCnt));
                break;
            case R.id.zoomOut:
                if (gridSpanCnt < maxGridSpanCnt)
                    mRecyclerView.setLayoutManager(new GridLayoutManager(this, ++gridSpanCnt));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public ArrayList readPhoto() {

        ArrayList mSpacePhoto = new ArrayList<>();

        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projImage = {MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DISPLAY_NAME};

        Cursor mCursor = getContentResolver().query(
                mImageUri,
                projImage,
                MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[]{"image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED + " desc");

        String path, displayName;
        if (mCursor != null) {
            while (mCursor.moveToNext()) {
                path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                displayName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                mSpacePhoto.add(new SpacePhoto(path, displayName));
            }
        }
        //Toast.makeText(this, path, Toast.LENGTH_LONG).show();
        return mSpacePhoto;
    }
}
