package csie.aad.ast_album.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import csie.aad.ast_album.Adapters.StyleAdapter;
import csie.aad.ast_album.Models.SpacePhoto;
import csie.aad.ast_album.Models.Stylize;
import csie.aad.ast_album.R;
import csie.aad.ast_album.Utils.ImageUtils;

import static csie.aad.ast_album.Models.Stylize.NUM_STYLES;
import static csie.aad.ast_album.Models.Stylize.THUMBNAIL_PATH;

public class StylizeActivity extends AppCompatActivity {

    private static final int REQUEST_SHARE = 0;

    private ImageView viewport;
    private StyleAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayout mLowerLayout;

    private SpacePhoto spacePhoto;
    private Uri sharedFileUri;

    Animation hideLower, showLower;

    private boolean ui_on;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stylize);

        getSupportActionBar().hide();

        viewport = findViewById(R.id.viewport);
        mRecyclerView = findViewById(R.id.recycler_style);

        mRecyclerView.setLayoutManager(
                new GridLayoutManager(
                        this,
                        1,
                        GridLayoutManager.HORIZONTAL,
                        false
                )
        );
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new StyleAdapter(this, ImageUtils.getThumbnails());
        mRecyclerView.setAdapter(mAdapter);

        Intent intent = getIntent();
        spacePhoto = intent.getParcelableExtra(SpacePhotoActivity.EXTRA_SPACE_PHOTO);
        Glide.with(this)
                .load(spacePhoto.mpath)
                .placeholder(R.drawable.ic_search)
                .error(R.drawable.ic_error)
                .into(viewport);

        mLowerLayout = findViewById(R.id.lowerLayout);
        ui_on = true;

        Stylize.init(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Stylize.clean();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SHARE) {
            getContentResolver().delete(sharedFileUri, null, null);
        }
    }

    public void onBtnCancel(View view) {
        finish();
    }

    public void onBtnSave(View view) {
        if (!mAdapter.isReady())
            return;

        String path = MediaStore.Images.Media.insertImage(
                this.getContentResolver(),
                ImageUtils.drawableToBitmap(viewport.getDrawable()),
                "",
                "");

        view.setEnabled(false);

        Toast.makeText(this, "Saved to " + path, Toast.LENGTH_LONG).show();
    }

    public void onBtnShare(View view) {
        if (!mAdapter.isReady())
            return;

        String path = MediaStore.Images.Media.insertImage(
                getContentResolver(),
                ImageUtils.drawableToBitmap(viewport.getDrawable()),
                "",
                "");

        Uri contentUri = Uri.parse(path);

        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            // temp permission for receiving app to read this file
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            startActivityForResult(
                    Intent.createChooser(shareIntent, "Choose an app"),
                    0);
        }

        sharedFileUri = contentUri;
    }

    public void onClickViewport(View view) {
        ui_on = !ui_on;
        int h = mLowerLayout.getMeasuredHeight();

        mLowerLayout.animate()
                .translationYBy(ui_on? -h: h)
                .setDuration(300);
        mLowerLayout.setClickable(ui_on);
//        mLowerLayout.setVisibility(ui_on? View.VISIBLE: View.INVISIBLE);

    }
}
