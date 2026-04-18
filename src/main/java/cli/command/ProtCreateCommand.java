package cli.command;

import cli.CliContext;
import domain.MeasurementParam;
import validation.ProtocolValidator;

import java.util.Set;

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
        ProtocolValidator.validateName(name);

        System.out.print("Обязательные параметры (через запятую): ");
        Set<MeasurementParam> params = ProtocolValidator.parseParams(
                context.getScanner().nextLine()
        );

        long id = context.getProtocolService().create(name, params);
        System.out.println("OK protocol_id=" + id);
        return true;
    }
}