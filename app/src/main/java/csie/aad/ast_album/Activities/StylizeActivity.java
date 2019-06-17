package csie.aad.ast_album.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
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
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import csie.aad.ast_album.Adapters.StyleAdapter;
import csie.aad.ast_album.Models.SpacePhoto;
import csie.aad.ast_album.Models.Stylize;
import csie.aad.ast_album.R;
import csie.aad.ast_album.Utils.ImageUtils;

public class StylizeActivity extends AppCompatActivity {

    public static final String EXTRA_PHOTO = "StylizeActivity.SPACE_PHOTO";
    private static final int REQUEST_SHARE = 0;
    Animation barHide, barShow, editHide, editShow;
    private ImageView viewport;
    private StyleAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayout mLowerLayout;
    private FloatingActionButton fabBack, fabEdit;
    private SpacePhoto spacePhoto;
    private Uri sharedFileUri;
    private boolean edit_on, imgShow_on;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stylize);

        getSupportActionBar().hide();

        viewport = findViewById(R.id.viewport);
        mRecyclerView = findViewById(R.id.recycler_style);
        fabBack = findViewById(R.id.fabBack);
        fabEdit = findViewById(R.id.fabEdit);

        barHide = AnimationUtils.loadAnimation(this, R.anim.hidebar);
        barShow = AnimationUtils.loadAnimation(this, R.anim.showbar);
        editHide = AnimationUtils.loadAnimation(this, R.anim.hideedit);
        editShow = AnimationUtils.loadAnimation(this, R.anim.showedit);

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
        spacePhoto = intent.getParcelableExtra(EXTRA_PHOTO);
        Glide.with(this)
                .load(spacePhoto.mpath)
                .placeholder(R.drawable.ic_search)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        Toast.makeText(StylizeActivity.this, "Can't find the photo", Toast.LENGTH_SHORT).show();
                        finish();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .error(R.drawable.ic_error)
                .into(viewport);


        mLowerLayout = findViewById(R.id.lowerLayout);
        edit_on = false;
        imgShow_on = false;

        Stylize.init(this);
    }

    public void onClickBack(View view) {
        finish();
    }

    public void onClickEdit(View view) {
        edit_on = true;
        styleEditShow(300);
        fabEditHide();
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
        edit_on = false;
        styleEditHide(300);
        fabEditShow();
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
        if (imgShow_on) {
            if (edit_on) {
                styleEditShow(300);
            } else {
                fabEditShow();
            }
            fabBackShow();
        } else {
            if (edit_on) {
                styleEditHide(300);
            } else {
                fabEditHide();
            }
            fabBackHide();
        }
        imgShow_on = !imgShow_on;
    }

    public void styleEditShow(int duration) {
        int h = mLowerLayout.getMeasuredHeight();
        mLowerLayout.animate()
                .translationYBy(-h)
                .alpha(1.0f)
                .setDuration(duration);
        mLowerLayout.setClickable(true);
    }

    public void styleEditHide(int duration) {
        int h = mLowerLayout.getMeasuredHeight();
        mLowerLayout.animate()
                .translationYBy(h)
                .alpha(0.0f)
                .setDuration(duration);
        mLowerLayout.setClickable(false);
    }

    public void fabEditShow() {
        fabEdit.startAnimation(editShow);
        fabEdit.setClickable(true);
    }

    public void fabEditHide() {
        fabEdit.startAnimation(editHide);
        fabEdit.setClickable(false);
    }

    public void fabBackShow() {
        fabBack.startAnimation(barShow);
        fabBack.setClickable(true);
    }

    public void fabBackHide() {
        fabBack.startAnimation(barHide);
        fabBack.setClickable(false);
    }
}
