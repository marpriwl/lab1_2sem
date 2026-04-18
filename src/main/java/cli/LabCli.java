package cli;

import domain.MeasurementParam;
import domain.SampleStatus;
import service.MeasurementService;
import service.ProtocolService;
import service.SampleService;

import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class LabCli {
    private final SampleService sampleService = new SampleService();
    private final MeasurementService measurementService = new MeasurementService(sampleService);
    private final ProtocolService protocolService = new ProtocolService();
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        System.out.println("Введите help для списка команд.");
        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;
            if (line.equalsIgnoreCase("exit")) break;
            if (line.equalsIgnoreCase("help")) {
                printHelp();
                continue;
            }
            try {
                processCommand(line);
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
        System.out.println("До свидания!");
    }

    private void processCommand(String line) {
        String[] parts = line.split("\\s+");
        String cmd = parts[0].toLowerCase();

        switch (cmd) {
            case "sample_add" -> sampleAdd();
            case "sample_list" -> sampleList(parts);
            case "sample_show" -> sampleShow(parts);
            case "sample_update" -> sampleUpdate(parts);
            case "sample_archive" -> sampleArchive(parts);
            case "meas_add" -> measAdd(parts);
            case "meas_list" -> measList(parts);
            case "meas_stats" -> measStats(parts);
            case "prot_create" -> protCreate();
            case "prot_apply" -> protApply(parts);
            default -> throw new IllegalArgumentException("неизвестная команда");
        }
    }

    private void printHelp() {
        System.out.println("""
                Команды:
                sample_add  — создать образец 
                sample_list — вывести список образцов
                sample_show <id> — показать карточку образца 
                sample_update <id> field=value — поменять параметр образца
                sample_archive <id> — добавить образец в архив (Будьте внимательны, после этой команды изменения образца не могут быть произведены)
                meas_add <sample_id>  — добавить измерение 
                meas_list <sample_id> [--param PH|...] [--last N] — показать измерения образца (N — номер измерения)
                meas_stats <sample_id> <param> — показать статистику по параметру измерения 
                prot_create  — создать протокол 
                prot_apply <protocol_id> <sample_id> — проверить выполнен ли протокол для образца 
                help / exit
                """);
    }

    private void sampleAdd() {
        System.out.print("Название: ");
        String name = scanner.nextLine().trim();
        System.out.print("Тип: ");
        String type = scanner.nextLine().trim();
        System.out.print("Место: ");
        String location = scanner.nextLine().trim();

        long id = sampleService.add(name, type, location);
        System.out.println("OK sample_id=" + id);
    }

    private void sampleList(String[] parts) {
        String status = null;
        for (int i = 1; i < parts.length; i++) {
            if (parts[i].equals("--status") && i + 1 < parts.length) {
                status = parts[i + 1];
            }
        }
        System.out.println("ID   Name                 Type       Location        Status");
        System.out.println(sampleService.list(status));
    }

    private void sampleShow(String[] parts) {
        if (parts.length < 2) throw new IllegalArgumentException("нужен id");
        long id = Long.parseLong(parts[1]);
        var s = sampleService.getById(id);
        System.out.printf("Sample #%d%nname: %s%ntype: %s%nlocation: %s%nstatus: %s%nowner: %s%nmeasurements: %d%n",
                s.getId(), s.getName(), s.getType(), s.getLocation(), s.getStatus(), s.getOwnerUsername(), 0); // measurements count можно добавить позже
    }

    private void sampleUpdate(String[] parts) {
        if (parts.length < 3) throw new IllegalArgumentException("нужен id и field=value");
        long id = Long.parseLong(parts[1]);
        for (int i = 2; i < parts.length; i++) {
            String[] kv = parts[i].split("=", 2);
            if (kv.length != 2) continue;
            sampleService.update(id, kv[0], kv[1]);
        }
        System.out.println("OK");
    }

    private void sampleArchive(String[] parts) {
        if (parts.length < 2) throw new IllegalArgumentException("нужен id");
        long id = Long.parseLong(parts[1]);
        sampleService.archive(id);
        System.out.println("OK sample " + id + " archived");
    }

    private void measAdd(String[] parts) {
        if (parts.length < 2) throw new IllegalArgumentException("нужен sample_id");
        long sampleId = Long.parseLong(parts[1]);

        System.out.print("Параметр (PH/CONDUCTIVITY/TURBIDITY/NITRATE): ");
        MeasurementParam param = MeasurementParam.valueOf(scanner.nextLine().trim().toUpperCase());
        System.out.print("Значение: ");
        double value = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Единицы: ");
        String unit = scanner.nextLine().trim();
        System.out.print("Метод: ");
        String method = scanner.nextLine().trim();

        long id = measurementService.add(sampleId, param, value, unit, method);
        System.out.println("OK measurement_id=" + id);
    }

    private void measList(String[] parts) {
        if (parts.length < 2) throw new IllegalArgumentException("нужен sample_id");
        long sampleId = Long.parseLong(parts[1]);
        MeasurementParam param = null;
        int lastN = 0;
        for (int i = 2; i < parts.length; i++) {
            if (parts[i].equals("--param") && i + 1 < parts.length) {
                param = MeasurementParam.valueOf(parts[i + 1].toUpperCase());
            }
            if (parts[i].equals("--last") && i + 1 < parts.length) {
                lastN = Integer.parseInt(parts[i + 1]);
            }
        }
        System.out.println("ID   Param        Value    Unit     Method          Time");
        System.out.println(measurementService.list(sampleId, param, lastN));
    }

    private void measStats(String[] parts) {
        if (parts.length < 3) throw new IllegalArgumentException("нужен sample_id и param");
        long sampleId = Long.parseLong(parts[1]);
        MeasurementParam param = MeasurementParam.valueOf(parts[2].toUpperCase());
        System.out.println(measurementService.stats(sampleId, param));
    }

    private void protCreate() {
        System.out.print("Название протокола: ");
        String name = scanner.nextLine().trim();
        System.out.print("Обязательные параметры (через запятую): ");
        String[] p = scanner.nextLine().trim().split(",");
        Set<MeasurementParam> params = Set.of(p).stream()
                .map(String::trim)
                .map(String::toUpperCase)
                .map(MeasurementParam::valueOf)
                .collect(Collectors.toSet());

        long id = protocolService.create(name, params);
        System.out.println("OK protocol_id=" + id);
    }

    private void protApply(String[] parts) {
        if (parts.length < 3) throw new IllegalArgumentException("нужны protocol_id и sample_id");
        long protId = Long.parseLong(parts[1]);
        long sampleId = Long.parseLong(parts[2]);
        System.out.println(protocolService.apply(protId, sampleId, measurementService));
    }
}
