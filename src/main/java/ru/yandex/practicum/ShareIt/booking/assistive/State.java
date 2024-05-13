package ru.yandex.practicum.ShareIt.booking.assistive;

import ru.yandex.practicum.ShareIt.exception.EntityValidationException;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

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
