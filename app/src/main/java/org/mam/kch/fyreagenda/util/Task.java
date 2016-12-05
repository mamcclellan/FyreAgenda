package org.mam.kch.fyreagenda.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample name for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class Task {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<TaskItem> ITEMS = new ArrayList<TaskItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, TaskItem> ITEM_MAP = new HashMap<String, TaskItem>();

    private static final int COUNT = 4;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createTaskItem(i));
        }
    }

    private static void addItem(TaskItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static TaskItem createTaskItem(int position) {
        return new TaskItem(String.valueOf(position), "Item " + position, makeDetails(position),
                TaskItem.TaskType.THISWEEK);
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of name.
     */
    public static class TaskItem {

        public enum TaskType {THISWEEK, NEXTWEEK, THISMOTH, ARCHIVED }
        public final String id;
        public String name;
        public String details;
        public long creationTime;
        public long completionTime;
        public TaskType taskType;
        public boolean taskComplete;

        public TaskItem(String id, String name, String details, TaskType taskType) {
            this.id = id;
            this.name = name;
            this.details = details;
            this.taskType = taskType;
            this.creationTime = System.currentTimeMillis();
            this.taskComplete = false;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
