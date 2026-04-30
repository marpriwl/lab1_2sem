package storage;

import domain.Sample;
import domain.Measurement;
import domain.Protocol;

import java.util.ArrayList;
import java.util.List;

public class LabData {

    private List<Sample> samples = new ArrayList<>();
    private List<Measurement> measurements = new ArrayList<>();
    private List<Protocol> protocols = new ArrayList<>();

    public LabData() {
    }

    public LabData(List<Sample> samples,
                   List<Measurement> measurements,
                   List<Protocol> protocols) {
        this.samples = samples;
        this.measurements = measurements;
        this.protocols = protocols;
    }

    public List<Sample> getSamples() {
        return samples;
    }

    public void setSamples(List<Sample> samples) {
        this.samples = samples;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<Measurement> measurements) {
        this.measurements = measurements;
    }

    public List<Protocol> getProtocols() {
        return protocols;
    }

    public void setProtocols(List<Protocol> protocols) {
        this.protocols = protocols;
    }

}
