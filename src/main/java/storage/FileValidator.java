package storage;

import domain.Measurement;
import domain.Protocol;
import domain.Sample;
import validation.MeasurementValidator;
import validation.ProtocolValidator;
import validation.SampleValidator;

import java.util.HashSet;
import java.util.Set;

public class FileValidator {

    public void validate(LabData data) {
        if (data == null) {
            throw new IllegalArgumentException("файл не содержит данные");
        }

        if (data.getSamples() == null) {
            throw new IllegalArgumentException("поле samples отсутствует");
        }

        if (data.getMeasurements() == null) {
            throw new IllegalArgumentException("поле measurements отсутствует");
        }

        if (data.getProtocols() == null) {
            throw new IllegalArgumentException("поле protocols отсутствует");
        }

        validateSamples(data);
        validateMeasurements(data);
        validateProtocols(data);
    }

    private void validateSamples(LabData data) {
        Set<Long> sampleIds = new HashSet<>();

        for (Sample sample : data.getSamples()) {
            if (sample == null) {
                throw new IllegalArgumentException("список samples содержит null-объект");
            }

            if (sample.getId() <= 0) {
                throw new IllegalArgumentException("sample.id должен быть положительным");
            }

            if (!sampleIds.add(sample.getId())) {
                throw new IllegalArgumentException("дублирующийся sample.id=" + sample.getId());
            }

            SampleValidator.validate(
                    sample.getName(),
                    sample.getType(),
                    sample.getLocation()
            );

            if (sample.getStatus() == null) {
                throw new IllegalArgumentException("sample.status отсутствует у sample.id=" + sample.getId());
            }

            if (sample.getOwnerUsername() == null || sample.getOwnerUsername().trim().isEmpty()) {
                throw new IllegalArgumentException("sample.ownerUsername отсутствует у sample.id=" + sample.getId());
            }

            if (sample.getCreatedAt() == null) {
                throw new IllegalArgumentException("sample.createdAt отсутствует у sample.id=" + sample.getId());
            }

            if (sample.getUpdatedAt() == null) {
                throw new IllegalArgumentException("sample.updatedAt отсутствует у sample.id=" + sample.getId());
            }
        }
    }

    private void validateMeasurements(LabData data) {
        Set<Long> measurementIds = new HashSet<>();
        Set<Long> sampleIds = new HashSet<>();

        for (Sample sample : data.getSamples()) {
            sampleIds.add(sample.getId());
        }

        for (Measurement measurement : data.getMeasurements()) {
            if (measurement == null) {
                throw new IllegalArgumentException("список measurements содержит null-объект");
            }

            if (measurement.getId() <= 0) {
                throw new IllegalArgumentException("measurement.id должен быть положительным");
            }

            if (!measurementIds.add(measurement.getId())) {
                throw new IllegalArgumentException("дублирующийся measurement.id=" + measurement.getId());
            }

            if (!sampleIds.contains(measurement.getSampleId())) {
                throw new IllegalArgumentException(
                        "measurement.sampleId=" + measurement.getSampleId()
                                + " ссылается на несуществующий sample"
                );
            }

            if (measurement.getParam() == null) {
                throw new IllegalArgumentException("measurement.param отсутствует у measurement.id=" + measurement.getId());
            }

            MeasurementValidator.validate(
                    measurement.getValue(),
                    measurement.getUnit(),
                    measurement.getMethod()
            );

            if (measurement.getMeasuredAt() == null) {
                throw new IllegalArgumentException("measurement.measuredAt отсутствует у measurement.id=" + measurement.getId());
            }

            if (measurement.getOwnerUsername() == null || measurement.getOwnerUsername().trim().isEmpty()) {
                throw new IllegalArgumentException("measurement.ownerUsername отсутствует у measurement.id=" + measurement.getId());
            }

            if (measurement.getCreatedAt() == null) {
                throw new IllegalArgumentException("measurement.createdAt отсутствует у measurement.id=" + measurement.getId());
            }

            if (measurement.getUpdatedAt() == null) {
                throw new IllegalArgumentException("measurement.updatedAt отсутствует у measurement.id=" + measurement.getId());
            }
        }
    }

    private void validateProtocols(LabData data) {
        Set<Long> protocolIds = new HashSet<>();

        for (Protocol protocol : data.getProtocols()) {
            if (protocol == null) {
                throw new IllegalArgumentException("список protocols содержит null-объект");
            }

            if (protocol.getId() <= 0) {
                throw new IllegalArgumentException("protocol.id должен быть положительным");
            }

            if (!protocolIds.add(protocol.getId())) {
                throw new IllegalArgumentException("дублирующийся protocol.id=" + protocol.getId());
            }

            ProtocolValidator.validate(
                    protocol.getName(),
                    protocol.getRequiredParams()
            );

            if (protocol.getOwnerUsername() == null || protocol.getOwnerUsername().trim().isEmpty()) {
                throw new IllegalArgumentException("protocol.ownerUsername отсутствует у protocol.id=" + protocol.getId());
            }

            if (protocol.getCreatedAt() == null) {
                throw new IllegalArgumentException("protocol.createdAt отсутствует у protocol.id=" + protocol.getId());
            }

            if (protocol.getUpdatedAt() == null) {
                throw new IllegalArgumentException("protocol.updatedAt отсутствует у protocol.id=" + protocol.getId());
            }
        }
    }
}