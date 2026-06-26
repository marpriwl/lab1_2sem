package cli.command;

import cli.CliContext;
import domain.Sample;
import service.history.operations.AddSampleOperation;
import validation.SampleValidator;

public class SampleAddCommand implements CliCommand {
    @Override
    public String name() {
        return "sample_add";
    }

    @Override
    public String description() {
        return "sample_add — создать образец";
    }

    @Override
    public boolean execute(String[] args, CliContext context) {
        long ownerId = context.getUserService().requireLogin().getId();

        System.out.print("Название: ");
        String name = context.getScanner().nextLine().trim();
        SampleValidator.validateName(name);

        System.out.print("Тип: ");
        String type = context.getScanner().nextLine().trim();
        SampleValidator.validateType(type);

        System.out.print("Место: ");
        String location = context.getScanner().nextLine().trim();
        SampleValidator.validateLocation(location);

        long id = context.getSampleService().add(name, type, location, ownerId);
        Sample sample = context.getSampleService().getById(id);
        context.getHistoryService().addOperation(new AddSampleOperation(context.getSampleService(), sample));

        System.out.println("OK sample_id=" + id);
        return true;
    }
}
