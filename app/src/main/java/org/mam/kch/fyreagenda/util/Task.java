package org.mam.kch.fyreagenda.util;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
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

    public enum TaskType {THISWEEK(1), NEXTWEEK(2), THISMONTH(3), ARCHIVED(4);
        private int value;
        TaskType(int value){
            this.value = value;
        }
    }

    /**
     * An array of sample (dummy) items.
     */
    public static final ArrayList<TaskItem> THISWEEK = new ArrayList<TaskItem>();
    public static final ArrayList<TaskItem> NEXTWEEK = new ArrayList<TaskItem>();
    public static final ArrayList<TaskItem> THISMONTH = new ArrayList<TaskItem>();
    public static final ArrayList<TaskItem> ARCHIVE = new ArrayList<TaskItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, TaskItem> ITEM_MAP = new HashMap<String, TaskItem>();

    private static int COUNT = 0;

    static {
        // Add some sample items.
        // This is called immediately on program loading for development purposes
        for (int i = 1; i <= 5; i++) {
            addItem(createTaskItem());
        }
        for (int i = 1; i <= 5; i++) {
            Task.COUNT++;
            TaskItem item = new TaskItem(String.valueOf(Task.COUNT), "Next Week " + Task.COUNT,
                    makeDetails(Task.COUNT), TaskType.NEXTWEEK);
            addItem(item);
        }
        for (int i = 1; i <= 5; i++) {
            Task.COUNT++;
            TaskItem item = new TaskItem(String.valueOf(Task.COUNT), "This month " + Task.COUNT,
                    makeDetails(Task.COUNT), TaskType.THISMONTH);
            addItem(item);
        }
        for (int i = 1; i <= 5; i++) {
            Task.COUNT++;
            TaskItem item = new TaskItem(String.valueOf(Task.COUNT), "ARCHIVE " + Task.COUNT,
                    makeDetails(Task.COUNT), TaskType.ARCHIVED);
            addItem(item);
        }
    }

    public static void addItem(TaskItem item) {
        ITEM_MAP.put(item.id, item);
        if(item.getTaskType()==1)
            THISWEEK.add(item);
        if(item.getTaskType()==2)
            NEXTWEEK.add(item);
        if(item.getTaskType()==3)
            THISMONTH.add(item);
        if(item.getTaskType()==4)
            ARCHIVE.add(item);
    }

    public static void saveItem(TaskItem item){
        ITEM_MAP.put(item.id,item);
    }



    public static TaskItem createTaskItem() {
        Task.COUNT++;
        return new TaskItem(String.valueOf(Task.COUNT), "Item " + Task.COUNT,
                makeDetails(Task.COUNT), TaskType.THISWEEK);
    }

    public static TaskItem createTaskItem(String name) {
        Task.COUNT++;
        if(name.equals("")){
            return new TaskItem(String.valueOf(Task.COUNT), "Item " + Task.COUNT,
                    makeDetails(Task.COUNT), TaskType.THISWEEK);
        }
        return new TaskItem(String.valueOf(Task.COUNT), name,
                "No details set.", TaskType.THISWEEK);
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        builder.append("\nMore details information here.");

        return builder.toString();
    }

    /**
     * A dummy item representing a piece of name.
     */
    public static class TaskItem implements Serializable, Parcelable{

        public final String id;
        public String name;
        public String details;
        public long creationTime;
        public long completionTime;
        TaskType taskType;
        public boolean taskComplete;

        public TaskItem(String id, String name, String details, TaskType taskType) {
            this.id = id;
            this.name = name;
            this.details = details;
            this.taskType = taskType;
            this.creationTime = System.currentTimeMillis();
            this.completionTime = 0;
            this.taskComplete = false;
        }


        public int getTaskType(){return this.taskType.value;}


        public void setDetails(String details){
            this.details = details;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        private TaskItem(Parcel source) {
            this.id = source.readString();
            this.name = source.readString();
            this.details = source.readString();
            int typeIndex = source.readInt();
            switch (typeIndex) {
                case 1:
                    this.taskType = TaskType.THISWEEK;
                case 2:
                    this.taskType = TaskType.NEXTWEEK;
                case 3:
                    this.taskType = TaskType.THISMONTH;
                case 4:
                    this.taskType = TaskType.ARCHIVED;
            }
            this.creationTime = source.readLong();
            this.completionTime = source.readLong();
            this.taskComplete = source.readByte() != 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            // must be written in same order as read
            dest.writeString(this.id);
            dest.writeString(this.name);
            dest.writeString(this.details);
            dest.writeInt(this.taskType.value);
            dest.writeLong(this.creationTime);
            dest.writeLong(this.completionTime);
            dest.writeByte((byte) (this.taskComplete ? 1 : 0));
        }

        public static final Creator<TaskItem> CREATOR
                = new Creator<TaskItem>() {
            @Override
            public TaskItem createFromParcel(Parcel source) {
                return new TaskItem(source);
            }

            @Override
            public TaskItem[] newArray(int size) {
                return new TaskItem[size];
            }
        };
    }
}
