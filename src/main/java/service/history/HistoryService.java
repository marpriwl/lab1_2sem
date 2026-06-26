package service.history;

import java.util.Stack;

public class HistoryService {
    private final Stack<Operation> undoStack = new Stack<>();
    private final Stack<Operation> redoStack = new Stack<>();

    public void addOperation(Operation operation) {
        undoStack.push(operation);
        redoStack.clear();
    }

    public String undo() {
        if (undoStack.isEmpty()) {
            throw new IllegalStateException("Nothing to undo");
        }
        Operation operation = undoStack.pop();
        operation.undo();
        redoStack.push(operation);
        return "Undone: " + operation.getDescription();
    }

    public String redo() {
        if (redoStack.isEmpty()) {
            throw new IllegalStateException("Nothing to redo");
        }
        Operation operation = redoStack.pop();
        operation.redo();
        undoStack.push(operation);
        return "Redone: " + operation.getDescription();
    }
}
