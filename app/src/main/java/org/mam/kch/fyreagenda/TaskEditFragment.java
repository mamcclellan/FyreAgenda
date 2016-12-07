package org.mam.kch.fyreagenda;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

    /**
     * The dummy name this fragment is presenting.
     */
    private Task.TaskItem mItem;

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
            // Load the dummy name specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load name from a name provider.
            mItem = Task.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
            mItem.setDetails("new details");
            Task.saveItem(mItem);
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
        final Spinner spinner = (Spinner) rootView.findViewById(R.id.task_type_input);
        final String[] selections = {"This week", "Next week", "This month"};
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_spinner_item,
                        selections);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Show the dummy name as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.edit_detail)).setText("Edit stuff here");
        }

        return rootView;
    }
}
