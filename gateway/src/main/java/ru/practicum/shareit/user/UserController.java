package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.groups.Create;
import ru.practicum.shareit.groups.Update;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Validated({Create.class}) @RequestBody UserDto userDto) {
        log.info("Received a POST-request to the endpoint: '/users' to add user.");
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> save(@Validated({Update.class}) @RequestBody UserDto userDto, @PathVariable long userId) {
        log.info("Received a PATCH-request to the endpoint: '/users' to update user with ID = {}", userId);
        return userClient.updateUser(userDto, userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findById(@PathVariable long userId) {
        log.info("Received a GET-request to the endpoint: '/users' to get user with ID = {}", userId);
        return userClient.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("Received a GET-request to the endpoint: '/users' to get all users");
        return userClient.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("Received a DELETE-request to the endpoint: '/users' to delete user with ID = {}", userId);
        userClient.deleteUser(userId);
    }
}