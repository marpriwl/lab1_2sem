package cli;

import service.MeasurementService;
import service.ProtocolService;
import service.SampleService;
import service.UserService;
import storage.UserStorage;

import java.util.Scanner;

public class CliContext {
    private final SampleService sampleService;
    private final MeasurementService measurementService;
    private final ProtocolService protocolService;
    private final Scanner scanner;
    private final UserService userService;
    private final UserStorage userStorage;

    public CliContext(
            SampleService sampleService,
            MeasurementService measurementService,
            ProtocolService protocolService,
            Scanner scanner,
            UserStorage userStorage,
            UserService userService
    ) {
        this.sampleService = sampleService;
        this.measurementService = measurementService;
        this.protocolService = protocolService;
        this.userService = userService;
        this.userStorage = userStorage;
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

    public UserStorage getUserStorage() {
        return userStorage;
    }
}
