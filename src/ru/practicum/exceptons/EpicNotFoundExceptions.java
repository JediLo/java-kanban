package ru.practicum.exceptons;

public class EpicNotFoundExceptions extends RuntimeException {
    public EpicNotFoundExceptions(String message) {
        super(message);
    }
}
