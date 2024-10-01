package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long userId, Pageable page);

    @Query(" select i from Item i" +
            " where (lower(i.name) like lower(concat('%', ?1, '%')) " +
            " or lower(i.description) like lower(concat('%', ?1, '%')))")
    List<Item> search(@Param("search") String text, Pageable page);

    List<Item> findAllByRequestId(Long requestId);
}
