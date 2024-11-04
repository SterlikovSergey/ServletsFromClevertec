package ru.clevertec.service.impl;

import lombok.AllArgsConstructor;
import ru.clevertec.model.Contact;
import ru.clevertec.model.User;
import ru.clevertec.repository.Repository;
import ru.clevertec.service.IUserService;

import java.util.List;

@AllArgsConstructor
public class UserService implements IUserService {

    private Repository repository;

    @Override
    public User createUser(User user) {
        return repository.save(user);
    }

    @Override
    public User updateUser(Long id, User user) {
        return repository.update(id, user);
    }

    @Override
    public boolean deleteUser(Long id) {
        return repository.delete(id);
    }

    @Override
    public User getUserById(Long id) {
        return repository.getUserById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Override
    public Contact addContact(Long userId, Contact contact) {
        return repository.addContact(contact, userId);
    }
}
