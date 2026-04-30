package service;

public class IdGenerator {
    private long currentId = 1;

    public long nextId() {
        return currentId++;
    }

    public void setNextId(long nextId) {
        if (nextId < 1) {
            throw new IllegalArgumentException("nextId должен быть положительным");
        }
        this.currentId = nextId;
    }
}
