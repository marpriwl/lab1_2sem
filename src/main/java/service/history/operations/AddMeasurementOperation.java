package service.history.operations;

import domain.Measurement;
import service.MeasurementService;
import service.history.Operation;

public class AddMeasurementOperation implements Operation {
    private final MeasurementService service;
    private final Measurement measurement;

    public AddMeasurementOperation(MeasurementService service, Measurement measurement) {
        this.service = service;
        this.measurement = measurement;
    }

    @Override
    public void undo() {
        service.delete(measurement.getId());
    }

    @Override
    public void redo() {
        service.restore(measurement);
    }

    @Override
    public String getDescription() {
        return "Add Measurement " + measurement.getId() + " for Sample " + measurement.getSampleId();
    }
}
