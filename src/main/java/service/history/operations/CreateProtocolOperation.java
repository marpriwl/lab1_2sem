package service.history.operations;

import domain.Protocol;
import service.ProtocolService;
import service.history.Operation;

public class CreateProtocolOperation implements Operation {
    private final ProtocolService service;
    private final Protocol protocol;

    public CreateProtocolOperation(ProtocolService service, Protocol protocol) {
        this.service = service;
        this.protocol = protocol;
    }

    @Override
    public void undo() {
        service.delete(protocol.getId());
    }

    @Override
    public void redo() {
        service.restore(protocol);
    }

    @Override
    public String getDescription() {
        return "Create Protocol: " + protocol.getName() + " (ID: " + protocol.getId() + ")";
    }
}
