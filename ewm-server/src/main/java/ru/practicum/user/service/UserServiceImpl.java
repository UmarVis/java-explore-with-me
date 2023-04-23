package ru.practicum.user.service;

import lombok.AllArgsConstructor;
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
public class UserServiceImpl implements UserService {
    public static final Sort SORT_BY_ASC = Sort.by(Sort.Direction.ASC, "id");
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto add(UserDtoAdd userDtoAdd) {
        User user = UserMapper.makeUser(userDtoAdd);
        return UserMapper.makeUserDto(userRepository.save(user));
    }

    @Override
    public List<UserDto> getAll(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from/size, size, SORT_BY_ASC);
        return UserMapper.makeListUserDtos(userRepository.findAllByIdIn(ids, pageable));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.findById(id).orElseThrow(()
                -> new UserNotFoundException("Пользователь с ИД " + id + " не найден"));
        userRepository.deleteById(id);
    }
}
