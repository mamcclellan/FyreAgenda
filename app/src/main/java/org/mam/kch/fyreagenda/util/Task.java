package org.mam.kch.fyreagenda.util;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for providing sample name for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class Task {

    public enum TaskType {THISWEEK(0, "This Week"), NEXTWEEK(1, "Next Week"), THISMONTH(2, "This Month"), ARCHIVED(3, "Archived");
        private int value;
        private String name;
        TaskType(int value, String name){
            this.value = value;
            this.name = name;
        }
        public String getName(){
            return this.name;
        }
        public int getValue(){
            return this.value;
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

    public static ArrayList<Task.TaskItem> getList(int i){
        if(i==1)
            return NEXTWEEK;
        else if(i==2)
            return THISMONTH;
        else if(i==3)
            return ARCHIVE;
        else
            return THISWEEK;
    }

    // Helpful!
    public static void addItemBack(TaskItem item, int pos) {
        if(Integer.parseInt(item.id)<Task.COUNT) {
            // this is the reason we need the pos value stored in the TaskItem.
            Task.getList(item.getTaskTypeValue()).add(pos, item);
            return;
        }
        else
            addItem(item);
    }
    public static void addItem(TaskItem item) {
        ITEM_MAP.put(item.id, item);
        Task.getList(item.getTaskType().getValue()).add(item);
    }

    public static void removeItem(TaskItem item){
        // removes all items with the same id as item from list, but not the map
        // thisList selects the list to find item on.
        for(int thisList = 0;thisList< 4; thisList++) {
            // thisTask is the position in the array list of the task
            for (int thisTask = 0; thisTask < Task.getList(thisList).size(); thisTask++) {
                if(Task.getList(thisList).get(thisTask).id == item.id) {
                    Task.getList(thisList).remove(thisTask);
                    thisTask--;
                }
            }
        }
    }

    private static void updateItemListing(TaskItem item){
        // checks to see if in same list, if so updates that entry
        for(int i = 0;i< Task.getList(item.getTaskTypeValue()).size(); i++) {
            if (Task.getList(item.getTaskTypeValue()).get(i).id == item.id) {
                Task.getList(item.getTaskTypeValue()).set(i,item);
                ITEM_MAP.put(item.id, item);
                return;
            }
        }
        // else, removes item from all lists and adds item back in at end of list specified by item
        Task.removeItem(item);
        Task.addItem(item);
    }

    public static void saveItem(TaskItem item){
        if(item.isNewTaskType()){
            removeItem(ITEM_MAP.get(item.id));
            item.saved();
            if(item.getTaskType() == TaskType.ARCHIVED)
                item.setTaskComplete();
            addItem(item);}
        else if(item.isEdited()){
            item.saved();
            updateItemListing(item);
        }
    }

    // Creates a clone of item and returns it. Does not increase TaskItem count (Task.COUNT)
    public static TaskItem cloneTask(TaskItem item){
        Task.TaskItem clone = new Task.TaskItem(item.id, item.name, item.details,item.taskType);
        clone.creationTime = item.creationTime;
        clone.completionTime = item.completionTime;
        clone.taskComplete = item.taskComplete;
        clone.edited = true;
        clone.newTaskType = item.newTaskType;
        return clone;
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
        private String name;
        private String details;
        private long creationTime;
        private long completionTime;
        private TaskType taskType;
        private boolean taskComplete;
        private boolean edited;
        private boolean newTaskType;

        private TaskItem(String id, String name, String details, TaskType taskType) {
            this.id = id;
            this.name = name;
            this.details = details;
            this.taskType = taskType;
            this.creationTime = System.currentTimeMillis();
            this.completionTime = 0;
            this.taskComplete = false;
            this.edited = false;
            this.newTaskType = false;
        }

        public String getName(){
            return this.name;}
        public String getDetails(){
            return this.details;}
        public int getTaskTypeValue(){
            return this.taskType.value;}
        public TaskType getTaskType(){
            return this.taskType;}

        public void setName(String name) {
            this.name = name;
        }
        public void setDetails(String details){
            this.edited = true;
            this.details = details;}

        public void setTaskType(TaskType taskType){
            if(this.taskType != taskType){
                this.edited = true;
                this.newTaskType = true;
            }
            this.taskType = taskType;}
        public void setTaskType(int i){
            if(this.taskType.value != i){
                this.edited = true;
                this.newTaskType = true;
                if(i == 0)
                    this.taskType = TaskType.THISWEEK;
                if(i == 1)
                    this.taskType = TaskType.NEXTWEEK;
                if(i == 2)
                    this.taskType = TaskType.THISMONTH;
            }}

        public void setTaskComplete(){
            this.edited = true;
            this.completionTime = System.currentTimeMillis();
            this.taskComplete = true;}
        public boolean getTaskComplete(){
            return this.taskComplete;}

        public long getCompletionTime(){
            return this.completionTime;}

        public long getCreationTime(){
            return this.creationTime;}

        public boolean isEdited(){
            return this.edited;}

        public boolean isNewTaskType(){
            return this.newTaskType;}

        public void saved(){
            this.newTaskType = false;
            this.edited = false;}

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
