package csie.aad.ast_album.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import csie.aad.ast_album.Models.SpacePhoto;
import csie.aad.ast_album.R;

public class StyleAdapter extends RecyclerView.Adapter<StyleAdapter.StyleHolder> {

    private Context mContext;
    private ArrayList mPhotos;

    public StyleAdapter(Context context, ArrayList photos){
        mContext = context;
        mPhotos = photos;
    }

    @NonNull
    @Override
    public StyleHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View photoView = inflater.inflate(R.layout.style_item, viewGroup, false);
        return new StyleAdapter.StyleHolder(photoView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull StyleHolder holder, int i) {

        SpacePhoto spacePhoto = (SpacePhoto)mPhotos.get(i);
        ImageView imageView = holder.mImageView;

        Glide.with(mContext)
                .load(spacePhoto.mpath)
                .placeholder(R.drawable.ic_search)
                .error(R.drawable.ic_error)
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

    public class StyleHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;
        final StyleAdapter mAdapter;

        public StyleHolder(View itemView, StyleAdapter adapter) {
            super(itemView);
            this.mImageView = itemView.findViewById(R.id.imageView);
            this.mAdapter = adapter;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Toast.makeText(mAdapter.mContext,
                            "Clicked "+position,
                            Toast.LENGTH_SHORT);
                    // TODO: Send Style values to AsyncTask
                }
            });
        }
    }

}
