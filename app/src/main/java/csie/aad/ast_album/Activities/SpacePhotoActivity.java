package csie.aad.ast_album.Activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import csie.aad.ast_album.R;
import csie.aad.ast_album.Models.SpacePhoto;

public class SpacePhotoActivity extends AppCompatActivity {

    public static  final String EXTRA_SPACE_PHOTO = "SpacePhotoActivity.SPACE_PHOTO";
    private ImageView mImageView;
    private SpacePhoto spacePhoto;
    private FloatingActionButton fab;

    private boolean showFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_photo);

        setTitle("");

        mImageView = (ImageView) findViewById(R.id.image);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        spacePhoto = getIntent().getParcelableExtra(EXTRA_SPACE_PHOTO);

        Glide.with(this)
                .load(spacePhoto.mpath)
                .asBitmap()
                .error(R.drawable.ic_error)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(mImageView);

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(showFlag){
                    getSupportActionBar().hide();
                    fab.hide();
                    showFlag = false;
                }else{
                    getSupportActionBar().show();
                    fab.show();
                    showFlag = true;
                }
            }
        });

    }

    public void editPhoto(View view){
        Toast.makeText(this, spacePhoto.mpath, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, StylizeActivity.class);
        intent.putExtra(EXTRA_SPACE_PHOTO, spacePhoto);
        startActivity(intent);
    }

}