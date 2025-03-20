package seedu.internsprint.command;

import seedu.internsprint.internship.Internship;
import seedu.internsprint.internship.InternshipList;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static seedu.internsprint.util.InternSprintMessages.ADD_MESSAGE_SUCCESS;
import static seedu.internsprint.util.InternSprintMessages.MESSAGE_DUPLICATE_INTERNSHIP;
import static seedu.internsprint.util.InternSprintMessages.LIST_COUNT_MESSAGE;

/**
 * Represents a command to add an internship.
 */
public abstract class AddCommand extends Command {
    protected static Logger logger = Logger.getLogger(AddCommand.class.getName());
    protected final Set<String> requiredParameters;
    protected final Set<String> optionalParameters;

    public AddCommand(Set<String> requiredParameters, Set<String> optionalParameters) {
        this.requiredParameters = requiredParameters;
        this.optionalParameters = optionalParameters;
    }

    /**
     * Checks if the parameters entered by the user are valid.
     *
     * @return True if the parameters are valid, false otherwise.
     */
    @Override
    protected boolean isValidParameters() {
        return parameters.keySet().containsAll(requiredParameters);
    }

    /**
     * Gets the usage message for the command.
     *
     * @return Usage message.
     */
    protected abstract String getUsageMessage();

    /**
     * Creates an internship object.
     *
     * @return Internship object.
     */
    protected abstract Internship createInternship();

    /**
     * Executes the command to add an internship.
     *
     * @param internships InternshipList object.
     * @return CommandResult object.
     */
    @Override
    public CommandResult execute(InternshipList internships) {
        logger.log(Level.INFO, "Executing add command");
        CommandResult result;
        if (!isValidParameters()) {
            logger.log(Level.WARNING, "Invalid parameters entered");
            result = new CommandResult(getUsageMessage());
            result.setSuccessful(false);
            return result;
        }

        Internship toAdd;
        toAdd = createInternship();

        if (internships.contains(toAdd)) {
            result = new CommandResult(MESSAGE_DUPLICATE_INTERNSHIP);
            result.setSuccessful(false);
            return result;
        }
        assert !internships.contains(toAdd) : "Internship should not be present in the list";

        List<String> feedback = new ArrayList<>();

        internships.addInternship(toAdd);
        assert internships.contains(toAdd) : "Internship should be present in the list";

        feedback.add(ADD_MESSAGE_SUCCESS);
        feedback.add(toAdd.toString());
        feedback.add(String.format(LIST_COUNT_MESSAGE, internships.getInternshipCount()));

        try {
            internships.saveInternships();
            //feedback.add(InternSprintMessages.SAVE_SUCCESS_MESSAGE);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error saving internships after adding an internship");
            feedback.add(e.getMessage());
            result = new CommandResult(feedback);
            result.setSuccessful(false);
            return result;
        }

        logger.log(Level.INFO, "Internship added successfully and saved to file");
        result = new CommandResult(feedback);
        result.setSuccessful(true);
        return result;
    }
}
