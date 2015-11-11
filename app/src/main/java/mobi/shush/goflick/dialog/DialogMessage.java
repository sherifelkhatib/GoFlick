package mobi.shush.goflick.dialog;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.SparseArrayCompat;

import mobi.shush.goflick.R;

/**
 * Created by User on 10/3/2015.
 */
public class DialogMessage extends GoFlickDialog<GoFlickDialog.CallBack> {
    protected static class Builder extends GoFlickDialog.Builder {
        public Builder(Fragment fragment) {
            super(fragment);
        }

        public Builder(FragmentActivity activity) {
            super(activity);
        }

        @Override
        protected GoFlickDialog instanstiate() {
            return new DialogMessage();
        }
    }

    private static GoFlickDialog build(Builder builder, String title, String message, int button) {
        return builder.setCancelable(false)
                .addButton(R.id.buttoncancel, button)
                .addText(title)
                .addText(message)
                .buildAndShow();
    }

    private static GoFlickDialog build(Builder builder, String title, String message, int button, int id) {
        return builder.setCancelable(false)
                .addButton(R.id.buttoncancel, button)
                .addText(title)
                .addText(message)
                .setId(id)
                .buildAndShow();
    }

    public static GoFlickDialog newInstance(FragmentActivity activity, String title, String message, int button) {
        return build(new Builder(activity), title, message, button);
    }

    public static GoFlickDialog newInstance(Fragment fragment, String title, String message, int button) {
        return build(new Builder(fragment), title, message, button);
    }

    public static GoFlickDialog newInstance(Fragment fragment, String title, String message, int button, int id) {
        return build(new Builder(fragment), title, message, button, id);
    }

    @Override
    protected void onClick(int id, SparseArrayCompat<String> inputs) {
    }
}
