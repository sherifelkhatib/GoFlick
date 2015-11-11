package mobi.shush.goflick;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import mobi.shush.goflick.adapter.PhotosAdapter;
import mobi.shush.goflick.base.GoFlickActivity;
import mobi.shush.goflick.bean.Photo;
import mobi.shush.goflick.bean.PhotoExtended;
import mobi.shush.goflick.comm.Flickr;
import mobi.shush.goflick.comm.Request;
import mobi.shush.goflick.comm.RequestListener;
import mobi.shush.goflick.comm.handler.PhotoHandler;
import mobi.shush.goflick.comm.handler.PhotosHandler;

public class DetailsActivity extends GoFlickActivity {
    PhotoExtended mPhoto;
    ImageView mImage;
    TextView mAuthor;
    TextView mDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mImage = (ImageView) findViewById(R.id.image);
        mAuthor = (TextView) findViewById(R.id.author);
        mDescription = (TextView) findViewById(R.id.description);
        Photo photo = (Photo) getIntent().getSerializableExtra("photo");
        getSupportActionBar().setTitle(photo.title);


        if(savedInstanceState != null && savedInstanceState.containsKey("mPhoto")) {
            mPhoto = (PhotoExtended) savedInstanceState.getSerializable("mPhoto");
            update();
        }
        else {
            Picasso.with(DetailsActivity.this).load(photo.getImage()).into(mImage, new Callback() {
                @Override
                public void onSuccess() {
                    findViewById(R.id.progress).setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    findViewById(R.id.progress).setVisibility(View.GONE);
                }
            });
            Flickr.details(photo).setListener(new RequestListener<PhotoHandler>(this) {

                @Override
                public void onForeground(PhotoHandler result) {
                    super.onForeground(result);
                    mPhoto = result.photo;
                    update();
                }
            }).run();
        }
    }

    private void update() {
        mAuthor.setText(mPhoto.title);
        mDescription.setText(mPhoto.description);
        if(mPhoto.hasImage()) {
            Picasso.with(DetailsActivity.this).load(mPhoto.getImage()).fetch(new Callback() {
                @Override
                public void onSuccess() {
                    //had to do this workaround because of into(Target) not working
                    Picasso.with(DetailsActivity.this).load(mPhoto.getImage()).noFade().into(mImage, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                        }
                    });
                }

                @Override
                public void onError() {

                }
            });
        }
        else {
        }
    }
    public static Intent getIntent(Activity activity, Photo photo) {
        Intent intent = new Intent(activity, DetailsActivity.class);
        intent.putExtra("photo", photo);
        return intent;
    }
}
