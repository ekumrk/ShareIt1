package ru.yandex.practicum.ShareIt.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Override
    public ItemDto addNewItem(Long userId, ItemDto dto) {
        return ItemMapper.toItemDto(itemRepository.addNewItem(userId, dto));
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto dto) {
        return ItemMapper.toItemDto(itemRepository.updateItem(userId, itemId, dto));
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        return ItemMapper.toItemDto(itemRepository.getItem(userId, itemId));
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        return itemRepository.getItems(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<ItemDto> findByText(Long userId, String text) {
        return itemRepository.findByText(userId, text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toUnmodifiableList());
    }
}
