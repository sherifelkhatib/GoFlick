package mobi.shush.goflick.comm.handler;

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.BasicResponseHandler;

import java.io.IOException;

/**
 * Created by Shush on 11/11/2015.
 */
public abstract class Parser {
    public abstract void parse(String result) throws Exception;
}
