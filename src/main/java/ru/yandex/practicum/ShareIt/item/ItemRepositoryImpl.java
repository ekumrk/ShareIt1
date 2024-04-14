package ru.yandex.practicum.ShareIt.item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.ShareIt.exception.EntityNotFoundException;
import ru.yandex.practicum.ShareIt.item.ItemDto;
import ru.yandex.practicum.ShareIt.user.User;
import ru.yandex.practicum.ShareIt.user.UserRepositoryImpl;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> itemIdToItem = new HashMap<>();
    private final UserRepositoryImpl userRepository;
    private Long id = 1L;

    @Override
    public Item addNewItem(Long userId, ItemDto dto) {
        if (dto.getId() != null && itemIdToItem.containsKey(dto.getId())) {
            throw new ValidationException("Такая вещь уже существует!");
        }
        User user = userRepository.getUserById(userId);
        dto.setId(generateId());
        itemIdToItem.put(dto.getId(), ItemMapper.toItem(dto, user, null));
        return itemIdToItem.get(dto.getId());
    }

    @Override
    public Item updateItem(Long userId, Long itemId, ItemDto dto) {
        if (!itemIdToItem.containsKey(itemId) || !Objects.equals(itemIdToItem.get(itemId).getOwner().getId(), userId)) {
            throw new EntityNotFoundException("Такая вещь не существует!");
        }
        final Item item = itemIdToItem.get(itemId);
        if (dto.getName() == null) {
            dto.setName(item.getName());
        }
        if (dto.getDescription() == null) {
            dto.setDescription(item.getDescription());
        }
        if (dto.getAvailable() == null) {
            dto.setAvailable(item.getIsAvailable());
        }
        dto.setId(item.getId());
        itemIdToItem.put(itemId, ItemMapper.toItem(dto, userRepository.getUserById(userId), null));
        return itemIdToItem.get(itemId);
    }

    @Override
    public Item getItem(Long userId, Long itemId) {
        return itemIdToItem.values().stream()
                .filter(item -> Objects.equals(item.getId(), itemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Вещь не найдена!"));
    }

    @Override
    public List<Item> getItems(Long userId) {
        return itemIdToItem.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<Item> findByText(Long userId, String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemIdToItem.values().stream()
                .filter(item -> (item.getDescription().toLowerCase().contains(text.toLowerCase()) ||
                        item.getName().toLowerCase().contains(text.toLowerCase())) && item.getIsAvailable())
                .collect(Collectors.toUnmodifiableList());
    }

    private Long generateId() {
        return id++;
    }
}
