package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "registrationDate", target = "registrationDate", dateFormat = "yyyy.MM.dd hh:mm:ss")
    UserDto mapToUserDto(User user);

    List<UserDto> mapToUserDto(Iterable<User> users);

    User mapToNewUser(UserCreationDto userDto);
}