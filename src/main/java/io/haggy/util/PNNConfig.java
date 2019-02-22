package io.haggy.util;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.IOException;

public class PNNConfig {

    public String getCommandFile() {
        return commandFile;
    }

    public Integer getLoopCount() {
        return loopCount;
    }

    public class ConfigException extends Exception {
        public ConfigException(String reason) {
            super(reason);
        }
    }

    private final String commandFile;
    private final Integer loopCount;

    private PNNConfig(String commandFile, Integer loopCount) throws ConfigException {
        if(commandFile == null || loopCount == null) {
            throw new ConfigException("Invalid parameters");
        }
        this.commandFile = commandFile;

        this.loopCount = loopCount;
    }

    public static PNNConfig fromArgs(String[] args) throws IOException {

        OptionParser parser = new OptionParser();
        OptionSpec<String> cmdFileOpt = parser.accepts( "cmdFile" )
                .withRequiredArg()
                .describedAs("The file containing the pin commands")
                .ofType(String.class);
        OptionSpec<Integer> loopCountOpt = parser.accepts( "loopCount" )
                .withOptionalArg()
                .describedAs("The number of times to repeat the command file")
                .ofType(Integer.class)
                .defaultsTo(0);

        parser.accepts("help").forHelp();

        OptionSet options = parser.parse(args);

        if(options.has("help")) {
            parser.printHelpOn( System.out );
            return null;
        } else {
            try {
                return new PNNConfig(
                        options.valueOf(cmdFileOpt),
                        options.valueOf(loopCountOpt));
            } catch(ConfigException ce) {
                parser.printHelpOn( System.out );
                return null;
            }
        }

    }
}
