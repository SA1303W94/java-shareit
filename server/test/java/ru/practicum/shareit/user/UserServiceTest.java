package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;


    @Test
    void createUserTest() {
        UserDto userDto = new UserDto();
        User user = UserMapper.toUser(userDto);
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserDto createdUserDto = userService.create(userDto);
        assertNotNull(createdUserDto);
        assertEquals(userDto.getName(), createdUserDto.getName());
        assertEquals(userDto.getEmail(), createdUserDto.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void saveUserTest() {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserDto updatedUserDto = userService.save(userDto, userId);
        assertNotNull(updatedUserDto);
        assertEquals(userDto.getName(), updatedUserDto.getName());
        assertEquals(userDto.getEmail(), updatedUserDto.getEmail());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void findUserByIdTest() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        UserDto userDto = userService.findUserById(userId);
        assertNotNull(userDto);
        assertEquals(user.getId(), userDto.getId());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void findUserByIdNotFoundTest() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.findUserById(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void findAllUsersTest() {
        List<User> userList = new ArrayList<>();
        userList.add(new User());
        userList.add(new User());
        when(userRepository.findAll()).thenReturn(userList);
        List<UserDto> userDtoList = userService.findAllUsers();
        assertNotNull(userDtoList);
        assertEquals(userList.size(), userDtoList.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void deleteUserTest() {
        Long userId = 1L;
        userService.delete(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }
}