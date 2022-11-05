package utils;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.Properties;

public abstract class CommandUtils {
    public static final String SERVER_ADDRESS_PARAMETER = "serverAddress";
    public static final String JAVA_OPT = "D";
    public static final Option serverAddressOption = new Option(JAVA_OPT, "sets the server address in format: address:port");
    static {
        serverAddressOption.setArgName(SERVER_ADDRESS_PARAMETER);
        serverAddressOption.setRequired(true);
        serverAddressOption.setArgs(2);
    }

    public static Properties parseCommandLine(String[] args, Option... optionArray) throws ParseException {
        Options options = new Options();

        for (Option option : optionArray) {
            option.setArgs(2);
            options.addOption(option);
        }

        return new DefaultParser().parse(options, args, true).getOptionProperties(JAVA_OPT);
    }
}
