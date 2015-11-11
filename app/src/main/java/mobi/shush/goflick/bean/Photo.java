package mobi.shush.goflick.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Shush on 11/11/2015.
 */
public class Photo {
    public long id;
    public String owner;
    public String secret;
    public String server;
    public int farm;
    public String title;
    public boolean isPublic;
    public boolean isFriend;
    public boolean isFamily;

    public Photo(JSONObject o) throws JSONException {
        id = o.getLong("id");
        owner = o.getString("owner");
        secret = o.getString("secret");
        server = o.getString("server");
        farm = o.getInt("farm");
        title = o.getString("title");
        isPublic = o.getInt("ispublic")==1;
        isFriend = o.getInt("isfriend")==1;
        isFamily = o.getInt("isfamily") == 1;
    }

    public String getImageMedium() {
        return "https://farm"+farm+".staticflickr.com/"+server+"/"+id+"_"+secret+"_m.jpg";
    }
}
