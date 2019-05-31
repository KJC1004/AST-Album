package csie.aad.ast_album;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder> {

    private ArrayList mSpacePhotos;
    private Context mContext;

    public class ImageHolder extends RecyclerView.ViewHolder{

        public final ImageView mImageView;
        final ImageAdapter mAdapter;

        public ImageHolder(View itemView, ImageAdapter adapter ) {
            super(itemView);
            this.mImageView = itemView.findViewById(R.id.iv_photo);
            this.mImageView.getLayoutParams().height = this.mImageView.getLayoutParams().width;
            this.mAdapter = adapter;

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        SpacePhoto spacePhoto = (SpacePhoto)mSpacePhotos.get(position);
                        //Intent intent = new Intent(mContext, ....)
                        Intent intent = new Intent(mContext, SpacePhotoActivity.class);
                        intent.putExtra(SpacePhotoActivity.EXTRA_SPACE_PHOTO, spacePhoto);
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                (Activity) mContext, mImageView, ViewCompat.getTransitionName(mImageView)
                        );
                        mContext.startActivity(intent);

                        //Toast.makeText( view.getContext(), spacePhoto.mtitle , Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public ImageAdapter(Context context, ArrayList spacePhotos){
        mContext = context;
        mSpacePhotos = spacePhotos;
    }


    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View photoView = inflater.inflate(R.layout.photo_item, parent, false);
        return new ImageHolder(photoView, this);
    }

    @Override
    public void onBindViewHolder(ImageHolder holder, int position) {
        SpacePhoto spacePhoto = (SpacePhoto)mSpacePhotos.get(position);
        ImageView imageView = holder.mImageView;

        Glide.with(mContext)
                .load(spacePhoto.mpath)
                .placeholder(R.drawable.ic_search)
                .error(R.drawable.ic_error)
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return mSpacePhotos.size();
    }
}
