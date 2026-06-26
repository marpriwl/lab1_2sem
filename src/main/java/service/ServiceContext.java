package service;

import db.DataBaseInitializer;
import domain.Measurement;
import domain.Protocol;
import domain.Sample;
import domain.User;
import storage.DbStorage;
import storage.LabData;

import service.history.HistoryService;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ServiceContext {

    private static final DbStorage dbStorage = createDbStorage();
    private static final SampleService sampleService = new SampleService(dbStorage);

    private static final MeasurementService measurementService =
            new MeasurementService(sampleService, dbStorage);

    private static final ProtocolService protocolService = new ProtocolService(dbStorage);
    private static final UserService userService = new UserService(loadUsers(), dbStorage);
    private static final HistoryService historyService = new HistoryService();

    static {
        refreshFromDatabase();
    }

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

    public static UserService getUserService() {
        return userService;
    }

    public static HistoryService getHistoryService() {
        return historyService;
    }

    public static boolean isDatabaseMode() {
        return dbStorage != null;
    }

    public static void refreshFromDatabase() {
        if (dbStorage == null) {
            return;
        }

        LabData data = dbStorage.loadLabData();

        sampleService.replaceAll(toSampleMap(data));
        measurementService.replaceAll(toMeasurementMap(data));
        protocolService.replaceAll(toProtocolMap(data));
    }

    private static DbStorage createDbStorage() {
        try {
            DataBaseInitializer.init();
            return new DbStorage();
        } catch (Exception exception) {
            System.out.println("DB mode is unavailable: " + exception.getMessage());
            return null;
        }
    }

    private static List<User> loadUsers() {
        if (dbStorage == null) {
            return new ArrayList<>();
        }

        return dbStorage.findAllUsers();
    }

    private static TreeMap<Long, Sample> toSampleMap(LabData data) {
        return data.getSamples().stream()
                .collect(Collectors.toMap(
                        Sample::getId,
                        Function.identity(),
                        (left, right) -> left,
                        TreeMap::new
                ));
    }

    private static TreeMap<Long, Measurement> toMeasurementMap(LabData data) {
        return data.getMeasurements().stream()
                .collect(Collectors.toMap(
                        Measurement::getId,
                        Function.identity(),
                        (left, right) -> left,
                        TreeMap::new
                ));
    }

    private static TreeMap<Long, Protocol> toProtocolMap(LabData data) {
        return data.getProtocols().stream()
                .collect(Collectors.toMap(
                        Protocol::getId,
                        Function.identity(),
                        (left, right) -> left,
                        TreeMap::new
                ));
    }
}
