package cli.command;

import cli.CliContext;
import domain.MeasurementParam;

public class MeasListCommand implements CliCommand {
    @Override
    public String name() {
        return "meas_list";
    }

    @Override
    public String description() {
        return "meas_list <sample_id> [--param PH|...] [--last N] — показать измерения образца";
    }

    @Override
    public boolean execute(String[] args, CliContext context) {
        if (args.length < 2) {
            throw new IllegalArgumentException("нужен sample_id");
        }

        long sampleId = Long.parseLong(args[1]);
        MeasurementParam param = null;
        int lastN = 0;

        for (int i = 2; i < args.length; i++) {
            if (args[i].equals("--param") && i + 1 < args.length) {
                param = MeasurementParam.valueOf(args[i + 1].toUpperCase());
            }
            if (args[i].equals("--last") && i + 1 < args.length) {
                lastN = Integer.parseInt(args[i + 1]);
            }
        }

        System.out.println("ID   Param        Value    Unit     Method          Time");
        System.out.println(context.getMeasurementService().list(sampleId, param, lastN));
        return true;
    }
}