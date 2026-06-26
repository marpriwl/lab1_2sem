package service.history;

public interface Operation {
    void undo();
    void redo();
    String getDescription();
}
