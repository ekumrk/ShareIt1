package ru.yandex.practicum.ShareIt.item.comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentDtoCommentMapper {
    @Mapping(target = "authorName", source = "author.name")
    CommentDto mapCommentToCommentDto(Comment comment);

    Comment mapCommentDtoToComment(CommentDto dto);
}
