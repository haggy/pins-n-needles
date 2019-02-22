package io.haggy;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import io.haggy.util.CommandRunner;
import io.haggy.util.PNNConfig;

import java.io.*;
import java.util.ArrayList;

public class PinsNNeedles {

    public static void main(String[] args) throws IOException {
        PNNConfig conf = PNNConfig.fromArgs(args);
        if(conf == null) return;

        final GpioController gpio = GpioFactory.getInstance();

        System.out.println("Starting up the command runner");
        CommandRunner cm = new CommandRunner(gpio, conf);
        int numCommandsRun = cm.run(readCommandsFromFile(conf.getCommandFile()), conf.getLoopCount());

        System.out.println("Ran [" + numCommandsRun + "] commands. Shutting down");
        gpio.shutdown();
    }

    private static ArrayList<String> readCommandsFromFile(String path) throws IOException {
        ArrayList<String> commands = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            while (br.ready()) {
                commands.add(br.readLine());
            }
        }

        return commands;
    }
}
