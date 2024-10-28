package ru.clevertec.service.impl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.clevertec.model.User;
import ru.clevertec.service.IUserService;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserService implements IUserService {

    private List<User> users = new ArrayList<>();

    @Override
    public User createUser(User user) {
        return null;
    }

    @Override
    public User updateUser(Long id, User user) {
        return null;
    }

    @Override
    public void deleteUser(Long id) {

    }

    @Override
    public User getUserById(Long id) {
        return null;
    }
}
