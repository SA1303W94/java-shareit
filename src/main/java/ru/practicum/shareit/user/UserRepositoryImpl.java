package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users;
    private final Set<String> emails; // добавлю
    private Long currentId;

    public UserRepositoryImpl() {
        currentId = 0L;
        users = new HashMap<>();
        emails = new HashSet<>();
    }

    @Override
    public User create(User user) {
        user.setId(++currentId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User delete(Long userId) {
        return users.remove(userId);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Long userId) {
        return users.get(userId);
    }

    @Override
    public boolean isExistUserInDb(Long id) {
        return users.containsKey(id);
    }

    @Override
    public Long getUserIdByEmail(String inputEmail) {
        if (inputEmail == null) {
            return null;
        }
        for (User user : users.values()) {
            String email = user.getEmail();
            if (email.equals(inputEmail)) {
                return user.getId();
            }
        }
        return null;
    }
}