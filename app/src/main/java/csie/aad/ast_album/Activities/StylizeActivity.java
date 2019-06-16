package csie.aad.ast_album.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import csie.aad.ast_album.Adapters.StyleAdapter;
import csie.aad.ast_album.Models.SpacePhoto;
import csie.aad.ast_album.Models.Stylize;
import csie.aad.ast_album.R;

import static csie.aad.ast_album.Models.Stylize.NUM_STYLES;
import static csie.aad.ast_album.Models.Stylize.THUMBNAIL_PATH;

public class StylizeActivity extends AppCompatActivity {

    private ImageView viewport;
    private StyleAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private SpacePhoto spacePhoto;

    public static ArrayList getThumbnails() {
        ArrayList<SpacePhoto> mList = new ArrayList();
        for (int i = 0; i < NUM_STYLES; ++i) {
            mList.add(
                    new SpacePhoto(
                            THUMBNAIL_PATH + "style" + i + ".jpg",
                            ""));
        }
        return mList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stylize);

        viewport = findViewById(R.id.viewport);
        mRecyclerView = findViewById(R.id.recycler_style);

        Intent intent = getIntent();
        spacePhoto = intent.getParcelableExtra(SpacePhotoActivity.EXTRA_SPACE_PHOTO);

        Glide.with(this)
//                .load(Uri.parse(THUMBNAIL_PATH+"style0.jpg"))
                .load(spacePhoto.mpath)
                .placeholder(R.drawable.ic_search)
                .error(R.drawable.ic_error)
                .into(viewport);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(
                new GridLayoutManager(
                        this,
                        1,
                        GridLayoutManager.HORIZONTAL,
                        false
                )
        );
        mAdapter = new StyleAdapter(this, getThumbnails());
        mRecyclerView.setAdapter(mAdapter);
    }

    public void onBtnCancel(View view) {
        finish();
    }

    public void onBtnSave(View view) {
        // TODO: Implement save function
    }

    public void onBtnShare(View view) {
        // TODO: Implement share function
    }
}
