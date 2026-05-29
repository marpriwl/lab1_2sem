package service;

import domain.User;
import security.PasswordHasher;
import storage.DbStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserService {

    private final List<User> users;
    private final DbStorage dbStorage;
    private final PasswordHasher passwordHasher;
    private User currentUser;
    private long nextId;

    public UserService() {
        this(new ArrayList<>(), null);
    }

    public UserService(List<User> loadedUsers) {
        this(loadedUsers, null);
    }

    public UserService(List<User> loadedUsers, DbStorage dbStorage) {
        this.users = new ArrayList<>(loadedUsers);
        this.dbStorage = dbStorage;
        this.passwordHasher = new PasswordHasher();
        this.currentUser = null;
        this.nextId = calculateNextId();
    }

    private long calculateNextId() {
        long maxId = 0;

        for (User user : users) {
            if (user.getId() > maxId) {
                maxId = user.getId();
            }
        }

        return maxId + 1;
    }

    private Optional<User> findByLogin(String login) {
        for (User user : users) {
            if (user.getLogin().equals(login)) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }

    public User register(String login, String password) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("Ошибка: логин не может быть пустым");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Ошибка: пароль не может быть пустым");
        }

        if (findByLogin(login).isPresent()) {
            throw new IllegalArgumentException("Ошибка: логин уже занят");
        }

        String passwordHash = passwordHasher.hash(password);

        long id = dbStorage == null ? nextId : 0;
        User user = new User(id, login, passwordHash);

        if (dbStorage != null) {
            user = dbStorage.insertUser(user);
        }

        users.add(user);

        if (dbStorage == null) {
            nextId++;
        } else {
            nextId = calculateNextId();
        }

        return user;
    }


    public User login(String login, String password) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("Ошибка: логин не может быть пустым");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Ошибка: пароль не может быть пустым");
        }

        Optional<User> userOptional = findByLogin(login);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Ошибка: неверный логин или пароль");
        }

        User user = userOptional.get();

        if (!passwordHasher.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Ошибка: неверный логин или пароль");
        }

        this.currentUser = user;

        return user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public User requireLogin() {
        if (currentUser == null) {
            throw new IllegalStateException("Ошибка: сначала выполните login");
        }

        return currentUser;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }


}
