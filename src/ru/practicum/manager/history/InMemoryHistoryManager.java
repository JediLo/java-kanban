package ru.practicum.manager.history;

import ru.practicum.model.Epic;
import ru.practicum.model.Node;
import ru.practicum.model.SubTask;
import ru.practicum.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private final TasksLinkedList<Task> historyViews = new TasksLinkedList<>();

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        Task taskToHistory;
        if (task instanceof SubTask subTask) {
            taskToHistory = new SubTask(subTask);
        } else if (task instanceof Epic epic) {
            taskToHistory = new Epic(epic);
        } else {
            taskToHistory = new Task(task);
        }
        historyViews.add(taskToHistory.getTaskID(),taskToHistory);
    }


    @Override
    public ArrayList<Task> getHistory() {
        return historyViews.getTasks();
    }

    @Override
    public void remove(int id) {
        historyViews.remove(id);
    }

    private static class TasksLinkedList<T> {
        int size = 0;
        Node<T> first;
        Node<T> last;
        HashMap<Integer, Node<T>> nodeMap = new HashMap<>();

        void add(int id, T task) {
            Node<T> oldNode = nodeMap.get(id);
            if (oldNode != null) { // Если задача есть в списке
                removeNode(id, oldNode);
            }
            Node<T> newNode = new Node<>(last, task, null); // Заводим новую Задачу
            if (last != null) { // Проверяем не пустой ли наш лист
                last.next = newNode; // Добавляем связь предыдущей и новой
            } else {
                first = newNode; // Если пустой ставим в начало новую задачу
            }
            last = newNode; // Делаем новую задачу последней
            nodeMap.put(id, newNode); // Добавляем в наш Мар новую задачу
            size++; // Увеличиваем размер списка
        }

        public void remove(int id) {
            Node<T> oldNode = nodeMap.get(id);
            if (oldNode != null) {
                removeNode(id, oldNode);
            }
        }

        public ArrayList<T> getTasks() {
            ArrayList<T> listTasks = new ArrayList<>();
            Node<T> tNode = first;
            while (tNode != null) {
                listTasks.add(tNode.task);
                tNode = tNode.next;
            }
            return listTasks;
        }

        private void removeNode(int id, Node<T> oldNode) {
            if (oldNode == null){
                return;
            }
            if (oldNode.prev != null) { // если у задачи есть связь с предыдущей
                oldNode.prev.next = oldNode.next; // Привязываем предыдущую к следующей
            } else {
                first = oldNode.next; // если задача была первой, делаем первой следующую задачу
            }
            if (oldNode.next != null) { // если у задачи есть следующая задача
                oldNode.next.prev = oldNode.prev;
            }  // Привязываем к следующей задаче предыдущую
            else {
                last = oldNode.prev; // делаем последнюю задачу предыдущей
            }
            nodeMap.remove(id); // Удаляем и нашего Мар старую задачу
            size--; // Уменьшаем размер списка
        }
    }





}
