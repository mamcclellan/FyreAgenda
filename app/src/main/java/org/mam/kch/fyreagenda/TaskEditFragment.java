package org.mam.kch.fyreagenda;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.mam.kch.fyreagenda.util.Task;

/**
 * Created by Michael on 12/7/2016.
 */

public class TaskEditFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    Spinner spinner;
    EditText nameInput, detailsInput;

    /**
     * The dummy name this fragment is presenting.
     */
    private static Task.TaskItem mItem;
    private static Task.TaskItem oldData;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TaskEditFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItem = Task.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getName());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.task_edit, container, false);
        nameInput = (EditText) rootView.findViewById(R.id.name_input);
        detailsInput = (EditText) rootView.findViewById(R.id.details_input);
        spinner = (Spinner) rootView.findViewById(R.id.task_type_input);
        final String[] selections = {"This week", "Next week", "This month"};
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_spinner_item,
                        selections);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if(mItem.getTaskTypeValue()<3)
            spinner.setSelection(mItem.getTaskTypeValue());

        // Show the dummy name as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.edit_detail)).setText(mItem.getDetails());
            if(mItem.getDetails().equals("")) {
                rootView.findViewById(R.id.edit_detail).setVisibility(View.GONE);
            }
            else {
                rootView.findViewById(R.id.edit_detail).setVisibility(View.VISIBLE);
            }
            nameInput.setText(mItem.getName());
            detailsInput.setText(mItem.getDetails());
        }

        return rootView;
    }

    public void saveData(){
        oldData = Task.cloneTask(mItem);
        if(mItem.getTaskTypeValue()!=spinner.getSelectedItemPosition())
            Task.moveItemToNewList(mItem, spinner.getSelectedItemPosition());
        mItem.setName(nameInput.getText().toString());
        mItem.setDetails(detailsInput.getText().toString());
        mItem.setTaskType(spinner.getSelectedItemPosition());
        Task.saveItem(mItem);
    }
    public void undoSaveData(){
        Task.removeItem(mItem);
        Task.addItemBack(oldData);
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    @Override
    public void onDetach() {
        super.onDetach();
        hideKeyboard(this.getActivity());
    }
}
