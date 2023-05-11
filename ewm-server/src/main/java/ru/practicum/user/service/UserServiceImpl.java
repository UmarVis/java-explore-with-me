package ru.practicum.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserDtoAdd;
import ru.practicum.user.exception.UserNotFoundException;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserServiceImpl implements UserService {
    public static final Sort SORT_BY_ASC = Sort.by(Sort.Direction.ASC, "id");
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto add(UserDtoAdd userDtoAdd) {
        log.info("Add new user {}", userDtoAdd);
        User user = UserMapper.makeUser(userDtoAdd);
        return UserMapper.makeUserDto(userRepository.save(user));
    }

    @Override
    public List<UserDto> getAll(List<Long> ids, int from, int size) {
        log.info("Get all users with IDs {}", ids);
        Pageable pageable = PageRequest.of(from / size, size, SORT_BY_ASC);
        return ids == null || ids.isEmpty() ? UserMapper.makeListUserDtos(userRepository.findAll(pageable))
                : UserMapper.makeListUserDtos(userRepository.findAllByIdIn(ids, pageable));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Delete user with ID {}", id);
        userRepository.findById(id).orElseThrow(()
                -> new UserNotFoundException("Пользователь с ИД " + id + " не найден"));
        userRepository.deleteById(id);
    }
}
