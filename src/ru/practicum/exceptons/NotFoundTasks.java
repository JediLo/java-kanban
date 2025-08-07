package ru.practicum.exceptons;

public class NotFoundTasks extends RuntimeException {
    public NotFoundTasks(String message) {
        super(message);
    }
}
