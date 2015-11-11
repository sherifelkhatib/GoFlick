package mobi.shush.goflick.adapter;

import android.content.Context;
import android.os.Bundle;
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
public class PhotosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private static final int TYPE_PHOTO = 0;
    private static final int TYPE_LOAD = 1;
    private final ArrayList<Photo> mPhotos;
    private final int mWidth;
    private int mPage, mPages;
    private boolean mLoading = false;
    private Listener mListener;

    public void save(Bundle outState) {
        outState.putSerializable("mPhotos", mPhotos);
        outState.putInt("mPage", mPage);
        outState.putInt("mPages", mPages);
    }

    public static PhotosAdapter load(Bundle savedInstanceState, Context context, Listener listener) {
        if(!savedInstanceState.containsKey("mPhotos")) {
            return null;
        }
        ArrayList<Photo> photos = (ArrayList<Photo>) savedInstanceState.getSerializable("mPhotos");
        int page = savedInstanceState.getInt("mPage");
        int pages = savedInstanceState.getInt("mPages");
        return new PhotosAdapter(context, photos, page, pages, listener);
    }

    public PhotosAdapter(Context context, ArrayList<Photo> photos, int page, int pages, Listener listener) {
        super();
        this.mPhotos = photos;
        this.mWidth = context.getResources().getDimensionPixelSize(R.dimen.image_width);
        this.mPage = page;
        this.mPages = pages;
        this.mListener = listener;
    }

    public void loadMore(ArrayList<Photo> photos, int page, int pages) {
        mLoading = false;
        this.mPhotos.addAll(photos);
        this.mPage = page;
        this.mPages = pages;
        notifyDataSetChanged();
    }

    public void loadMoreFailed() {
        //currently no visual indicator
        mLoading = false;
        notifyDataSetChanged();
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case TYPE_PHOTO:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
                v.setOnClickListener(this);

                return new PhotoViewHolder(v);
            case TYPE_LOAD:
            default:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_load, parent, false);
                return new LoadViewHolder(v);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position== mPhotos.size()?TYPE_LOAD:TYPE_PHOTO;
    }

    @Override
    public int getItemCount() {
        return mPhotos.size()+(mPage == mPages || mPages == 0 ?0:1);
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holderBase, int position) {
        switch (getItemViewType(position)) {
            case TYPE_PHOTO:
                PhotoViewHolder holder = (PhotoViewHolder) holderBase;
                holder.text.setText(mPhotos.get(position).title);
                Picasso.with(holder.image.getContext()).load(mPhotos.get(position).getImage()).resize(mWidth, 0).into(holder.image);
                break;
            case TYPE_LOAD:
                if(!mLoading) {
                    mListener.onLoadMore(this, mPage + 1);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        mListener.onPhotoClick(v);
    }

    public Photo getPhoto(int position) {
        return mPhotos.get(position);
    }

    public interface Listener {
        public void onLoadMore(PhotosAdapter adapter, int page);
        public void onPhotoClick(View view);
    }
    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        ImageView image;
        public PhotoViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.text);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }
    public static class LoadViewHolder extends RecyclerView.ViewHolder {
        public LoadViewHolder(View itemView) {
            super(itemView);
        }
    }
}