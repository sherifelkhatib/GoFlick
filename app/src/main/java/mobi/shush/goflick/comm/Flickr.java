package mobi.shush.goflick.comm;

import mobi.shush.goflick.cache.Policy;
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
        Request<PhotosHandler> r = Request.<PhotosHandler>build(url, false)
                .addParam("method", "flickr.photos.search")
                .addParam("api_key", "378fce68a78b5ea1731b3e910e947c6f")
                .addParam("format", "json")
                .addParam("nojsoncallback", "1")
                .addParam("page", String.valueOf(page))
                .addParam("text", query)
                .cachePolicy(Policy.Factory.AlwaysUseCache())
                .setParser(new PhotosHandler());
        return r;
    }
}
