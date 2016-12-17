package org.mam.kch.fyreagenda;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;


import org.mam.kch.fyreagenda.util.Task;

/**
 * An activity representing a list of Tasks. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link TaskDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class TaskListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Task.loadTasks(getApplicationContext());
        setContentView(R.layout.drawer_layout);

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
                fabClicked(view);
            }
        });


        if (findViewById(R.id.task_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void fabClicked(final View view){
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
                                Task.addNewItem(newTask);
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
                                Task.addNewItem(newTask);
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
                                    intent.putExtra("EditMode", true);
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
    @Override

    public void onStart() {
        // Set correct tab on ViewPager
        viewPager.setCurrentItem(currentTab);
        viewPager.getAdapter().notifyDataSetChanged();
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        // Save the current tab's position
        currentTab = viewPager.getCurrentItem();
        super.onPause();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_notifications) {

        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
