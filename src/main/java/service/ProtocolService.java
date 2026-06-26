package service;

import domain.Measurement;
import domain.MeasurementParam;
import domain.Protocol;
import storage.DbStorage;
import validation.ProtocolValidator;

import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ProtocolService {
    private final TreeMap<Long, Protocol> protocols = new TreeMap<>();
    private final DbStorage dbStorage;

    public ProtocolService() {
        this(null);
    }

    public ProtocolService(DbStorage dbStorage) {
        this.dbStorage = dbStorage;
    }

    public long create(String name, Set<MeasurementParam> params, long ownerId) {
        ProtocolValidator.validate(name, params);

        long id = dbStorage == null ? IdGenerator.nextProtocolId() : 0;
        Protocol protocol = new Protocol(id, name, params, ownerId);

        if (dbStorage != null) {
            protocol = dbStorage.insertProtocol(protocol);
            id = protocol.getId();
        }

        protocols.put(id, protocol);
        return id;
    }

    public Protocol getById(long id) {
        Protocol protocol = protocols.get(id);
        if (protocol == null) {
            throw new IllegalArgumentException("Protocol id=" + id + " was not found");
        }
        return protocol;
    }

    public String apply(long protocolId, long sampleId, MeasurementService measService) {
        Protocol protocol = protocols.get(protocolId);

        if (protocol == null) {
            throw new IllegalArgumentException("Protocol was not found");
        }

        var existingParams = measService.getAll().values().stream()
                .filter(measurement -> measurement.getSampleId() == sampleId)
                .map(Measurement::getParam)
                .collect(Collectors.toSet());

        var missing = protocol.getRequiredParams().stream()
                .filter(param -> !existingParams.contains(param))
                .toList();

        if (missing.isEmpty()) {
            return "OK protocol is complete";
        }

        return "Missing params: " + missing.stream()
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }

    public void delete(long id) {
        protocols.remove(id);
        if (dbStorage != null) {
            dbStorage.deleteProtocol(id);
        }
    }

    public void restore(Protocol protocol) {
        protocols.put(protocol.getId(), protocol);
        if (dbStorage != null) {
            dbStorage.insertProtocol(protocol);
        }
    }

    public void replaceAll(TreeMap<Long, Protocol> loadedProtocols) {
        protocols.clear();
        protocols.putAll(loadedProtocols);

        long nextId = protocols.isEmpty()
                ? 1
                : protocols.lastKey() + 1;

        IdGenerator.setProtocolId(nextId);
    }

    public void refreshFromDatabase() {
        if (dbStorage == null) {
            return;
        }

        TreeMap<Long, Protocol> loadedProtocols = dbStorage.loadLabData()
                .getProtocols()
                .stream()
                .collect(Collectors.toMap(
                        Protocol::getId,
                        protocol -> protocol,
                        (left, right) -> left,
                        TreeMap::new
                ));

        replaceAll(loadedProtocols);
    }

    public TreeMap<Long, Protocol> getAll() {
        return protocols;
    }
}
