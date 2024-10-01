package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.InputItemRequestDto;
import ru.practicum.shareit.request.dto.OutputItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    InputItemRequestDto post(Long userId, InputItemRequestDto dto);

    List<OutputItemRequestDto> getOwnRequests(Long userId);

    List<OutputItemRequestDto> getUsersRequests(Long userId, Integer from, Integer size);

    OutputItemRequestDto getRequest(Long userId, Long requestId);
}
