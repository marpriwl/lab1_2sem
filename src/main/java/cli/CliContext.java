package cli;

import service.MeasurementService;
import service.ProtocolService;
import service.SampleService;
import service.UserService;

import service.history.HistoryService;

import java.util.Scanner;

public class CliContext {
    private final SampleService sampleService;
    private final MeasurementService measurementService;
    private final ProtocolService protocolService;
    private final Scanner scanner;
    private final UserService userService;
    private final HistoryService historyService;

    public CliContext(
            SampleService sampleService,
            MeasurementService measurementService,
            ProtocolService protocolService,
            Scanner scanner,
            UserService userService,
            HistoryService historyService
    ) {
        this.sampleService = sampleService;
        this.measurementService = measurementService;
        this.protocolService = protocolService;
        this.userService = userService;
        this.scanner = scanner;
        this.historyService = historyService;
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

    public HistoryService getHistoryService() {
        return historyService;
    }
}
