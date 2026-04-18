package service;

import domain.Measurement;
import domain.MeasurementParam;
import domain.Protocol;
import validation.ProtocolValidator;

import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ProtocolService {
    private final TreeMap<Long, Protocol> protocols = new TreeMap<>();
    private final IdGenerator idGenerator = new IdGenerator();

    public long create(String name, Set<MeasurementParam> params) {
        ProtocolValidator.validate(name, params);
        long id = idGenerator.nextId();
        Protocol p = new Protocol(id, name, params, "SYSTEM");
        protocols.put(id, p);
        return id;
    }

    public String apply(long protocolId, long sampleId, MeasurementService measService) {
        Protocol p = protocols.get(protocolId);
        if (p == null) throw new IllegalArgumentException("протокол не найден");
        measService.getAll(); // just to ensure sample exists (we check inside list)

        var existingParams = measService.getAll().values().stream()
                .filter(m -> m.getSampleId() == sampleId)
                .map(Measurement::getParam)
                .collect(Collectors.toSet());

        var missing = p.getRequiredParams().stream()
                .filter(param -> !existingParams.contains(param))
                .toList();

        if (missing.isEmpty()) return "OK protocol is complete";
        return "Missing params: " + missing.stream().map(Enum::name).collect(Collectors.joining(", "));
    }

    public TreeMap<Long, Protocol> getAll() { return protocols; }
}