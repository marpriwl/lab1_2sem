package cli.command;

import cli.CliContext;
import domain.Sample;
import service.history.operations.UpdateSampleOperation;

public class SampleUpdateCommand implements CliCommand {
    @Override
    public String name() {
        return "sample_update";
    }

    @Override
    public String description() {
        return "sample_update <id> field=value — поменять параметр образца";
    }

    @Override
    public boolean execute(String[] args, CliContext context) {
        long actorId = context.getUserService().requireLogin().getId();

        if (args.length < 3) {
            throw new IllegalArgumentException("нужен id и field=value");
        }

        long id = Long.parseLong(args[1]);

        for (int i = 2; i < args.length; i++) {
            String[] kv = args[i].split("=", 2);
            if (kv.length != 2) {
                continue;
            }
            String field = kv[0].toLowerCase();
            String newValue = kv[1];
            
            Sample sample = context.getSampleService().getById(id);
            String oldValue = getFieldValue(sample, field);
            
            context.getSampleService().update(id, field, newValue, actorId);
            context.getHistoryService().addOperation(
                    new UpdateSampleOperation(context.getSampleService(), id, field, oldValue, newValue, actorId)
            );
        }

        System.out.println("OK");
        return true;
    }

    private String getFieldValue(Sample sample, String field) {
        return switch (field) {
            case "name" -> sample.getName();
            case "type" -> sample.getType();
            case "location" -> sample.getLocation();
            case "status" -> sample.getStatus().name();
            default -> throw new IllegalArgumentException("Unknown field: " + field);
        };
    }
}
