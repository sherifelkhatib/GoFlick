package mobi.shush.goflick;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.quinny898.library.persistentsearch.SearchBox;
import com.quinny898.library.persistentsearch.SearchResult;

import java.util.ArrayList;

import mobi.shush.goflick.utils.User;


public class LandingActivity extends ActionBarActivity {
    private static final int REQUEST_HISTORY = 34;
    SearchBox mSearchBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        mSearchBox = (SearchBox) findViewById(R.id.searchbox);
        initSearchBox();
        refreshSearchHistory();
    }

    private void search(String searchTerm) {
        User.get().addSearchToHistory(searchTerm);
        refreshSearchHistory();
    }

    private void refreshSearchHistory() {
        mSearchBox.clearSearchable();
        ArrayList<String> history = User.get().getSearchHistory();
        for(int i = 0; i < history.size(); i++){
            SearchResult option = new SearchResult(history.get(i), null);
            mSearchBox.addSearchable(option);
        }
    }

    private void initSearchBox() {
        mSearchBox.enableVoiceRecognition(this);
        mSearchBox.setLogoText(getString(R.string.search));
        mSearchBox.setMenuListener(new SearchBox.MenuListener(){

            @Override
            public void onMenuClick() {
                //Hamburger has been clicked
                Log.v("shush", "onMenuClick");
            }

        });
        mSearchBox.setSearchListener(new SearchBox.SearchListener() {

            @Override
            public void onSearchOpened() {
                //Use this to tint the screen
            }

            @Override
            public void onSearchClosed() {
                //Use this to un-tint the screen
            }

            public void onSearchTermChanged(String var1) {
            }
            @Override
            public void onSearch(String searchTerm) {
                search(searchTerm);
            }

            @Override
            public void onResultClick(SearchResult result) {
                //React to a result being clicked
            }


            @Override
            public void onSearchCleared() {
            }

        });
        mSearchBox.setOverflowMenu(R.menu.overflow_menu);
        mSearchBox.setOverflowMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_clear_history:
                        User.get().clearHistory();
                        refreshSearchHistory();
                        return true;
                    case R.id.action_full_history:
                        Intent intent = new Intent(LandingActivity.this, HistoryActivity.class);
                        startActivityForResult(intent, REQUEST_HISTORY);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_landing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            if(mSearchBox.getVisibility() == View.GONE) {
                mSearchBox.revealFromMenuItem(R.id.action_search, this);
                //mSearchBox.setVisibility(View.VISIBLE);
            }
            mSearchBox.toggleSearch();
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
            case SearchBox.VOICE_RECOGNITION_CODE:
                if (data != null && data.hasExtra(RecognizerIntent.EXTRA_RESULTS)) {
                    ArrayList<String> matches = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (matches.size() > 0) {
                        mSearchBox.populateEditText(matches.get(0));
                    }
                }
                break;
            case REQUEST_HISTORY:
                if (data != null && data.hasExtra(HistoryActivity.EXTRA_QUERY)) {
                    String query = data.getStringExtra(HistoryActivity.EXTRA_QUERY);
                    mSearchBox.hideCircularly(this);
                    search(query);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
