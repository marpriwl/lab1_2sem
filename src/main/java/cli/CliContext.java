package cli;

import service.MeasurementService;
import service.ProtocolService;
import service.SampleService;
import service.UserService;

import java.util.Scanner;

public class CliContext {
    private final SampleService sampleService;
    private final MeasurementService measurementService;
    private final ProtocolService protocolService;
    private final Scanner scanner;
    private final UserService userService;

    public CliContext(
            SampleService sampleService,
            MeasurementService measurementService,
            ProtocolService protocolService,
            Scanner scanner,
            UserService userService
    ) {
        this.sampleService = sampleService;
        this.measurementService = measurementService;
        this.protocolService = protocolService;
        this.userService = userService;
        this.scanner = scanner;
    }

    public SampleService getSampleService() {
        return sampleService;
    }

    public MeasurementService getMeasurementService() {
        return measurementService;
    }

    public ProtocolService getProtocolService() {
        return protocolService;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public UserService getUserService() {
        return userService;
    }
}
