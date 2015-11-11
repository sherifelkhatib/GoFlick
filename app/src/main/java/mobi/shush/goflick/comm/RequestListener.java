package mobi.shush.goflick.comm;

import android.support.v4.app.Fragment;

import org.apache.http.HttpResponse;

import mobi.shush.goflick.R;
import mobi.shush.goflick.base.GoFlickActivity;
import mobi.shush.goflick.dialog.DialogMessage;

/**
 * Created by Shush on 11/11/2015.
 */
public abstract class RequestListener<T> implements Request.Listener<T> {
    GoFlickActivity activity;
    Fragment fragment;
    T result;

    public RequestListener(GoFlickActivity activity) {
        this.activity = activity;
    }

    public RequestListener(Fragment fragment) {
        this.activity = (GoFlickActivity) fragment.getActivity();
        this.fragment = fragment;
    }

    @Override
    public void onStart(Request request) {
        this.activity.showLoader();
    }

    @Override
    public void onDoneBackground(Request request, HttpResponse response, T result) {
        this.result = result;
        onBackground(result);
    }

    @Override
    public final void onDone(Request request, T response) {
        if ((fragment != null && (!fragment.isVisible() || !fragment.isAdded() || fragment.isRemoving() || fragment.isDetached())) || activity.isFinishing()) {
            return;
        }
        onForeground(response);
        this.activity.hideLoader();
    }

    @Override
    public void onError(Request request, Exception ex) {
        showDialog(activity.getString(R.string.error_general_title), activity.getString(R.string.error_general_message) + "\n\n" + ex.getMessage());
        this.activity.hideLoader();
    }

    protected void showDialog(String title, String message) {

        DialogMessage.newInstance(activity, title, message, android.R.string.ok);
      /*  AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(activity.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.setOnDismissListener(listener);
        AlertDialog alert = builder.create();
        alert.show();*/
    }

    public void onBackground(T result) {

    }

    ;


    public void onForeground(T result) {

    }

    ;
}
