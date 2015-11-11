package mobi.shush.goflick.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.SparseArrayCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

import mobi.shush.goflick.R;

public class GoFlickDialog<T extends GoFlickDialog.CallBack> extends DialogFragment implements View.OnClickListener {
    private static final int TYPE_TEXTVIEW = 0;
    private static final int TYPE_EDITEXT = 1;
    protected static class Builder {
        private Fragment fragment;
        private FragmentActivity activity;
        private boolean callerIsActivity;

        private int id = 0;
        private boolean cancelable;

        private ArrayList<Integer> buttonsIds;
        private ArrayList<Integer> buttons;

        private ArrayList<Integer> types;

        private ArrayList<String> texts;

        private ArrayList<Integer> inputTypes;
        private ArrayList<Integer> inputIds;
        private ArrayList<Integer> inputs;
        private ArrayList<String> inputsValues;
        private Bundle bundle=new Bundle();

        public Builder(Fragment fragment) {
            this.callerIsActivity = false;
            this.fragment = fragment;

        }
        public Builder(FragmentActivity activity) {
            this.callerIsActivity = true;
            this.activity = activity;
        }
        protected String getString(int resId) {
            return callerIsActivity ? activity.getString(resId) : fragment.getString(resId);
        }
        public Builder setId(int id) {
            this.id = id;
            return this;
        }
        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder addArgument(String key, Serializable value) {
            if (value instanceof Serializable)
                bundle.putSerializable(key, value);
            return this;
        }

        public Builder addButton(int id, int text) {
            if(buttonsIds == null) buttonsIds = new ArrayList<Integer>();
            if(buttons == null) buttons = new ArrayList<Integer>();
            buttonsIds.add(id);
            buttons.add(text);
            return this;
        }
        public Builder addText(String text) {
            if(types == null) types = new ArrayList<Integer>();
            types.add(TYPE_TEXTVIEW);
            if(texts == null) texts = new ArrayList<String>();
            texts.add(text);
            return this;
        }
        public Builder addInput(int id, int hint, String text, int type) {
            if(types == null) types = new ArrayList<Integer>();
            types.add(TYPE_EDITEXT);
            if(inputIds == null) inputIds = new ArrayList<Integer>();
            if(inputTypes == null) inputTypes = new ArrayList<Integer>();
            if(inputs == null) inputs = new ArrayList<Integer>();
            if(inputsValues == null) inputsValues = new ArrayList<String>();
            inputTypes.add(type);
            inputIds.add(id);
            inputs.add(hint);
            inputsValues.add(text);
            return this;
        }
        protected GoFlickDialog instanstiate() {
            return new GoFlickDialog();
        }
        private void show(GoFlickDialog dialog) {
            if(activity != null) {
                dialog.show(activity.getSupportFragmentManager(), "dialog");
            }
            else if(fragment != null) {
                dialog.show(fragment.getChildFragmentManager(), "dialog");
            }
            else {
                throw new RuntimeException("No activity or dialog passed to Dialog Builder.");
            }
        }
        public GoFlickDialog build() {
            GoFlickDialog dialog = newInstance(instanstiate(), this);
            return dialog;
        }
        public GoFlickDialog buildAndShow() {
            GoFlickDialog dialog = build();
            show(dialog);
            return dialog;
        }
    }

    protected static GoFlickDialog newInstance(GoFlickDialog fragment, Builder builder) {
        // Supply num input as an argument.
        Bundle args = new Bundle();

        args.putBoolean("callerIsActivity", builder.callerIsActivity);
        args.putBoolean("cancelable", builder.cancelable);
        args.putInt("id", builder.id);

        args.putIntegerArrayList("types", builder.types);

        args.putIntegerArrayList("buttonsIds", builder.buttonsIds);
        args.putIntegerArrayList("buttons", builder.buttons);

        args.putStringArrayList("texts", builder.texts);

        args.putIntegerArrayList("inputIds", builder.inputIds);
        args.putIntegerArrayList("inputTypes", builder.inputTypes);
        args.putStringArrayList("inputsValues", builder.inputsValues);
        args.putIntegerArrayList("inputs", builder.inputs);
        args.putAll(builder.bundle);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.GoFlickDialog);
        setCancelable(getArguments().getBoolean("cancelable"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog, container, false);
        LinearLayout ll;

        ArrayList<Integer> buttons = getArguments().getIntegerArrayList("buttons");
        ArrayList<Integer> buttonsIds = getArguments().getIntegerArrayList("buttonsIds");
        ll= (LinearLayout) v.findViewById(R.id.dialog_ll_buttons);
        boolean cancelAdded = false;
        for(int i = 0; i < buttons.size(); i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            Button b = (Button) inflater.inflate(R.layout.dialog_button, ll, false);
            b.setId(buttonsIds.get(i));
            b.setText(buttons.get(i));
            b.setOnClickListener(this);
            ll.addView(b, params);
        }

        ArrayList<Integer> types = getArguments().getIntegerArrayList("types");

        ArrayList<String> texts = getArguments().getStringArrayList("texts");

        ArrayList<Integer> inputIds = getArguments().getIntegerArrayList("inputIds");
        ArrayList<Integer> inputTypes = getArguments().getIntegerArrayList("inputTypes");
        ArrayList<Integer> inputs = getArguments().getIntegerArrayList("inputs");
        ArrayList<String> inputsValues = getArguments().getStringArrayList("inputsValues");
        int counterText = 0;
        int counterInput = 0;
        ll = (LinearLayout) v.findViewById(R.id.dialog_content);
        for(int i = 0; i < types.size(); i++) {
            View child = null;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            switch (types.get(i)) {
                case TYPE_TEXTVIEW:
                    TextView tv = (TextView) inflater.inflate(R.layout.dialog_text, ll, false);
                    tv.setText(texts.get(counterText));
                    tv.setGravity(Gravity.CENTER);
                    counterText++;
                    child = tv;
                    break;
                case TYPE_EDITEXT:
                    EditText et = (EditText) inflater.inflate(R.layout.dialog_input, ll, false);
                    et.setId(inputIds.get(counterInput));
                    et.setText(inputsValues.get(counterInput));
                    et.setInputType(inputTypes.get(counterInput));
                    et.setHint(inputs.get(counterInput));
                    counterInput++;
                    child = et;
                    break;
            }
            ll.addView(child, params);
        }
        return v;
    }

    protected final int getDialogId() {
        return getArguments().getInt("id");
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        try {
            getCallBack().onCancelDialog(getDialogId());
        } catch (ClassCastException ex) {
            ex.printStackTrace();
        }
    }

    protected T getCallBack() {
        if (getArguments().getBoolean("callerIsActivity")) {
            return (T) getActivity();
        } else {
            return (T) getParentFragment();
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.buttoncancel) {
            getDialog().cancel();
            return;
        }
        SparseArrayCompat<String> inputs = new SparseArrayCompat<String>();
        ArrayList<Integer> inputIds = getArguments().getIntegerArrayList("inputIds");
        if(inputIds != null) {
            for (Integer input : inputIds) {
                inputs.put(input, ((TextView) getView().findViewById(input)).getText().toString());
            }
        }
        try {
            onClick(id, inputs);
        } catch(ClassCastException ex) {
            ex.printStackTrace();
            dismiss();
        }
    }

    protected void onClick(int id, SparseArrayCompat<String> inputs) {
        switch (id) {
        }
    }

    public interface CallBack {
        public void onCancelDialog(int id);
    }
}
