package service;

public class IdGenerator {
    private static long sampleIdCounter = 1;
    private static long measurementIdCounter = 1;
    private static long protocolIdCounter = 1;

    private IdGenerator() {}

    public static synchronized long nextSampleId() {
            return sampleIdCounter++;
        }
    public static synchronized long nextMeasurementId() {
        return measurementIdCounter++;
    }
    public static synchronized long nextProtocolId() {
        return protocolIdCounter++;
    }

}
