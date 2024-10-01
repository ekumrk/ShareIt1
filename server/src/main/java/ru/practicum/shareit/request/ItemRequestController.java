package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.InputItemRequestDto;
import ru.practicum.shareit.request.dto.OutputItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.constants.Constants.USER_ID;
import static ru.practicum.shareit.constants.Constants.FROM;
import static ru.practicum.shareit.constants.Constants.SIZE;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public InputItemRequestDto postRequest(@RequestHeader(USER_ID) Long userId,
                                           @RequestBody InputItemRequestDto dto) {
        log.info("Server. Posting request with requestorId={}.", userId);
        return requestService.post(userId, dto);
    }

    @GetMapping
    public List<OutputItemRequestDto> getOwnRequests(@RequestHeader(USER_ID) Long userId) {
        log.info("Server. Get requests with requestorId={}.", userId);
        return requestService.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public List<OutputItemRequestDto> getOtherUsersRequests(@RequestHeader(USER_ID) Long userId,
                                                            @RequestParam(defaultValue = FROM) Integer from,
                                                            @RequestParam(defaultValue = SIZE) Integer size) {
        log.info("Server. Get requests with requestorId !={}.", userId);
        return requestService.getUsersRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public OutputItemRequestDto getRequest(@RequestHeader(USER_ID) Long userId,
                                           @PathVariable Long requestId) {
        log.info("Server. Get request with requestId={}.", userId);
        return requestService.getRequest(userId, requestId);
    }
}
