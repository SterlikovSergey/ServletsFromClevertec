package ru.clevertec.service;

import ru.clevertec.model.User;

public interface IUserService {
    User createUser(User user);
    User updateUser(Long id, User user);
    void deleteUser(Long id);
    User getUserById(Long id);
}
