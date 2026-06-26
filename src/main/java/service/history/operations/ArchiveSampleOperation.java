package service.history.operations;

import domain.SampleStatus;
import service.SampleService;
import service.history.Operation;

public class ArchiveSampleOperation implements Operation {
    private final SampleService service;
    private final long id;
    private final long actorId;

    public ArchiveSampleOperation(SampleService service, long id, long actorId) {
        this.service = service;
        this.id = id;
        this.actorId = actorId;
    }

    @Override
    public void undo() {
        service.update(id, "status", SampleStatus.ACTIVE.name(), actorId);
    }

    @Override
    public void redo() {
        service.archive(id, actorId);
    }

    @Override
    public String getDescription() {
        return "Archive Sample " + id;
    }
}
