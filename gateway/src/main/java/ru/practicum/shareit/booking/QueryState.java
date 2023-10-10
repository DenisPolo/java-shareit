package ru.practicum.shareit.booking;

import java.util.Optional;

public enum QueryState {
    ALL,
    WAITING,
    REJECTED,
    FUTURE,
    CURRENT,
    PAST;

    public static Optional<QueryState> from(String stringState) {
        for (QueryState state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}