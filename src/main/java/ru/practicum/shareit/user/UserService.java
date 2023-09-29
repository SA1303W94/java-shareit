package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new NotFoundException("User email can not be empty.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            throw new NotFoundException("User name can not be empty.");
        }
        Long idFromDbByEmail = userRepository.getUserIdByEmail(user.getEmail());
        if (idFromDbByEmail != null) {
            throw new AlreadyExistsException("User with e-mail = " + user.getEmail() + " already exists.");
        }
        return UserMapper.toUserDto(userRepository.create(user));
    }

    public UserDto update(UserDto userDto, Long id) {
        userDto.setId(id);
        if (userDto.getName() == null) {
            userDto.setName(userRepository.getUserById(id).getName());
        }
        if (userDto.getEmail() == null) {
            userDto.setEmail(userRepository.getUserById(id).getEmail());
        }
        User user = UserMapper.toUser(userDto);
        if (userRepository.getUserById(user.getId()) == null) {
            throw new NotFoundException("User with ID = " + user.getId() + " not found.");
        }
        if (user.getId() == null) {
            throw new ValidationException("User ID can not be empty.");
        }
        final Long idFromDbByEmail = userRepository.getUserIdByEmail(user.getEmail());
        if (idFromDbByEmail != null && !user.getId().equals(idFromDbByEmail)) {
            throw new AlreadyExistsException("User with e-mail=" + user.getEmail() + " already exists.");
        }
        User updateUser = userRepository.update(user);
        return UserMapper.toUserDto(updateUser);
    }

    public UserDto delete(Long userId) {
        if (userId == null) {
            throw new ValidationException("User ID can not be empty.");
        }
        if (!userRepository.isExistUserInDb(userId)) {
            throw new NotFoundException("User with ID = " + userId + " not found.");
        }
        itemRepository.deleteItemsByOwner(userId);
        return UserMapper.toUserDto(userRepository.delete(userId));
    }

    public List<UserDto> getUsers() {
        return userRepository.getUsers()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(toList());
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.getUserById(id);
        if (user == null) {
            throw new NotFoundException("User with ID = " + id + " not found.");
        }
        return UserMapper.toUserDto(userRepository.getUserById(id));
    }
}