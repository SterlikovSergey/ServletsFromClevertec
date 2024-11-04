package ru.clevertec.service;

import ru.clevertec.model.Contact;
import ru.clevertec.model.User;

import java.util.List;

public interface IUserService {
    User createUser(User user);

    User updateUser(Long id, User user);

    boolean deleteUser(Long id);

    User getUserById(Long id);

    List<User> getAllUsers();

    Contact addContact(Long userId, Contact contact);
}
