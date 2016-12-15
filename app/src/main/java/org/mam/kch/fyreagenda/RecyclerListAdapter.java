package org.mam.kch.fyreagenda;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.mam.kch.fyreagenda.util.OnStartDragListener;
import org.mam.kch.fyreagenda.util.ItemTouchHelperAdapter;
import org.mam.kch.fyreagenda.util.ItemTouchHelperViewHolder;
import org.mam.kch.fyreagenda.util.Task;

import java.util.Collections;
import java.util.List;

public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private final List<Task.TaskItem> mValues;
    View selectedView;
    private Task.TaskItem selectedItem;
    private Task.TaskItem clonedItem;
    ActionMode mActionMode;
    AppCompatActivity mainActivity;
    private final OnStartDragListener mDragStartListener;

    public RecyclerListAdapter(List<Task.TaskItem> items, AppCompatActivity mainActivity, Context context, OnStartDragListener dragStartListener) {
        mDragStartListener = dragStartListener;
        mValues = items;
        this.mainActivity = mainActivity;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_list_content, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        final int pos = position;
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).getName());

        // Start a drag whenever the handle view it touched
        holder.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });

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
                        clonedItem = Task.cloneTask(selectedItem);
                        selectedView = v;
                        v.setSelected(true);
                        v.setBackgroundColor(Color.argb(150, 0, 0, 255));
                        return true;
                    }
                }
        );
    }

    @Override
    public void onItemDismiss(int position) {
        mValues.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mValues, fromPosition, toPosition);
        Task.updatePositions(mValues.get(0).getTaskType());
        //Task.switchItemPositions(mValues, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Task.TaskItem mItem;
        public final ImageView handleView;

        public ItemViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            handleView = (ImageView) view.findViewById(R.id.handle);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
            notifyDataSetChanged();
        }
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.list_context_menu, menu);
            if (mValues.size() > 0 && mValues.get(0).getTaskType() == Task.TaskType.ARCHIVED) {
                menu.removeItem(R.id.archive);
            }
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
            View v = mainActivity.findViewById(R.id.app_bar);
            switch (item.getItemId()) {
                case R.id.delete:
                    Task.deleteItem(selectedItem);
                    ((TaskListActivity) mainActivity).refreshRecycleView();
                    Snackbar snackbar = Snackbar
                            .make(v, "Task deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Task.addItemBack(clonedItem);
                                    Task.saveItem(clonedItem);
                                    ((TaskListActivity) mainActivity).refreshRecycleView();
                                    Snackbar snackbar1 = Snackbar.make(view, "Task delete undone!", Snackbar.LENGTH_SHORT);
                                    snackbar1.show();
                                }
                            });
                    snackbar.show();
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                case R.id.archive:
                    Task.archiveItem(selectedItem);
                    ((TaskListActivity) mainActivity).refreshRecycleView();
                    snackbar = Snackbar
                            .make(v, "Task archived", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Task.removeItem(selectedItem);
                                    Task.addItemBack(clonedItem);
                                    Task.saveItem(clonedItem);
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
            selectedView.setBackgroundColor(Color.TRANSPARENT);
            mActionMode = null;
        }
    };
}