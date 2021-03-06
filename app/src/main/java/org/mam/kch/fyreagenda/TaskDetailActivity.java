package org.mam.kch.fyreagenda;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import org.mam.kch.fyreagenda.util.Task;

/**
 * An activity representing a single Task detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link TaskListActivity}.
 */
public class TaskDetailActivity extends AppCompatActivity {

    private boolean editMode;
    TaskDetailFragment detailFragment;
    TaskEditFragment editFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        editMode = getIntent().getBooleanExtra("EditMode", false);
        setContentView(R.layout.activity_task_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // In this case, the fragment automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(TaskDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(TaskDetailFragment.ARG_ITEM_ID));
            if (editMode) {
                editFragment = new TaskEditFragment();
                editFragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.task_detail_container, editFragment)
                        .commit();
            }
            else {
                detailFragment = new TaskDetailFragment();
                detailFragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.task_detail_container, detailFragment)
                        .commit();
            }
        }
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editMode) {
                            editFragment.saveData();

                            // Create the detail fragment and add it to the activity
                            // using a fragment transaction.
                            Bundle arguments = new Bundle();
                            arguments.putString(TaskDetailFragment.ARG_ITEM_ID,
                                    getIntent().getStringExtra(TaskDetailFragment.ARG_ITEM_ID));
                            detailFragment = new TaskDetailFragment();
                            detailFragment.setArguments(arguments);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.task_detail_container, detailFragment)
                                    .commit();
                            // Bottom popup allows user to undo.
                            Snackbar snackbar = Snackbar
                                    .make(v, "Task is saved", Snackbar.LENGTH_LONG)
                                    .setAction("UNDO", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            editFragment.undoSaveData();

                                            // Create the detail fragment and add it to the activity
                                            // using a fragment transaction.
                                            Bundle arguments = new Bundle();
                                            arguments.putString(TaskDetailFragment.ARG_ITEM_ID,
                                                    getIntent().getStringExtra(TaskDetailFragment.ARG_ITEM_ID));
                                            detailFragment = new TaskDetailFragment();
                                            detailFragment.setArguments(arguments);
                                            getSupportFragmentManager().beginTransaction()
                                                    .replace(R.id.task_detail_container, detailFragment)
                                                    .commit();

                                            Snackbar snackbar1 = Snackbar.make(view, "Task edit undone!", Snackbar.LENGTH_SHORT);
                                            snackbar1.show();
                                        }
                                    });

                            snackbar.show();
                            fab.setImageDrawable(getDrawable(R.drawable.ic_create_black));

                            editMode = false;
                        }
                        else {
                            Bundle arguments = new Bundle();
                            arguments.putString(TaskDetailFragment.ARG_ITEM_ID,
                                    getIntent().getStringExtra(TaskDetailFragment.ARG_ITEM_ID));
                            editFragment = new TaskEditFragment();
                            editFragment.setArguments(arguments);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.task_detail_container, editFragment)
                                    .commit();
                            fab.setImageDrawable(getDrawable(R.drawable.ic_check_black));

                            editMode = true;
                        }

                    }
                }
        );

        if (editMode) {
            fab.setImageDrawable(getDrawable(R.drawable.ic_check_black));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, TaskListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
