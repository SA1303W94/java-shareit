package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("Received a POST-request to the endpoint: '/users' to add user.");
        return userService.create(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto save(@RequestBody UserDto userDto, @PathVariable long userId) {
        log.info("Received a PATCH-request to the endpoint: '/users' to update user with ID = {}", userId);
        return userService.save(userDto, userId);
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable long userId) {
        log.info("Received a GET-request to the endpoint: '/users' to get user with ID = {}", userId);
        return userService.findUserById(userId);
    }

    @GetMapping
    public List<UserDto> findAll() {
        log.info("Received a GET-request to the endpoint: '/users' to get all users");
        return userService.findAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("Received a DELETE-request to the endpoint: '/users' to delete user with ID = {}", userId);
        userService.delete(userId);
    }
}