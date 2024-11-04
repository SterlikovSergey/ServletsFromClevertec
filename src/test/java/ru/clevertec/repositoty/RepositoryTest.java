package ru.clevertec.repositoty;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.model.Contact;
import ru.clevertec.model.User;
import ru.clevertec.model.enums.ContactType;
import ru.clevertec.repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RepositoryTest {

    @Mock
    private Repository repository;

    @BeforeEach
    public void setUp() {
        repository = new Repository();
    }

    @Test
    @DisplayName("Test saving a new user")
    public void testSaveUser() {
        User user = User.builder().username("testUser").build();

        User savedUser = repository.save(user);

        assertNotNull(savedUser.getId());
        assertEquals("testUser", savedUser.getUsername());
        assertEquals(1, repository.findAll().size());
    }

    @Test
    @DisplayName("Test finding all users")
    public void testFindAllUsers() {
        User user1 = User.builder().username("user1").build();
        User user2 = User.builder().username("user2").build();

        repository.save(user1);
        repository.save(user2);

        List<User> users = repository.findAll();
        assertEquals(2, users.size());
    }

    @ParameterizedTest
    @MethodSource("provideIdsForGetUserById")
    @DisplayName("Test finding user by id")
    public void testGetUserById(Long id, String expectedUsername) {
        User user = User.builder().username(expectedUsername).build();
        user = repository.save(user);

        User foundUser = repository.getUserById(user.getId());
        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());
        assertEquals(expectedUsername, foundUser.getUsername());
    }

    private static Stream<Arguments> provideIdsForGetUserById() {
        return Stream.of(
                Arguments.of(1L, "user1"),
                Arguments.of(2L, "user2")
        );
    }

    @ParameterizedTest
    @MethodSource("provideIdsForDeleteUser")
    @DisplayName("Test deleting user by id")
    public void testDeleteUser(Long id, String username, boolean expectedResult) {
        User user = User.builder().username(username).build();
        user = repository.save(user);

        boolean isDeleted = repository.delete(user.getId());
        assertEquals(expectedResult, isDeleted);
        assertNull(repository.getUserById(user.getId()));
    }

    private static Stream<Arguments> provideIdsForDeleteUser() {
        return Stream.of(
                Arguments.of(1L, "deleteUser1", true),
                Arguments.of(2L, "deleteUser2", true)
        );
    }

    @ParameterizedTest
    @MethodSource("provideUsersForUpdate")
    @DisplayName("Test updating user information")
    public void testUpdateUser(User initialUser, User updatedUser, String expectedUsername, String expectedPassword) {
        initialUser = repository.save(initialUser);

        User result = repository.update(initialUser.getId(), updatedUser);

        assertNotNull(result);
        assertEquals(expectedUsername, result.getUsername());
        assertEquals(expectedPassword, result.getPassword());
    }

    private static Stream<Arguments> provideUsersForUpdate() {
        return Stream.of(
                Arguments.of(
                        User.builder().username("oldUsername1").password("oldPassword1").build(),
                        User.builder().username("newUsername1").password("newPassword1").build(),
                        "newUsername1",
                        "newPassword1"
                ),
                Arguments.of(
                        User.builder().username("oldUsername2").password("oldPassword2").build(),
                        User.builder().username("newUsername2").password("newPassword2").build(),
                        "newUsername2",
                        "newPassword2"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideContactsForAddContact")
    @DisplayName("Test adding contact to user")
    public void testAddContact(User user, Contact contact, String expectedContactType, String expectedContactValue) {
        User addUser = repository.save(user);

        Contact addedContact = repository.addContact(contact, addUser.getId());

        assertNotNull(addedContact.getId());
        assertEquals(expectedContactType, addedContact.getType().name());
        assertEquals(expectedContactValue, addedContact.getValue());

        assertTrue(
                repository.getUserById(addUser.getId()).getContacts().stream()
                        .anyMatch(c -> c.getType() == contact.getType() && c.getValue().equals(contact.getValue()))
        );
    }

    private static Stream<Arguments> provideContactsForAddContact() {
        return Stream.of(
                Arguments.of(
                        User.builder().username("userWithContact1").contacts(new ArrayList<>()).build(),
                        Contact.builder().type(ContactType.EMAIL).value("test1@example.com").build(),
                        "email".toUpperCase(),
                        "test1@example.com"
                ),
                Arguments.of(
                        User.builder().username("userWithContact2").contacts(new ArrayList<>()).build(),
                        Contact.builder().type(ContactType.PHONE).value("+1234567890").build(),
                        "phone".toUpperCase(),
                        "+1234567890"
                ),
                Arguments.of(
                        User.builder().username("userWithContact3").contacts(new ArrayList<>()).build(),
                        Contact.builder().type(ContactType.SKYPE).value("live:username").build(),
                        "skype".toUpperCase(),
                        "live:username"
                )
        );
    }
}
