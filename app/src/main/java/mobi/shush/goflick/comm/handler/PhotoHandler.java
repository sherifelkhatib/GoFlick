package mobi.shush.goflick.comm.handler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import mobi.shush.goflick.bean.Photo;
import mobi.shush.goflick.bean.PhotoExtended;

/**
 * Created by Shush on 11/11/2015.
 */
public class PhotoHandler extends Parser {
    public PhotoExtended photo;

    @Override
    public void parse(String result) throws Exception {
        JSONObject o = new JSONObject(result).getJSONObject("photo");
        photo = new PhotoExtended(o);
    }
}
