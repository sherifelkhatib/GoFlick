package mobi.shush.goflick.comm;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mobi.shush.goflick.cache.Cacher;
import mobi.shush.goflick.cache.Policy;
import mobi.shush.goflick.comm.handler.Parser;

/**
 * Created by Shush on 11/11/2015.
 */
public class Request<T extends Parser> extends AsyncTask<Void, Void, T> {
	public static <T extends Parser> Request build(String url, boolean post) {
		return new Request<T>(url, post);
	}

	private HashMap<String, String> mParams;
	private HashMap<String, String> mHeaders;
	private String mUrl;
//    private Object mBody;
	private Listener mListener;
	private boolean mPost;
	private T mResponseHandler;
	private Policy mPolicy = Policy.Factory.NeverUseCache();
	private Exception mException;

	private Request(String url, boolean post) {
		mParams = new HashMap<String, String>();
		mHeaders = new HashMap<String, String>();
		mUrl = url;
		mPost = post;
	}

	public Request setListener(Listener<T> listener) {
		mListener = listener;
		return this;
	}
//	public Request setBody(Object object) {
//        mBody = object;
//		return this;
//	}

	public Request cachePolicy(Policy policy) {
		mPolicy = policy;
		return this;
	}


	public Request addParam(String name, String value) {
		mParams.put(name, value);
		return this;
	}

	public Request addHeader(String name, String value) {
		mHeaders.put(name, value);
		return this;
	}

	public Exception getException( ) {
		return mException;
	}

	public Request setParser(T parser) {
		mResponseHandler = parser;
		return this;
	}

	@Override
	protected T doInBackground(Void... params) {
		return executeSync();
	}

	public T executeSync( ) {
		try {
			String cacheKey = formatUrl(mUrl, mParams);
			Log.v("sherif", cacheKey);
			String result = Cacher.get().get(cacheKey, mPolicy);
			if(result != null) {
				try {
					mResponseHandler.parse(result);
					return mResponseHandler;
				} catch(Exception ex) {
					//If there is an exception I will allow the download
					ex.printStackTrace();
				}
			}
			HttpRequestBase method;
			if ( !mPost) {
				mUrl = formatUrl(mUrl, mParams);
				method = new HttpGet(mUrl);
			} else {
                HttpEntity entity;
                method = new HttpPost(mUrl);
//                if(mBody != null) {
//                    method.addHeader("Content-Type", "application/json;charset=UTF-8");
//                    entity = new StringEntity(mapper.writeValueAsString(mBody));
//                }
//                else {
                    entity = new MultipartEntity();
                    MultipartEntity me = (MultipartEntity) entity;
                    for (String key : mParams.keySet()) {
                        me.addPart(key, new StringBody(mParams.get(key)));
                    }
//                }
				( (HttpPost) method ).setEntity(entity);
			}
			for (String key : mHeaders.keySet()) {
				method.addHeader(key, mHeaders.get(key));
			}

			HttpResponse response = new DefaultHttpClient().execute(method);
            result = new BasicResponseHandler().handleResponse(response);
			mResponseHandler.parse(result);
			Cacher.get().set(cacheKey, result);
            Log.v("sherif", result.toString());
            if(mListener != null) {
                mListener.onDoneBackground(this, response, mResponseHandler);
            }
            return mResponseHandler;
		} catch (Exception ex) {
			ex.printStackTrace();
			mException = ex;
			return null;
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mListener.onStart(this);
	}

	@Override
	protected void onPostExecute(T result) {
		super.onPostExecute(result);
		if (mListener != null) {
			if (result == null || mException != null) {
				mException.printStackTrace();
				mListener.onError(this, mException);
			} else {
				mListener.onDone(this, result);
			}
		}
	}

	private static String formatUrl(String url, HashMap<String, String> params) {
		List<NameValuePair> listParams = new ArrayList<NameValuePair>();
		for (String key : params.keySet()) {
			listParams.add(new BasicNameValuePair(key, params.get(key)));
		}
		if (listParams != null && listParams.size() > 0) {
			StringBuilder sb = new StringBuilder(url);
			if ( !url.contains("?")) {
				sb.append('?');
			} else sb.append("&");
			sb.append(URLEncodedUtils.format(listParams, "utf-8"));
			return sb.toString();
		} else {
			return url;
		}
	}

	public interface Listener<T> {
        public void onDoneBackground(Request request, HttpResponse response, T result);

		public void onDone(Request request, T result);

		public void onError(Request request, Exception ex);

		public void onStart(Request request);
	}

	public void run( ) {
		execute();
	}
}
