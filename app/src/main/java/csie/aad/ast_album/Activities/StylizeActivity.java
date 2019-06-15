package csie.aad.ast_album.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import csie.aad.ast_album.R;
import csie.aad.ast_album.Utils.Stylize;

public class StylizeActivity extends AppCompatActivity {
    private ImageView viewport;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stylize);

        Intent intent = getIntent();
        String pathname = intent.getStringExtra(SpacePhotoActivity.EXTRA_SPACE_PHOTO);

        bitmap = BitmapFactory.decodeFile(pathname);

        viewport = findViewById(R.id.viewport);

    }

}
