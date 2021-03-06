package mobi.shush.goflick;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;
import android.support.v4.view.MenuItemCompat;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.CursorAdapter;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import mobi.shush.goflick.adapter.PhotosAdapter;
import mobi.shush.goflick.base.GoFlickActivity;
import mobi.shush.goflick.bean.Photo;
import mobi.shush.goflick.comm.Flickr;
import mobi.shush.goflick.comm.Request;
import mobi.shush.goflick.comm.RequestListener;
import mobi.shush.goflick.comm.handler.PhotosHandler;
import mobi.shush.goflick.utils.User;


public class LandingActivity extends GoFlickActivity implements PhotosAdapter.Listener {
    private static final int REQUEST_HISTORY = 34;
    SearchView mSearchView;
    RecyclerView mRecyclerView;
    PhotosAdapter mAdapter;
    MenuItem mSearchItem;
    SimpleCursorAdapter mSuggestionsAdapter;
    String mQuery;
    View mClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        mClear = findViewById(R.id.clear);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mSuggestionsAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                null,
                new String[] {"title"},
                new int[] {android.R.id.text1},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        if(savedInstanceState != null) {
            mAdapter = PhotosAdapter.load(savedInstanceState, this, LandingActivity.this);
            mQuery = savedInstanceState.getString("mQuery", null);
            if(mAdapter != null) {
                mRecyclerView.setAdapter(mAdapter);
                getSupportActionBar().setTitle(mQuery);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(mAdapter!=null) {
            mAdapter.save(outState);
            outState.putString("mQuery", mQuery);
        }
        super.onSaveInstanceState(outState);
    }

    private void search(String searchTerm) {
        mQuery = searchTerm;
        getSupportActionBar().setTitle(mQuery);
        Flickr.search(searchTerm).setListener(new RequestListener<PhotosHandler>(this) {
            @Override
            public void onStart(Request request) {
                super.onStart(request);
                if(mSearchItem != null) {
                    mSearchItem.collapseActionView();
                }
            }

            @Override
            public void onForeground(PhotosHandler result) {
                super.onForeground(result);
                mAdapter = new PhotosAdapter(LandingActivity.this, result.photos, result.page, result.pages, LandingActivity.this);
                mRecyclerView.setAdapter(mAdapter);
            }
        }).run();
        User.get().addSearchToHistory(searchTerm);
    }

    private void refreshSearchHistory(String query) {
        ArrayList<String> history = User.get().getSearchHistory();

        final MatrixCursor c = new MatrixCursor(new String[]{ BaseColumns._ID, "title" });
        int max = 10;
        int count = 0;
        for (int i=0; i<history.size(); i++) {
            if (history.get(i).toLowerCase().startsWith(query.toLowerCase())) {
                c.addRow(new Object[]{i, history.get(i)});
                count++;
            }
            if(count == max)
                break;
        }
        mSuggestionsAdapter.changeCursor(c);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_landing, menu);

        mSearchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) mSearchItem.getActionView();
        mSearchView.setSuggestionsAdapter(mSuggestionsAdapter);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                mClear.requestFocus();
                InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                MenuItemCompat.collapseActionView(mSearchItem);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                refreshSearchHistory(newText);
                return false;
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mSearchItem.collapseActionView();
                // Check if no view has focus:
                View view = LandingActivity.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                return true;
            }
        });
        mSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Object item = ((Cursor) mSearchView.getSuggestionsAdapter().getItem(position)).getString(1);
                search(item.toString());
                return true;
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        if(mSearchItem != null && mSearchItem.isActionViewExpanded()) {
            mSearchItem.collapseActionView();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_search:
                mSearchView.setIconifiedByDefault(true);
                mSearchView.setFocusable(true);
                mSearchView.setIconified(false);
                mSearchView.requestFocusFromTouch();
                return false;
            case R.id.action_clear_history:
                User.get().clearHistory();
                return true;
            case R.id.action_full_history:
                startActivityForResult(new Intent(this, HistoryActivity.class), REQUEST_HISTORY);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (isFinishing() || resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
//            case SearchBox.VOICE_RECOGNITION_CODE:
//                if (data != null && data.hasExtra(RecognizerIntent.EXTRA_RESULTS)) {
//                    ArrayList<String> matches = data
//                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                    if (matches.size() > 0) {
//                        mSearchBox.populateEditText(matches.get(0));
//                    }
//                }
//                break;
            case REQUEST_HISTORY:
                if (data != null && data.hasExtra(HistoryActivity.EXTRA_QUERY)) {
                    String query = data.getStringExtra(HistoryActivity.EXTRA_QUERY);
//                    search(query);
//                    mSearchBox.hideCircularly(this);
                    search(query);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showLoader() {
        setProgressBarIndeterminateVisibility(true);
    }
    @Override
    public void hideLoader() {
        setProgressBarIndeterminateVisibility(false);
    }

    @Override
    public void onLoadMore(PhotosAdapter adapter, int page) {
        Flickr.search(mQuery, page).setListener(new RequestListener<PhotosHandler>(this) {
            @Override
            public void onStart(Request request) {
//                super.onStart(request);
            }

            @Override
            public void onForeground(PhotosHandler result) {
                super.onForeground(result);
                mAdapter.loadMore(result.photos, result.page, result.pages);
            }

            @Override
            public void onError(Request request, Exception ex) {
//                super.onError(request, ex);
                mAdapter.loadMoreFailed();
            }
        }).run();
    }
    @Override
    public void onPhotoClick(View view) {
        int position = mRecyclerView.getChildAdapterPosition(view);
        Photo photo = mAdapter.getPhoto(position);
        Intent intent = DetailsActivity.getIntent(this, photo);
        startActivity(intent);
    }
}
