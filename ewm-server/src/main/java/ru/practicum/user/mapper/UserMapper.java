package ru.practicum.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserDtoAdd;
import ru.practicum.user.dto.UserDtoShort;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class UserMapper {
    public static User makeUser(UserDtoAdd userDtoAdd) {
        return User.builder()
                .email(userDtoAdd.getEmail())
                .name(userDtoAdd.getName())
                .build();
    }

    public static UserDto makeUserDto(User user) {
        return UserDto.builder()
                .email(user.getEmail())
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public static UserDtoShort makeUserShotDto(User user) {
        return UserDtoShort.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public static List<UserDto> makeListUserDtos(List<User> users) {
        return users.stream().map(UserMapper::makeUserDto).collect(Collectors.toList());
    }
}
