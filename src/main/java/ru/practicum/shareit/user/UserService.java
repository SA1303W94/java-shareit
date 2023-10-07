package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public UserDto create(UserDto userDto) {
        if (userRepository.getUserIdByEmail(userDto.getEmail()) != null) {
            throw new AlreadyExistsException("User with id = " + userDto.getEmail() + " already exists.");
        }
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.create(user));
    }

    public UserDto update(UserDto userDto, Long id) {
        if (userRepository.getUserById(id) == null) {
            throw new NotFoundException("User with ID = " + id + " not found.");
        }
        userDto.setId(id);
        if (userDto.getName() == null) {
            userDto.setName(userRepository.getUserById(id).getName());
        }
        if (userDto.getEmail() == null) {
            userDto.setEmail(userRepository.getUserById(id).getEmail());
        }
        User user = UserMapper.toUser(userDto);
        final Long idFromDbByEmail = userRepository.getUserIdByEmail(user.getEmail());
        if (idFromDbByEmail != null && !user.getId().equals(idFromDbByEmail)) {
            throw new AlreadyExistsException("User with e-mail=" + user.getEmail() + " already exists.");
        }
        User updateUser = userRepository.update(user);
        return UserMapper.toUserDto(updateUser);
    }

    public UserDto delete(Long userId) {
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
        if (userRepository.getUserById(id) == null) {
            throw new NotFoundException("User with ID = " + id + " not found.");
        }
        return UserMapper.toUserDto(userRepository.getUserById(id));
    }
}