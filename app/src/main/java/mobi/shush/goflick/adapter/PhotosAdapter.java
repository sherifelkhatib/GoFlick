package mobi.shush.goflick.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import mobi.shush.goflick.R;
import mobi.shush.goflick.bean.Photo;

/**
 * Created by Shush on 11/11/2015.
 */
public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotoViewHolder> {
    private final ArrayList<Photo> photos;
    private final int width;

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        ImageView image;
        public PhotoViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.text);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }

    public PhotosAdapter(Context context, ArrayList<Photo> photos) {
        super();
        this.photos = photos;
        this.width = context.getResources().getDimensionPixelSize(R.dimen.image_width);
    }

    @Override
    public final PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
//        v.setOnClickListener(this);

        return new PhotoViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    @Override
    public final void onBindViewHolder(PhotosAdapter.PhotoViewHolder holder, int position) {
        holder.text.setText(photos.get(position).title);
        Picasso.with(holder.image.getContext()).load(photos.get(position).getImageMedium()).resize(width, 0).into(holder.image);
    }
}