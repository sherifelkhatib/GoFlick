package mobi.shush.goflick.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Shush on 11/11/2015.
 */
public class Photo implements Serializable {
    public long id;
    public String owner;
    public String secret;
    public String server;
    public int farm;
    public String title;

    public Photo(JSONObject o) throws JSONException {
        id = o.getLong("id");
        owner = o.getString("owner");
        secret = o.getString("secret");
        server = o.getString("server");
        farm = o.getInt("farm");
        title = o.getString("title");
    }

    public String getImage() {
        return "https://farm"+farm+".staticflickr.com/"+server+"/"+id+"_"+secret+"_m.jpg";
    }
}
