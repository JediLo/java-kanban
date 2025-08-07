package ru.practicum.exceptons;

public class IncorrectTaskUpdate extends RuntimeException{
    public IncorrectTaskUpdate(String message) {
        super(message);
    }
}
