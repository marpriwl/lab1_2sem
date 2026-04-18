package cli.command;

import cli.CliContext;
import domain.MeasurementParam;

public class MeasStatsCommand implements CliCommand {
    @Override
    public String name() {
        return "meas_stats";
    }

    @Override
    public String description() {
        return "meas_stats <sample_id> <param> — показать статистику по параметру измерения";
    }

    @Override
    public boolean execute(String[] args, CliContext context) {
        if (args.length < 3) {
            throw new IllegalArgumentException("нужен sample_id и param");
        }

        long sampleId = Long.parseLong(args[1]);
        MeasurementParam param = MeasurementParam.valueOf(args[2].toUpperCase());

        System.out.println(context.getMeasurementService().stats(sampleId, param));
        return true;
    }
}
