package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.InputItemDto;
import ru.practicum.shareit.item.model.ItemDtoItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.InputItemRequestDto;
import ru.practicum.shareit.request.dto.OutputItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.RequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RequestMapper mapper;
    private final ItemDtoItemMapper itemMapper;

    @Override
    @Transactional
    public InputItemRequestDto post(Long userId, InputItemRequestDto dto) {
        User requestor = getUser(userId);
        ItemRequest request = mapper.inputItemRequestDtoToItemRequest(dto);
        request.setRequestor(requestor);
        return mapper.itemRequestToInputItemRequestDto(requestRepository.saveAndFlush(request));
    }

    @Override
    public List<OutputItemRequestDto> getOwnRequests(Long userId) {
        getUser(userId);
        return mapper.itemRequestToOutputItemRequestDtos(requestRepository.findAllByRequestorIdOrderByRequestorIdDesc(userId))
                .stream()
                .map(this::setRequestItems)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<OutputItemRequestDto> getUsersRequests(Long userId, Integer from, Integer size) {
        getUser(userId);
        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "created"));
        List<ItemRequest> requests = requestRepository.findAllByRequestorIdNot(userId, page);
        return mapper.itemRequestToOutputItemRequestDtos(requests).stream()
                .map(this::setRequestItems)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public OutputItemRequestDto getRequest(Long userId, Long requestId) {
        getUser(userId);
        return setRequestItems(
                mapper.itemRequestToOutputItemRequestDto(getRequest(requestId))
        );
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь не найден!")
        );
    }

    private ItemRequest getRequest(Long requestId) {
        return requestRepository.findById(requestId).orElseThrow(
                () -> new EntityNotFoundException("Запрос не найден!")
        );
    }

    private OutputItemRequestDto setRequestItems(OutputItemRequestDto request) {
        List<InputItemDto> list = itemRepository.findAllByRequestId(request.getId())
                .stream()
                .map(itemMapper::mapItemToInputItemDto)
                .collect(Collectors.toUnmodifiableList());
        request.setItems(list);
        return request;
    }
}
