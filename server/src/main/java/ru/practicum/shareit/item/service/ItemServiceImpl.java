package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.EntityValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InputItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentDtoCommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDtoItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final BookingServiceImpl bookingService;
    private final CommentDtoCommentMapper commentMapper;
    private final ItemDtoItemMapper mapper;

    @Transactional
    @Override
    public InputItemDto addNewItem(Long userId, InputItemDto dto) {
        Item item = mapper.mapInputItemDtoToItem(dto);
        User owner = getUser(userId);
        if (dto.getRequestId() != null) {
            ItemRequest request = getItemRequest(dto.getRequestId());
            item.setRequest(request);
        }
        item.setOwner(owner);
        return mapper.mapItemToInputItemDto(itemRepository.saveAndFlush(item));
    }

    @Transactional
    @Override
    public InputItemDto updateItem(Long userId, Long itemId, InputItemDto dto) {
        getUser(userId);
        Item item = getItem(itemId);

        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new EntityNotFoundException("Только владелец вещи может обновлять данные!");
        }

        if (dto.getDescription() != null) {
            item.setDescription(dto.getDescription());
        }

        if (dto.getName() != null) {
            item.setName(dto.getName());
        }

        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }

        return mapper.mapItemToInputItemDto(itemRepository.saveAndFlush(item));
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        getUser(userId);
        Item item = getItem(itemId);

        if (Objects.equals(item.getOwner().getId(), userId)) {
            return setBookings(mapper.mapItemToItemDto(item));
        } else {
            return setComments(mapper.mapItemToItemDto(item));
        }
    }

    @Override
    public List<ItemDto> getItems(Long userId, Integer from, Integer size) {
        getUser(userId);
        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        return itemRepository.findAllByOwnerId(userId, page).stream()
                .map(mapper::mapItemToItemDto)
                .map(this::setBookings)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<InputItemDto> findByText(Long userId, String text, Integer from, Integer size) {
        getUser(userId);
        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        return itemRepository.search(text.toLowerCase(), page)
                .stream()
                .filter(Item::getAvailable)
                .map(mapper::mapItemToInputItemDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Transactional
    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto dto) {
        Item item = getItem(itemId);
        User author = getUser(userId);

        if (bookingService.getBookingByBooker(userId, itemId) == null ||
                bookingService.getBookingByBooker(userId, itemId).size() == 0) {
            throw new EntityValidationException("Пользователь еще не арендовал данную вещь!");
        }

        Comment comment = commentMapper.mapCommentDtoToComment(dto);
        comment.setAuthor(author);
        comment.setItem(item);
        return commentMapper.mapCommentToCommentDto(commentRepository.saveAndFlush(comment));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь не найден!")
        );
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new EntityNotFoundException("Вещь не найдена!")
        );
    }

    private ItemRequest getItemRequest(Long requestId) {
        return requestRepository.findById(requestId).orElseThrow(
                () -> new EntityNotFoundException("Запрос не найден!")
        );
    }

    private ItemDto setBookings(ItemDto dto) {
        BookingInfoDto last = bookingService.getLastBooking(dto.getId());
        BookingInfoDto next = bookingService.getNextBooking(dto.getId());
        setComments(dto);

        if (last != null) {
            dto.setLastBooking(last);
        }

        if (next != null) {
            dto.setNextBooking(next);
        }

        return dto;
    }

    private ItemDto setComments(ItemDto dto) {
        List<CommentDto> list = commentRepository.findAllByItemId(dto.getId())
                .stream()
                .map(commentMapper::mapCommentToCommentDto)
                .collect(Collectors.toUnmodifiableList());
        dto.setComments(list);
        return dto;
    }
}
