package ru.yandex.practicum.ShareIt.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ShareIt.booking.BookingInfoDto;
import ru.yandex.practicum.ShareIt.booking.BookingServiceImpl;
import ru.yandex.practicum.ShareIt.exception.EntityNotFoundException;
import ru.yandex.practicum.ShareIt.exception.EntityValidationException;
import ru.yandex.practicum.ShareIt.item.comment.Comment;
import ru.yandex.practicum.ShareIt.item.comment.CommentDto;
import ru.yandex.practicum.ShareIt.item.comment.CommentDtoCommentMapper;
import ru.yandex.practicum.ShareIt.item.comment.CommentRepository;
import ru.yandex.practicum.ShareIt.user.User;
import ru.yandex.practicum.ShareIt.user.UserRepository;


import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingServiceImpl bookingService;
    private final CommentDtoCommentMapper commentMapper;
    private final ItemDtoItemMapper mapper;

    @Override
    public InputItemDto addNewItem(Long userId, InputItemDto dto) {
        if (dto.getId() == null) {
            Item item = mapper.mapInputItemDtoToItem(dto);
            item.setOwner(getUser(userId));
            return mapper.mapItemToInputItemDto(itemRepository.saveAndFlush(item));
        }

        throw new EntityValidationException("Проверьте корректность данных новой вещи!");
    }

    @Override
    public InputItemDto updateItem(Long userId, Long itemId, InputItemDto dto) {
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
            item.setIsAvailable(dto.getAvailable());
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
    public List<ItemDto> getItems(Long userId) {
        getUser(userId);
        return itemRepository.findAllByOwnerId(userId).stream()
                .map(mapper::mapItemToItemDto)
                .map(this::setBookings)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<InputItemDto> findByText(Long userId, String text) {
        getUser(userId);

        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        }

        return itemRepository.search(text.toLowerCase())
                .stream()
                .filter(Item::getIsAvailable)
                .map(mapper::mapItemToInputItemDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto dto) {
        Item item = getItem(itemId);
        User author = getUser(userId);

        if (bookingService.getBookingByBooker(userId, itemId) == null ||
                bookingService.getBookingByBooker(userId, itemId).size() == 0) {
            throw new EntityValidationException("Пользователь еще не арендовал данную вещь!");
        }

        Comment comment = commentMapper.mapCommentDtoToComment(dto);
        comment.setCreated(LocalDateTime.now());
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