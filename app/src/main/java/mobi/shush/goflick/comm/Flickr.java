package mobi.shush.goflick.comm;

import mobi.shush.goflick.bean.Photo;
import mobi.shush.goflick.cache.Policy;
import mobi.shush.goflick.comm.handler.Parser;
import mobi.shush.goflick.comm.handler.PhotoHandler;
import mobi.shush.goflick.comm.handler.PhotosHandler;

/**
 * Created by Shush on 11/11/2015.
 */
public class Flickr {
    protected static String url = "https://api.flickr.com/services/rest/";
    private final static String URL_SALE = "sale";
    private final static String URL_ITEM = "items";
    private final static String URL_AUTH = "auth";
    public final static String URL_TRANSACTION = "transaction";
    private final static String URL_DIRECT_TOPUP = "directTopup";
    private final static String URL_CREDIT_TRANSFER = "creditTransfer";
    private final static String URL_DATA = "data";

    /**
     * branchUrl use to direct api to read from omt,branch or bank
     *
     * @param query
     * @return
     */
    public static Request<PhotosHandler> search(String query) {
        return search(query, 1);
    }
    public static Request<PhotosHandler> search(String query, int page) {
        Request<PhotosHandler> r = Flickr.<PhotosHandler>build("flickr.photos.search")
                .addParam("page", String.valueOf(page))
                .addParam("text", query)
                .cachePolicy(Policy.Factory.AlwaysUseCache())
                .setParser(new PhotosHandler());
        return r;
    }
    public static Request<PhotoHandler> details(Photo photo) {
        Request<PhotoHandler> r = Flickr.<PhotoHandler>build("flickr.photos.getInfo")
                .addParam("photo_id", String.valueOf(photo.id))
                .addParam("secret", photo.secret)
                .cachePolicy(Policy.Factory.AlwaysUseCache())
                .setParser(new PhotoHandler());
        return r;
    }
    private static <T extends Parser>Request build(String api) {
        return Request.<T>build(url, false)
                .addParam("method", api)
                .addParam("api_key", "378fce68a78b5ea1731b3e910e947c6f")
                .addParam("format", "json")
                .addParam("nojsoncallback", "1");
    }
}
