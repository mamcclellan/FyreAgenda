package org.mam.kch.fyreagenda;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.mam.kch.fyreagenda.util.Task;

import java.util.ArrayList;

public class TaskListFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    RecyclerAdapter adapter;

    public TaskListFragment() {

    }

    public void updateList() {
        Bundle args = getArguments();
        ArrayList<Task.TaskItem> newList
                = (ArrayList<Task.TaskItem>) args.getSerializable(ARG_SECTION_NUMBER);
        adapter = new RecyclerAdapter(newList);
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TaskListFragment newInstance(ArrayList<Task.TaskItem> taskList) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SECTION_NUMBER, taskList);
        fragment.setArguments(args);
        fragment.updateList();
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
}
