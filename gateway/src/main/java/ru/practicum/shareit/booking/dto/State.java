package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.exception.EntityValidationException;

public enum State {
    ALL,
    CURRENT,
    FUTURE,
    PAST,
    REJECTED,
    WAITING;

    public static void checkState(String state) {
        int isPresent = 0;
        for (State value : State.values()) {
            if (value.name().equals(state)) {
                isPresent++;
            }
        }
        if (isPresent != 1) {
            throw new EntityValidationException("Unknown state: " + state);
        }
    }
}
