package org.mam.kch.fyreagenda;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.mam.kch.fyreagenda.util.Task;
import org.mam.kch.fyreagenda.util.OnStartDragListener;
import org.mam.kch.fyreagenda.util.SimpleItemTouchHelperCallback;

import java.util.ArrayList;

public class TaskListFragment extends Fragment  implements OnStartDragListener {


    private ItemTouchHelper mItemTouchHelper;
    private ArrayList<Task.TaskItem> newList;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    RecyclerListAdapter adapter;

    public TaskListFragment() {

    }

    public void updateList(AppCompatActivity mainActivity) {
        Bundle args = getArguments();
        newList
                = (ArrayList<Task.TaskItem>) args.getSerializable(ARG_SECTION_NUMBER);
        adapter = new RecyclerListAdapter(newList, mainActivity, getContext(), this);
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TaskListFragment newInstance(ArrayList<Task.TaskItem> taskList,
                                               AppCompatActivity mainActivity) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SECTION_NUMBER, taskList);
        fragment.setArguments(args);
        fragment.updateList(mainActivity);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.task_list, container, false);

        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.task_list);
        rv.setHasFixedSize(true);
        rv.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void setUserVisibleHint(boolean isVisiibleToUser) {
        if (!isVisiibleToUser) adapter.finishActionMode();
        super.setUserVisibleHint(isVisiibleToUser);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

}
