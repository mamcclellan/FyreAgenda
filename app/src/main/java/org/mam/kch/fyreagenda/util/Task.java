package org.mam.kch.fyreagenda.util;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class Task {

    public enum TaskType {THISWEEK(0, "This Week"), NEXTWEEK(1, "Next Week"), THISMONTH(2, "This Month"), ARCHIVED(3, "Archived"), DELETED(4, "Deleted");
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

    public static final ArrayList<TaskItem> THISWEEK = new ArrayList<>();
    public static final ArrayList<TaskItem> NEXTWEEK = new ArrayList<>();
    public static final ArrayList<TaskItem> THISMONTH = new ArrayList<>();
    public static final ArrayList<TaskItem> ARCHIVE = new ArrayList<>();
    private static DatabaseHandler database;

    public static final Map<String, TaskItem> ITEM_MAP = new HashMap<>();


    public static ArrayList<Task.TaskItem> getList(int i){
        if(i==0)
            return THISWEEK;
        else if(i==1)
            return NEXTWEEK;
        else if(i==2)
            return THISMONTH;
        else if(i==3)
            return ARCHIVE;
        else if(i==4)
            // Item is marked deleted. No list to put on.
            return null;
        else
            return null;
    }

    public static void loadTasks(Context context) {

        if (database == null) {
            database = new DatabaseHandler(context);
        }

        if (!database.loadTasks()) {
            int COUNT = 0;
            for (int i = 1; i <= 5; i++) {
                COUNT++;
                TaskItem item = new TaskItem("This Week " + COUNT,
                        makeDetails(), TaskType.THISWEEK);
                addNewItem(item);
            }
            for (int i = 1; i <= 5; i++) {
                COUNT++;
                TaskItem item = new TaskItem("Next Week " + COUNT,
                        makeDetails(), TaskType.NEXTWEEK);
                addNewItem(item);
            }
            for (int i = 1; i <= 5; i++) {
                COUNT++;
                TaskItem item = new TaskItem("This month " + COUNT,
                        makeDetails(), TaskType.THISMONTH);
                addNewItem(item);
            }
            for (int i = 1; i <= 5; i++) {
                COUNT++;
                TaskItem item = new TaskItem("ARCHIVE " + COUNT,
                        makeDetails(), TaskType.ARCHIVED);
                addNewItem(item);
            }
        }

        sortLists();
    }

    private static void sortLists() {
        Collections.sort(Task.THISWEEK);
        Collections.sort(Task.NEXTWEEK);
        Collections.sort(Task.THISMONTH);
        Collections.sort(Task.ARCHIVE);
    }

    public static void addItemBack(TaskItem item) {
        if(Task.getList(item.getTaskTypeValue())!=null){
            Task.getList(item.getTaskTypeValue()).add(item.getListPosition(), item);
            Task.updatePositions(item.getTaskType());
        }
    }

    public static void moveItemToNewList(TaskItem item, TaskType taskType){
        if(item.getTaskType() == taskType)
            return;
        ArrayList<TaskItem> listToPutItem = Task.getList(taskType.getValue());
        ArrayList<TaskItem> listItemIsFrom = Task.getList(item.getTaskTypeValue());
        if(listItemIsFrom!=null) {
            listItemIsFrom.remove(item);
            Task.updatePositions(item.getTaskType());
        }
        if(listToPutItem != null) {
            item.setTaskType(taskType);
            item.setListPosition(listToPutItem.size());
            listToPutItem.add(0, item);
            Task.updatePositions(taskType);
        }
    }
    public static void moveItemToNewList(TaskItem item, int taskTypeValue){
        TaskType taskType;
        switch (taskTypeValue) {
            case 0:
                taskType = TaskType.THISWEEK;
                break;
            case 1:
                taskType = TaskType.NEXTWEEK;
                break;
            case 2:
                taskType = TaskType.THISMONTH;
                break;
            case 3:
                taskType = TaskType.ARCHIVED;
                break;
            case 4:
                taskType = TaskType.DELETED;
                break;
            default:
                taskType = TaskType.ARCHIVED;
        }
        moveItemToNewList(item, taskType);
    }
    public static void archiveItem(TaskItem item) {
        if(Task.getList(item.getTaskTypeValue())!=null) {
            ArrayList<Task.TaskItem> list = Task.getList(item.getTaskType().getValue());
            list.remove(item);
            Task.updatePositions(item.getTaskType());
        }
        item.completeTask();
        item.setTaskType(TaskType.ARCHIVED);
        item.setListPosition(Task.ARCHIVE.size());
        Task.ARCHIVE.add(item);
        Task.updatePositions(TaskType.ARCHIVED);
    }

    public static void updatePositions(TaskType taskType) {
        // Any time an item is moved from or to a list, this should be called on the list.
        ArrayList<TaskItem>list = Task.getList(taskType.getValue());
        for(TaskItem item: list){
            item.setListPosition(list.indexOf(item));
            ITEM_MAP.put(item.getID(),item);
            database.updateTask(item);
        }
    }

    public static void addNewItem(TaskItem item) {
        Task.getList(item.getTaskType().getValue()).add(0,item);
        database.addTask(item);
        ITEM_MAP.put(String.valueOf(item.id), item);
        Task.updatePositions(item.getTaskType());
    }

    public static void moveCheckedTask(TaskItem item){
        List<TaskItem> list = Task.getList(item.getTaskType().getValue());
        if(item.getListPosition()==list.size()-1)
            return;
        int where = item.getListPosition();
        list.remove(item);
        while(!list.get(where).getTaskComplete()){
            where++;
            if(where>=list.size())
                break;
        }
        list.add(where,item);
        Task.updatePositions(item.getTaskType());

    }

    public static void moveUncheckedTask(TaskItem item){
        if(item.getListPosition()==0)
            return;
        List<TaskItem> list = Task.getList(item.getTaskType().getValue());
        int where = item.getListPosition()-1;
        list.remove(item);
        while(list.get(where).getTaskComplete()){
            where--;
            if(where<0)
                break;
        }
        list.add(where+1,item);
        Task.updatePositions(item.getTaskType());
    }


    public static void addItemFromDatabase(TaskItem item) {
        if(Task.getList(item.getTaskTypeValue())!=null) {
            Task.getList(item.getTaskTypeValue()).add(item);
        }
        ITEM_MAP.put(String.valueOf(item.id), item);
    }

    public static void removeItem(TaskItem item) {
        if(Task.getList(item.getTaskType().getValue())!=null) {
            ArrayList<Task.TaskItem> list = Task.getList(item.getTaskType().getValue());
            list.remove(item);
            Task.updatePositions(item.getTaskType());
        }
        item.setTaskType(TaskType.DELETED);
        ITEM_MAP.put(item.getID(),item);
        database.deleteTask(item);
    }

    public static void deleteItem(TaskItem item) {
        ArrayList<Task.TaskItem> list = Task.getList(item.getTaskType().getValue());
        list.remove(item);
        Task.updatePositions(item.getTaskType());
        ITEM_MAP.remove(item.getID());
        database.deleteTask(item);
    }

    public static void saveItem(TaskItem item){
        if(item.getTaskType() == TaskType.ARCHIVED)
            Task.archiveItem(item);
        else {
            database.updateTask(item);
            ITEM_MAP.put(item.getID(), item);
        }
    }

    // Creates a clone of item and returns it. Does not increase TaskItem count (Task.COUNT)
    public static TaskItem cloneTask(TaskItem item){
        Task.TaskItem clone = new Task.TaskItem(item.name, item.details,item.taskType);
        clone.id = item.id;
        clone.setListPosition(item.getListPosition());
        clone.creationTime = item.creationTime;
        clone.completionTime = item.completionTime;
        clone.taskComplete = item.taskComplete;
        return clone;
    }

    public static TaskItem createTaskItem(String name) {
        if(name.equals("")){
            return new TaskItem("New Item",
                    "", TaskType.THISWEEK);
        }
        return new TaskItem(name,
                "", TaskType.THISWEEK);
    }

    private static String makeDetails() {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ");
        builder.append("\nMore details information here.");

        return builder.toString();
    }

    public static class TaskItem implements Serializable, Parcelable, Comparable{

        public String id;
        private String name;
        private String details;
        private long creationTime;
        private long completionTime;
        private TaskType taskType;
        private boolean taskComplete;
        private int listPosition;

        TaskItem() {

        }

        public TaskItem(String name, String details, TaskType taskType) {
            this.name = name;
            this.details = details;
            this.taskType = taskType;
            this.creationTime = System.currentTimeMillis();
            this.completionTime = 0;
            this.taskComplete = false;
        }

        String getID() {
            return this.id;
        }

        void setID(String id) {
            this.id = id;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDetails() {
            return this.details;
        }

        public int getTaskTypeValue() {
            return this.taskType.value;
        }


        public void setDetails(String details) {
            this.details = details;
        }

        public TaskType getTaskType() {
            return this.taskType;
        }

        public void setTaskType(TaskType taskType) {
            this.taskType = taskType;
        }

        public void setTaskType(int i){
            if(this.taskType.value != i){
                if(i == 0)
                    this.taskType = TaskType.THISWEEK;
                if(i == 1)
                    this.taskType = TaskType.NEXTWEEK;
                if(i == 2)
                    this.taskType = TaskType.THISMONTH;
            }}

        public boolean getTaskComplete() {
            return this.taskComplete;
        }

        public void setTaskComplete(boolean taskComplete) {
            this.taskComplete = taskComplete;
        }

        public long getCompletionTime() {
            return this.completionTime;
        }

        void setCompletionTime(long completionTime) {
            this.completionTime = completionTime;
        }

        public long getCreationTime() {
            return this.creationTime;
        }

        void setCreationTime(long creationTime) {
            this.creationTime = creationTime;
        }

        public int getListPosition() {
            return listPosition;
        }

        public void setListPosition(int listPosition) {
            this.listPosition = listPosition;
        }

        public void completeTask() {
            this.setTaskComplete(true);
            this.setCompletionTime(System.currentTimeMillis());
        }

        public void restartTask() {
            this.setTaskComplete(false);
            this.setCompletionTime(0);
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
                case 0:
                    this.taskType = TaskType.THISWEEK;
                case 1:
                    this.taskType = TaskType.NEXTWEEK;
                case 2:
                    this.taskType = TaskType.THISMONTH;
                case 3:
                    this.taskType = TaskType.ARCHIVED;
                case 4:
                    this.taskType = TaskType.DELETED;
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


        @Override
        public int compareTo(Object taskItem) {
            return this.getListPosition() - ((TaskItem) taskItem).getListPosition();
        }
    }
}
