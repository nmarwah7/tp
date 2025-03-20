package seedu.internsprint.handler;

import seedu.internsprint.command.AddGeneralCommand;
import seedu.internsprint.command.AddHardwareCommand;
import seedu.internsprint.command.AddSoftwareCommand;
import seedu.internsprint.command.ByeCommand;
import seedu.internsprint.command.Command;
import seedu.internsprint.command.DescriptionCommand;
import seedu.internsprint.command.EditCommand;
import seedu.internsprint.command.DeleteCommand;
import seedu.internsprint.internship.Internship;
import seedu.internsprint.internship.InternshipList;
import seedu.internsprint.command.HelpCommand;
import seedu.internsprint.command.ListCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static seedu.internsprint.util.InternSprintExceptionMessages.ILLEGAL_VALUE_INPUT;
import static seedu.internsprint.util.InternSprintExceptionMessages.INVALID_COMMAND_TYPE;
import static seedu.internsprint.util.InternSprintExceptionMessages.INVALID_INDEX;
import static seedu.internsprint.util.InternSprintExceptionMessages.INVALID_INDEX_RANGE;
import static seedu.internsprint.util.InternSprintExceptionMessages.MISSING_INDEX;
import static seedu.internsprint.util.InternSprintExceptionMessages.MISSING_VALUE_INPUT;

/**
 * Parses user input.
 */
public class Parser {
    private static Logger logger = Logger.getLogger(Parser.class.getName());

    /**
     * Parses the user input and returns the corresponding Command object.
     *
     * @param userInput User input string.
     * @return Command object corresponding to the user input.
     */
    public static Command parseCommand(String userInput) {
        logger.log(Level.INFO, String.format("User command inside parseCommand: %s", userInput));
        assert userInput != null : "User input should not be null";

        String[] commandTypeAndParams = splitCommandTypeAndParams(userInput.trim());
        assert commandTypeAndParams.length > 0 : "Command type should not be empty";

        String commandType = commandTypeAndParams[0];
        String params = commandTypeAndParams.length > 1 ? commandTypeAndParams[1] : "";

        Command command;
        switch (commandType) {
        case "add software":
            command = new AddSoftwareCommand();
            break;
        case "add hardware":
            command = new AddHardwareCommand();
            break;
        case "add general":
            command = new AddGeneralCommand();
            break;
        case "bye":
            command = new ByeCommand();
            break;
        case "edit":
            command = new EditCommand();
            break;
        case "list":
            command = new ListCommand();
            break;
        case "desc":
            command = new DescriptionCommand();
            break;
        case "help":
            command = new HelpCommand();
            break;
        case "delete":
            command = new DeleteCommand();
            break;
        default:
            throw new IllegalArgumentException(String.format(INVALID_COMMAND_TYPE, commandType));
        }
        parseKeyValuePairs(params, command);
        return command;
    }

    /**
     * Splits the user input into the command type and the parameters.
     *
     * @param userInput User input string.
     * @return Array containing the command type and the parameters.
     */
    private static String[] splitCommandTypeAndParams(String userInput) {
        String[] multiWordCommands = {"add software", "add hardware", "add general", "edit"};
        for (String command : multiWordCommands) {
            if (userInput.startsWith(command)) {
                return new String[]{command, userInput.substring(command.length()).trim()};
            }
        }
        String[] commandTypeAndParams = userInput.split(" ", 2);
        String commandType = commandTypeAndParams[0];
        String params = commandTypeAndParams.length > 1 ? commandTypeAndParams[1] : "";

        return new String[]{commandType, params};
    }

    /**
     * Parses the key-value pairs in the parameters string and sets them in the Command object.
     *
     * @param params Parameters string.
     * @param command Command object.
     */
    protected static void parseKeyValuePairs(String params, Command command) {
        HashMap<String, String> keyValueMap = new HashMap<>();

        if (params.isEmpty()) {
            return;
        }

        String[] parts = params.trim().split("(?=\\s*/[a-zA-Z]+)");
        if (!parts[0].trim().startsWith("/")) {
            keyValueMap.put("description", parts[0].trim());
            if (parts.length == 1) {
                command.setParameters(keyValueMap);
                return;
            }
            parts = Arrays.copyOfRange(parts, 1, parts.length);
        }

        for (String part : parts) {
            if (part.trim().isEmpty()) {
                continue;
            }
            String[] keyValue = part.trim().split("\\s+", 2);
            if (keyValue.length < 2) {
                throw new IllegalArgumentException(String.format(MISSING_VALUE_INPUT, keyValue[0]));
            }

            String key = keyValue[0].trim();
            String value = keyValue[1].trim();

            if (value.contains("/")) {
                throw new IllegalArgumentException(ILLEGAL_VALUE_INPUT);
            }
            keyValueMap.put(key, value);
        }

        command.setParameters(keyValueMap);
    }

    /**
     * Validates the index input by the user.
     *
     * @param index Index input by the user.
     * @param internships InternshipList object.
     * @return Array containing the type and index of the internship.
     */
    public static String[] validateIndex(String index, InternshipList internships) {
        if (index.isEmpty()) {
            throw new IllegalArgumentException(MISSING_INDEX);
        }
        try {
            HashMap<String, ArrayList<Internship>> internshipsMap = internships.getInternshipMap();
            int taskCount = internships.getInternshipCount();

            int indexValue = Integer.parseInt(index);
            if (indexValue < 1 || indexValue > taskCount) {
                throw new IllegalArgumentException(INVALID_INDEX_RANGE);
            }
            String type;
            if (indexValue <= internshipsMap.get("software").size()) {
                type = "software";
            } else if (indexValue <= internshipsMap.get("software").size() + internshipsMap.get("hardware").size()) {
                indexValue -= internshipsMap.get("software").size();
                type = "hardware";
            } else {
                indexValue -= internshipsMap.get("software").size();
                indexValue -= internshipsMap.get("hardware").size();
                type = "general";
            }
            return new String[]{type, Integer.toString(indexValue - 1)};
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(INVALID_INDEX);
        }
    }

}
