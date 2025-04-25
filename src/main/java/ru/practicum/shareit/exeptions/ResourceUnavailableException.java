package ru.practicum.shareit.exeptions;

public class ResourceUnavailableException extends RuntimeException {
    public ResourceUnavailableException(String message) {
        super(message);
    }
}
