package domain;

import java.time.Instant;

public final class User {  //класс модель данных пользователя

    private long id;  //поля класса
    private String login;
    private String passwordHash;
    private Instant createdAt;
    private Instant updatedAt;

    public User() { //пустой конструктор для загрузки из JSON
    }

    public User(long id, String login, String passwordHash) {  //конструктор класса
        this.id = id;
        this.login = login;
        this.passwordHash = passwordHash;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public long getId() {  //геттеры и сеттеры
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPasswordHash() {
        return  passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void updateTimestamp() {  //"временная метка" метод, который обновляет поле updatedAt текущим временем
        this.updatedAt = Instant.now();
    }
}


