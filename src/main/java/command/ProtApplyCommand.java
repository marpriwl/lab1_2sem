package cli.command;

import cli.CliContext;

public class ProtApplyCommand implements CliCommand {
    @Override
    public String name() {
        return "prot_apply";
    }

    @Override
    public String description() {
        return "prot_apply <protocol_id> <sample_id> — проверить выполнен ли протокол для образца";
    }

    @Override
    public boolean execute(String[] args, CliContext context) {
        if (args.length < 3) {
            throw new IllegalArgumentException("нужны protocol_id и sample_id");
        }

        long protocolId = Long.parseLong(args[1]);
        long sampleId = Long.parseLong(args[2]);

        System.out.println(
                context.getProtocolService().apply(protocolId, sampleId, context.getMeasurementService())
        );

        return true;
    }
}
