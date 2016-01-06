package org.redcross.openmapkit.tagswipe;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.redcross.openmapkit.R;
import org.redcross.openmapkit.odkcollect.tag.ODKTag;
import org.redcross.openmapkit.odkcollect.tag.ODKTagItem;

import java.util.Collection;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SelectMultipleTagValueFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SelectMultipleTagValueFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectMultipleTagValueFragment extends Fragment {

    private static final String IDX = "IDX";

    private TagEdit tagEdit;
    private View rootView;

    private OnFragmentInteractionListener mListener;

    public SelectMultipleTagValueFragment() {
        // Required empty public constructor
    }


    public static SelectMultipleTagValueFragment newInstance(int idx) {
        SelectMultipleTagValueFragment fragment = new SelectMultipleTagValueFragment();
        Bundle args = new Bundle();
        args.putInt(IDX, idx);
        fragment.setArguments(args);
        return fragment;
    }

    private void setupWidgets() {
        TextView tagKeyLabelTextView = (TextView) rootView.findViewById(R.id.tagKeyLabelTextView);
        TextView tagKeyTextView = (TextView) rootView.findViewById(R.id.tagKeyTextView);

        String keyLabel = tagEdit.getTagKeyLabel();
        String key = tagEdit.getTagKey();

        if (keyLabel != null) {
            tagKeyLabelTextView.setText(keyLabel);
            tagKeyTextView.setText(key);
        } else {
            tagKeyLabelTextView.setText(key);
            tagKeyTextView.setText("");
        }

        setupCheckBoxes();
    }

    @SuppressWarnings("ResourceType")
    private void setupCheckBoxes() {
        final LinearLayout checkboxLinearLayout = (LinearLayout)rootView.findViewById(R.id.checkboxLinearLayout);
        final Activity activity = getActivity();
        ODKTag odkTag = tagEdit.getODKTag();
        if (odkTag == null) return;

        /**
         * Setting up buttons with prescribed choice values.
         */
        String prevTagVal = tagEdit.getTagVal();
        boolean prevTagValInTagItems = false;
        Collection<ODKTagItem> odkTagItems = odkTag.getItems();
        int id = 1;
        for (ODKTagItem item : odkTagItems) {
            String label = item.getLabel();
            String value = item.getValue();
            if (value.equals(prevTagVal)) {
                prevTagValInTagItems = true;
            }
            CheckBox checkBox = new CheckBox(activity);
            checkBox.setTextSize(18);
            TextView textView = new TextView(activity);
            textView.setPadding(66, 0, 0, 25);
            textView.setOnClickListener(new TextViewOnClickListener(checkBox));
            if (label != null) {
                checkBox.setText(label);
                textView.setText(value);
            } else {
                checkBox.setText(value);
                textView.setText("");
            }
            checkboxLinearLayout.addView(checkBox);
            if (prevTagVal != null && value.equals(prevTagVal)) {
                checkBox.toggle();
            }
            checkBox.setId(id);
            odkTag.putButtonIdToTagItemHash(id++, item);
            odkTag.addCheckbox(checkBox);
            checkboxLinearLayout.addView(textView);
        }

        final CheckBox editTextCheckBox = new CheckBox(activity);
        final EditText editText = new EditText(activity);
        editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if (!prevTagValInTagItems && prevTagVal != null) {
            editText.setText(prevTagVal);
            editTextCheckBox.setChecked(true);
        }
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    editTextCheckBox.setChecked(true);
                } else {
                    editTextCheckBox.setChecked(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        editTextCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextCheckBox.isChecked()) {
                    editText.setFocusableInTouchMode(true);
                    editText.requestFocus();
                    final InputMethodManager inputMethodManager = (InputMethodManager) activity
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });
        tagEdit.setupEditCheckbox(editTextCheckBox, editText);

        LinearLayout customLinearLayout = new LinearLayout(activity);
        customLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        customLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        customLinearLayout.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        customLinearLayout.setFocusableInTouchMode(true);
        customLinearLayout.addView(editTextCheckBox);
        customLinearLayout.addView(editText);
        checkboxLinearLayout.addView(customLinearLayout);

    }

    /**
     * Allows us to pass a CheckBox as a parameter to onClick
     */
    private class TextViewOnClickListener implements View.OnClickListener {
        CheckBox checkBox;

        public TextViewOnClickListener(CheckBox cb) {
            checkBox = cb;
        }

        @Override
        public void onClick(View v) {
            checkBox.toggle();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int idx = getArguments().getInt(IDX);
            tagEdit = TagEdit.getTag(idx);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_select_multiple_tag_value, container, false);
        setupWidgets();
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}