package cli.command;

import cli.CliContext;
import domain.MeasurementParam;

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

        long sampleId = Long.parseLong(args[1]);

        System.out.print("Параметр (PH/CONDUCTIVITY/TURBIDITY/NITRATE): ");
        MeasurementParam param = MeasurementParam.valueOf(
                context.getScanner().nextLine().trim().toUpperCase()
        );

        System.out.print("Значение: ");
        double value = Double.parseDouble(context.getScanner().nextLine().trim());

        System.out.print("Единицы: ");
        String unit = context.getScanner().nextLine().trim();

        System.out.print("Метод: ");
        String method = context.getScanner().nextLine().trim();

        long id = context.getMeasurementService().add(sampleId, param, value, unit, method);
        System.out.println("OK measurement_id=" + id);
        return true;
    }
}
