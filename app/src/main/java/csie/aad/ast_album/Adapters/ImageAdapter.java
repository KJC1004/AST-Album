package csie.aad.ast_album.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import csie.aad.ast_album.Activities.StylizeActivity;
import csie.aad.ast_album.Models.SpacePhoto;
import csie.aad.ast_album.R;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder> {

    private ArrayList mSpacePhotos;
    private Context mContext;

    public ImageAdapter(Context context, ArrayList spacePhotos) {
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
        SpacePhoto spacePhoto = (SpacePhoto) mSpacePhotos.get(position);
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

    public class ImageHolder extends RecyclerView.ViewHolder {

        public final ImageView mImageView;
        final ImageAdapter mAdapter;

        public ImageHolder(View itemView, ImageAdapter adapter) {
            super(itemView);
            this.mImageView = itemView.findViewById(R.id.iv_photo);
            this.mImageView.getLayoutParams().height = this.mImageView.getLayoutParams().width;
            this.mAdapter = adapter;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        SpacePhoto spacePhoto = (SpacePhoto) mSpacePhotos.get(position);
                        Intent intent = new Intent(mContext, StylizeActivity.class);
                        intent.putExtra(StylizeActivity.EXTRA_PHOTO, spacePhoto);
                        mContext.startActivity(intent);

                        //Toast.makeText( view.getContext(), spacePhoto.mtitle , Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
