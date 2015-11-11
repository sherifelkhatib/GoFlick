package mobi.shush.goflick.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Shush on 11/11/2015.
 */
public class PhotoExtended implements Serializable {
    public long id;
    public String owner;
    public String secret;
    public String server;
    public long dateuploaded;
    public int farm;
    public String title;
    public String description;
    public String originalformat;

    public PhotoExtended(JSONObject o) throws JSONException {
        id = o.getLong("id");
        secret = o.optString("originalsecret", null);
        originalformat = o.optString("originalformat", null);
        server = o.getString("server");
        dateuploaded = o.getLong("dateuploaded")*1000;
        owner = o.getJSONObject("owner").getString("username");
        farm = o.getInt("farm");
        title = o.getJSONObject("title").getString("_content");
        description = o.getJSONObject("description").getString("_content");
    }

    public boolean hasImage() {
        return secret != null;
    }
    public String getImage() {
        return "https://farm"+farm+".staticflickr.com/"+server+"/"+id+"_"+secret+"_o." + originalformat;
    }
}
