package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserDtoAdd;

import java.util.List;

public interface UserService {
    UserDto add(UserDtoAdd userDtoAdd);

    List<UserDto> getAll(List<Long> ids, int from, int size);

    void delete(Long id);
}
