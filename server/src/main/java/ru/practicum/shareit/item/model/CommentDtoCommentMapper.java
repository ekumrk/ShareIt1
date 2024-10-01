package ru.practicum.shareit.item.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentDto;

@Mapper(componentModel = "spring")
public interface CommentDtoCommentMapper {
    @Mapping(target = "authorName", source = "author.name")
    CommentDto mapCommentToCommentDto(Comment comment);

    Comment mapCommentDtoToComment(CommentDto dto);
}
