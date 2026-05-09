package service;

public final class ServiceContext {

    private static final SampleService sampleService = new SampleService();

    private static final MeasurementService measurementService =
            new MeasurementService(sampleService);

    private static final ProtocolService protocolService = new ProtocolService();

    private ServiceContext() {
    }

    public static SampleService getSampleService() {
        return sampleService;
    }

    public static MeasurementService getMeasurementService() {
        return measurementService;
    }

    public static ProtocolService getProtocolService() {
        return protocolService;
    }
}