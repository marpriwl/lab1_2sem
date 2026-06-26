package service.history.operations;

import domain.Sample;
import service.SampleService;
import service.history.Operation;

public class AddSampleOperation implements Operation {
    private final SampleService service;
    private final Sample sample;

    public AddSampleOperation(SampleService service, Sample sample) {
        this.service = service;
        this.sample = sample;
    }

    @Override
    public void undo() {
        service.delete(sample.getId());
    }

    @Override
    public void redo() {
        service.restore(sample);
    }

    @Override
    public String getDescription() {
        return "Add Sample: " + sample.getName() + " (ID: " + sample.getId() + ")";
    }
}
