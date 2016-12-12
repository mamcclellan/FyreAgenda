package org.mam.kch.fyreagenda;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;


import org.mam.kch.fyreagenda.util.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Tasks. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link TaskDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class TaskListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private static boolean mTwoPane;
    ViewPager viewPager;
    private static int currentTab;
    final Context context = this;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        final PagerAdapter pagerAdapter =
                new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(currentTab);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.new_task_prompt, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // get user input and set it to result
                                        // edit text
                                        result =userInput.getText().toString();
                                        final Task.TaskItem newTask;
                                        //create a new TaskItem and assign the task type based on current view int. If archived view, sets to this week.
                                        newTask = Task.createTaskItem(result);
                                        newTask.setTaskType(viewPager.getCurrentItem());
                                        Task.addItem(newTask);
                                        viewPager.getAdapter().notifyDataSetChanged();
                                        Snackbar snackbar = Snackbar
                                                .make(view, "Task Added", Snackbar.LENGTH_LONG)
                                                .setAction("UNDO", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Task.removeItem(newTask);
                                                        viewPager.getAdapter().notifyDataSetChanged();
                                                        Snackbar snackbar1 = Snackbar.make(view, "Task is deleted!", Snackbar.LENGTH_SHORT);
                                                        snackbar1.show();
                                                    }
                                                });
                                        snackbar.show();
                                    }
                                })
                        .setNeutralButton("EDIT",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // get user input and set it to result
                                        // edit text
                                        result =userInput.getText().toString();
                                        final Task.TaskItem newTask;
                                        //create a new TaskItem and assign the task type based on current view int. If archived view, sets to this week.
                                        newTask = Task.createTaskItem(result);
                                        newTask.setTaskType(viewPager.getCurrentItem());
                                        Task.addItem(newTask);
                                        viewPager.getAdapter().notifyDataSetChanged();

                                        // go to edit screen.

                                        if (TaskListActivity.isTwoPane()) {
                                            Bundle arguments = new Bundle();
                                            arguments.putString(TaskEditFragment.ARG_ITEM_ID, newTask.id);
                                            TaskEditFragment fragment = new TaskEditFragment();
                                            fragment.setArguments(arguments);
                                            // Below -- figure out how to incorporate this
                                            //getSupportFragmentManager().beginTransaction()
                                            //.replace(R.id.task_detail_container, fragment)
                                            //.commit();
                                        } else {
                                            Context context = view.getContext();
                                            Intent intent = new Intent(context, TaskDetailActivity.class);
                                            intent.putExtra(TaskEditFragment.ARG_ITEM_ID, newTask.id);

                                            context.startActivity(intent);
                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });


        if (findViewById(R.id.task_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @Override
    public void onStart() {
        // Set correct tab on ViewPager
        viewPager.setCurrentItem(currentTab);
        viewPager.getAdapter().notifyDataSetChanged();
        super.onStart();
    }

    @Override
    public void onPause() {
        // Save the current tab's position
        currentTab = viewPager.getCurrentItem();
        super.onPause();
    }

    public static boolean isTwoPane() {
        return mTwoPane;
    }

    public void refreshRecycleView() {
        viewPager.getAdapter().notifyDataSetChanged();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return TaskListFragment.newInstance(Task.THISWEEK, TaskListActivity.this);
                case 1:
                    return TaskListFragment.newInstance(Task.NEXTWEEK, TaskListActivity.this);
                case 2:
                    return TaskListFragment.newInstance(Task.THISMONTH, TaskListActivity.this);
                case 3:
                    return TaskListFragment.newInstance(Task.ARCHIVE, TaskListActivity.this);
            }

            return TaskListFragment.newInstance(Task.ARCHIVE, TaskListActivity.this);
        }

        // This override is needed to update multiple views at a time
        // Needed since we have multiple fragments operating; they are reloaded upon data change
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "THIS WEEK";
                case 1:
                    return "NEXT WEEK";
                case 2:
                    return "THIS MONTH";
                case 3:
                    return "ARCHIVED";
            }
            return null;
        }


    }
}
