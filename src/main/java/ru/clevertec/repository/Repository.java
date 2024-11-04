package ru.clevertec.repository;

import ru.clevertec.model.Contact;
import ru.clevertec.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Repository {
    private List<User> users = new ArrayList<>();
    private Long nextUserId = 1L;
    private Long nextContactId = 1L;

    public User save(User user) {
        user.setId(nextUserId++);
        users.add(user);
        return user;
    }

    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    public User getUserById(Long id) {
        return users.stream().filter(user -> user.getId().equals(id)).findFirst().orElse(null);
    }

    public boolean delete(Long id) {
        return users.removeIf(user -> user.getId().equals(id));
    }

    public User update(Long id, User user) {
        Optional<User> existingUser = users.stream().filter(u -> u.getId().equals(id)).findFirst();
        if (existingUser.isPresent()) {
            User u = existingUser.get();
            u.setUsername(user.getUsername());
            u.setPassword(user.getPassword());
            if (user.getContacts() != null) {
                u.setContacts(new ArrayList<>(user.getContacts()));
            }
            return u;
        }
        return null;
    }

    public Contact addContact(Contact contact, Long userId) {
        User user = getUserById(userId);
        if (user != null) {
            if (user.getContacts() == null) {
                user.setContacts(new ArrayList<>());
            }
            if (!user.getContacts().contains(contact)) {
                contact.setId(nextContactId++);
                user.getContacts().add(contact);
            }
            return contact;
        }
        return null;
    }
}
