package ru.yandex.practicum.ShareIt.booking.assistive;

import ru.yandex.practicum.ShareIt.exception.EntityValidationException;

public enum Status {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED;

    public static void checkStatus(String status) {
        int isPresent = 0;
        for (Status value : Status.values()) {
            if (value.name().equals(status)) {
                isPresent++;
            }
        }
        if (isPresent != 1) {
            throw new EntityValidationException("Unknown status: " + status);
        }
    }
}
