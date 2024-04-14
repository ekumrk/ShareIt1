package ru.yandex.practicum.ShareIt.exception;

public class EntityValidationException extends RuntimeException {

    public EntityValidationException(String message) {
        super(message);
    }
}
