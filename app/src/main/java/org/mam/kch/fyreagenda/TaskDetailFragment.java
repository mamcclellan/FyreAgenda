package org.mam.kch.fyreagenda;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Date;
import org.mam.kch.fyreagenda.util.Task;

/**
 * A fragment representing a single Task detail screen.
 * This fragment is either contained in a {@link TaskListActivity}
 * in two-pane mode (on tablets) or a {@link TaskDetailActivity}
 * on handsets.
 */
public class TaskDetailFragment extends Fragment {
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
    public TaskDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy name specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load name from a name provider.
            mItem = Task.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getName());}
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.task_detail, container, false);

        // Show the dummy name as text in a TextView.
        if (mItem != null) {
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getName());}
            ((TextView) rootView.findViewById(R.id.task_details)).setText(mItem.getDetails());
            ((TextView) rootView.findViewById(R.id.task_TaskType)).setText("Current list: " + mItem.getTaskType().getName());
            ((TextView) rootView.findViewById(R.id.task_CreationTime)).setText("Creation time: " + new Date(mItem.getCreationTime()).toString());
            if(mItem.getTaskType() == Task.TaskType.ARCHIVED)
                ((TextView) rootView.findViewById(R.id.task_CompletionTime)).setText("Completion time: " + new Date(mItem.getCreationTime()).toString());
            else
                ((TextView) rootView.findViewById(R.id.task_CompletionTime)).setText("Completion time: Not Complete");


        }

        return rootView;
    }
}
