package cli.command;

import cli.CliContext;
import domain.MeasurementParam;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ProtCreateCommand implements CliCommand {
    @Override
    public String name() {
        return "prot_create";
    }

    @Override
    public String description() {
        return "prot_create — создать протокол";
    }

    @Override
    public boolean execute(String[] args, CliContext context) {
        System.out.print("Название протокола: ");
        String name = context.getScanner().nextLine().trim();

        System.out.print("Обязательные параметры (через запятую): ");
        String[] parts = context.getScanner().nextLine().trim().split(",");

        Set<MeasurementParam> params = Arrays.stream(parts)
                .map(String::trim)
                .map(String::toUpperCase)
                .map(MeasurementParam::valueOf)
                .collect(Collectors.toSet());

        long id = context.getProtocolService().create(name, params);
        System.out.println("OK protocol_id=" + id);
        return true;
    }
}
