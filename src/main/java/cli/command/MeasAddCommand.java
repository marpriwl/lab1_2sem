package cli.command;

import cli.CliContext;
import domain.MeasurementParam;
import validation.MeasurementValidator;

public class MeasAddCommand implements CliCommand {
    @Override
    public String name() {
        return "meas_add";
    }

    @Override
    public String description() {
        return "meas_add <sample_id> — добавить измерение";
    }

    @Override
    public boolean execute(String[] args, CliContext context) {
        if (args.length < 2) {
            throw new IllegalArgumentException("нужен sample_id");
        }

        long sampleId;
        try {
            sampleId = Long.parseLong(args[1].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("sample_id должен быть целым числом");
        }

        if (sampleId <= 0) {
            throw new IllegalArgumentException("sample_id должен быть положительным");
        }

        System.out.print("Параметр (PH/CONDUCTIVITY/TURBIDITY/NITRATE): ");
        MeasurementParam param = MeasurementValidator.validateParam(
                context.getScanner().nextLine()
        );

        System.out.print("Значение: ");
        double value = MeasurementValidator.validateValue(
                context.getScanner().nextLine()
        );

        System.out.print("Единицы: ");
        String unit = context.getScanner().nextLine().trim();
        MeasurementValidator.validateUnit(unit);

        System.out.print("Метод: ");
        String method = context.getScanner().nextLine().trim();
        MeasurementValidator.validateMethod(method);

        long id = context.getMeasurementService().add(sampleId, param, value, unit, method);
        System.out.println("OK measurement_id=" + id);
        return true;
    }
}
