package mobi.shush.goflick.comm.handler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import mobi.shush.goflick.bean.Photo;

/**
 * Created by Shush on 11/11/2015.
 */
public class PhotosHandler extends Parser {
    public ArrayList<Photo> photos;
    public int page;
    public int pages;
    public int total;

    @Override
    public void parse(String result) throws Exception {
        JSONObject o = new JSONObject(result).getJSONObject("photos");
        page = o.getInt("page");
        pages = o.getInt("pages");
        total = o.getInt("total");
        JSONArray a = o.getJSONArray("photo");
        photos = new ArrayList<Photo>();
        for(int i=0;i<a.length();i++) {
            photos.add(new Photo(a.getJSONObject(i)));
        }
    }
}
