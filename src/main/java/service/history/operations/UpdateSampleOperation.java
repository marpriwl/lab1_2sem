package service.history.operations;

import service.SampleService;
import service.history.Operation;

public class UpdateSampleOperation implements Operation {
    private final SampleService service;
    private final long id;
    private final String field;
    private final String oldValue;
    private final String newValue;
    private final long actorId;

    public UpdateSampleOperation(SampleService service, long id, String field, String oldValue, String newValue, long actorId) {
        this.service = service;
        this.id = id;
        this.field = field;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.actorId = actorId;
    }

    @Override
    public void undo() {
        service.update(id, field, oldValue, actorId);
    }

    @Override
    public void redo() {
        service.update(id, field, newValue, actorId);
    }

    @Override
    public String getDescription() {
        return "Update Sample " + id + ": " + field + "='" + newValue + "'";
    }
}
