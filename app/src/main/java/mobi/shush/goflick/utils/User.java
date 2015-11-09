package mobi.shush.goflick.utils;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by karim on 11/9/15.
 */
public class User implements Serializable {
    ArrayList<String> mHistory = new ArrayList<String>();

    public void addSearchToHistory(final String search) {
        search.intern();
        for(int i=mHistory.size() -1; i>=0;i--) {
            if(mHistory.get(i) == search) {
                mHistory.remove(i);
                mHistory.add(0, search);
                return;
            }
        }
        mHistory.add(0, search);
        //no need to do this on the UI thread
        new Thread() {
            @Override
            public void run() {
                super.run();
                //I will limit my search history to 50 (I will remove the last item if I hit this limit)
                if(mHistory.size() > 50)
                    mHistory.remove(mHistory.size() - 1);
                saveSync();
            }
        }.start();

    }
    public ArrayList<String> getSearchHistory() {
        return mHistory;
    }
    public void clearHistory() {
        mHistory.clear();
        saveAsync();
    }
    //Singleton code
    private static User instance;
    private static Context mAppContext;
    private User() {}
    public static User init(Context context) {
        mAppContext = context.getApplicationContext();
        instance = load();
        return instance;
    }
    public static User get() {
        return instance;
    }
    private static User load() {
        User saved = null;
        try {
            //I will not do any checks TODO
            FileInputStream fis = mAppContext.openFileInput("singleton.user");
            ObjectInputStream ois = new ObjectInputStream(fis);
            saved = (User) ois.readObject();
            saved.prepare();
            ois.close();
            fis.close();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        if(saved == null) {
            saved = new User();
        }
        return saved;
    }

    private void prepare() {
        for(int i=0;i<mHistory.size();i++)
            mHistory.get(i).intern();
    }

    private synchronized void saveSync() {
        try {
            FileOutputStream fos = mAppContext.openFileOutput("singleton.user", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
            fos.close();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveAsync() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                saveSync();
            }
        }).start();
    }
}
