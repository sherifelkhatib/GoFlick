package mobi.shush.goflick;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import mobi.shush.goflick.utils.User;


public class HistoryActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    public static final String EXTRA_QUERY = "query";

    ArrayAdapter<String> mAdapter;
    ListView mListView;
    ArrayList<String> mHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mListView = (ListView) findViewById(android.R.id.list);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        refreshAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_clear_history) {
            User.get().clearHistory();
            refreshAdapter();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent data = new Intent();
        data.putExtra(EXTRA_QUERY, mHistory.get(position));
        setResult(RESULT_OK, data);
        finish();
    }
    private void refreshAdapter() {
        mHistory = User.get().getSearchHistory();
        mAdapter.clear();
        mAdapter.addAll(mHistory);
    }
}
