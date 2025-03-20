package ru.practicum.manager;

import ru.practicum.model.EpicTask;
import ru.practicum.model.SubTask;
import ru.practicum.model.Task;
import ru.practicum.model.TaskProgress;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private int countTasks = 0;
    private final Map<Integer, Task> hashMapTask = new HashMap<>();
    private final Map<Integer, EpicTask> hashMapEpic = new HashMap<>();
    private final Map<Integer, SubTask> hashMapSub = new HashMap<>();


    public Task getTask(int id) {
        if(hashMapTask.get(id).getTaskID() == id) {
            return hashMapTask.get(id);
        }
        return null;
    }

    public EpicTask getEpicTask(int id) {
        if(hashMapEpic.get(id).getTaskID() == id) {
            return hashMapEpic.get(id);
        }
        return null;
    }

    public SubTask getSubTask(int id) {
        if(hashMapSub.get(id).getTaskID() == id) {
            return hashMapSub.get(id);
        }
        return null;
    }

    public int addNewTask(Task task) {

            countTasks++;
            task.setTaskID(countTasks);
            task.setTaskProgress(TaskProgress.NEW);
            hashMapTask.put(countTasks, task);
            return countTasks;
         // Исправить
    }

    public int addNewEpicTask(EpicTask task) {
        countTasks++;
        task.setTaskID(countTasks);
        task.setTaskProgress(TaskProgress.NEW);
        hashMapEpic.put(countTasks, task);
        return countTasks;
    }


    public int addNewSubTask(SubTask task) {

            countTasks++;
            task.setTaskID(countTasks);
            task.setTaskProgress(TaskProgress.NEW);
            hashMapSub.put(countTasks, task);

        return countTasks;
    }
    public void updateTask(Task newTask){

                hashMapTask.put(newTask.getTaskID(), newTask);

    }
    public void updateEpic(EpicTask newEpicTask){

            hashMapEpic.put(newEpicTask.getTaskID(), newEpicTask);


    }
    public void updateSubtask(SubTask newSubTask){

            hashMapSub.put(newSubTask.getTaskID(), newSubTask);


    }
    public void deleteTask(int id){
        hashMapTask.remove(id);
    }
    public void deleteEpicTask(int id){
        hashMapEpic.remove(id);
    }
    public void deleteSubtask(int id){
        hashMapSub.remove(id);
    }
    public void deleteTasks(){
        hashMapTask.clear();
        if(hashMapSub.isEmpty() && hashMapEpic.isEmpty()){
            countTasks = 0;
        }
    }
    public void deleteEpicTasks(){
        hashMapEpic.clear();
        if(hashMapSub.isEmpty() && hashMapTask.isEmpty()){
            countTasks = 0;
        }
    }
    public void deleteSubTasks(){
        hashMapSub.clear();
        if(hashMapEpic.isEmpty() && hashMapTask.isEmpty()){
            countTasks = 0;
        }
    }
    public List<Task> getAllTask(){
        return hashMapTask.values().stream().toList();
    }
    public List<EpicTask> getAllEpic(){
        return hashMapEpic.values().stream().toList();
    }
    public List<SubTask> getAllSubTask(){
        return hashMapSub.values().stream().toList();
    }
    public void checkAndReplaceTaskProgress(EpicTask epicTask) {
        List<SubTask> allSubTasks = this.getAllSubTask();
        boolean isInProgress = false;
        boolean isDone = true;
        TaskProgress taskProgressToSubTask;
        for (SubTask allSubTask : allSubTasks) {
            if (allSubTask.getEpicTaskID() == epicTask.getTaskID()) {
                taskProgressToSubTask = allSubTask.getTaskProgress();
                if (taskProgressToSubTask != TaskProgress.NEW) {
                    isInProgress = true;
                }
                if (taskProgressToSubTask != TaskProgress.DONE) {
                    isDone = false;
                }

            }
        }


            if(isInProgress){
                epicTask.setTaskProgress(TaskProgress.IN_PROGRESS);
            } else if(isDone){
                epicTask.setTaskProgress(TaskProgress.DONE);
            } else {
                epicTask.setTaskProgress(TaskProgress.NEW);
            }

        }

}
