package org.mam.kch.fyreagenda;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.mam.kch.fyreagenda.util.Task;

import java.util.List;

public class RecyclerAdapter
        extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private final List<Task.TaskItem> mValues;
    private Task.TaskItem selectedItem;
    ActionMode mActionMode;
    AppCompatActivity mainActivity;

    public RecyclerAdapter(List<Task.TaskItem> items, AppCompatActivity mainActivity) {
        mValues = items;
        this.mainActivity = mainActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final int pos = position;
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).getName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TaskListActivity.isTwoPane()) {
                    Bundle arguments = new Bundle();
                    arguments.putString(TaskDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                    TaskDetailFragment fragment = new TaskDetailFragment();
                    fragment.setArguments(arguments);
                    // Below -- figure out how to incorporate this
                    //getSupportFragmentManager().beginTransaction()
                    //.replace(R.id.task_detail_container, fragment)
                    //.commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, TaskDetailActivity.class);
                    intent.putExtra(TaskDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                    context.startActivity(intent);
                }
            }
        });

        holder.mView.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        //mainActivity.registerForContextMenu(v);
                        if (mActionMode != null) {
                            return false;
                        }
                        mActionMode = mainActivity.startActionMode(mActionModeCallback);
                        selectedItem = mValues.get(pos);
                        v.setSelected(true);
                        return true;
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Task.TaskItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.list_context_menu, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            final int savedPosition;
            View v = mainActivity.findViewById(R.id.app_bar);
            switch (item.getItemId()) {
                case R.id.delete:
                    savedPosition = mValues.indexOf(selectedItem);
                    Task.removeItem(selectedItem);
                    ((TaskListActivity) mainActivity).refreshRecycleView();
                    Snackbar snackbar = Snackbar
                            .make(v, "Task deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Task.addItemBack(selectedItem, savedPosition);
                                    ((TaskListActivity) mainActivity).refreshRecycleView();
                                    Snackbar snackbar1 = Snackbar.make(view, "Task delete undone!", Snackbar.LENGTH_SHORT);
                                    snackbar1.show();
                                }
                            });
                    snackbar.show();
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                case R.id.archive:
                    savedPosition = mValues.indexOf(selectedItem);
                    final Task.TaskType savedType = selectedItem.getTaskType();
                    Task.removeItem(selectedItem);
                    selectedItem.setTaskType(Task.TaskType.ARCHIVED);
                    Task.addItem(selectedItem);
                    ((TaskListActivity) mainActivity).refreshRecycleView();
                    snackbar = Snackbar
                            .make(v, "Task archived", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Task.removeItem(selectedItem);
                                    selectedItem.setTaskType(savedType);
                                    // This is inefficient, so clean up when not so lazy
                                    Task.addItemBack(selectedItem, savedPosition);
                                    mValues.remove(selectedItem);
                                    mValues.add(savedPosition, selectedItem);
                                    ((TaskListActivity) mainActivity).refreshRecycleView();
                                    Snackbar snackbar1 = Snackbar.make(view, "Task archive undone!", Snackbar.LENGTH_SHORT);
                                    snackbar1.show();
                                }
                            });
                    snackbar.show();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };
}