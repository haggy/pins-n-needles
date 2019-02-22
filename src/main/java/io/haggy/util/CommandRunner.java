package io.haggy.util;

import com.pi4j.io.gpio.GpioController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class CommandRunner {

    private final GpioController gpioController;
    private final PinManager pinManager;
    private final PNNConfig config;

    public CommandRunner(GpioController gpio, PNNConfig config) {
        this.gpioController = gpio;
        this.pinManager = new PinManager(gpioController);
        this.config = config;
    }

    public int run(ArrayList<String> commands, int repeatCount) {
        AtomicInteger numValidCommands = new AtomicInteger();
        int numIterations = 1;
        do {
            numIterations++;
            commands.forEach((cmd) -> {
                try {
                    System.out.println("Running command: [" + cmd + "]");
                    String[] parts = cmd.split(" ");
                    if (parts.length == 3 && !isCommentLine(parts)) {
                        numValidCommands.getAndIncrement();
                        String pinState = parts[0];
                        String pinId = parts[1];
                        Long stateDuration = Long.valueOf(parts[2]);

                        String state = "LOW";
                        switch (pinState) {
                            case "H":
                                state = "HIGH";
                                pinManager.pinHigh(pinId);
                                System.out.println("Keeping pin state [" + state + "] for [" + stateDuration + "] millis");
                                Thread.sleep(stateDuration);
                                break;

                            case "L":
                                pinManager.pinLow(pinId);
                                System.out.println("Keeping pin state [" + state + "] for [" + stateDuration + "] millis");
                                Thread.sleep(stateDuration);
                                break;

                            case "P":
                                System.out.println("Pulsing pin state for [" + stateDuration + "] millis");
                                pinManager.pulsePin(pinId, stateDuration, true);

                            default:
                                pinManager.pinLow(pinId);
                        }


                    } else if(isSleepCommandLine(parts)) {
                        long sleepDuration = Long.valueOf(parts[1]);
                        Thread.sleep(sleepDuration);
                    } else if (isCommentLine(parts)) {
                        System.out.println(cmd);
                    } else {
                        System.err.println("WARN: Skipping command: " + cmd);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        } while(numIterations < repeatCount);


        return numValidCommands.get();
    }

    private boolean isCommentLine(String[] lineParts) { return lineParts.length > 0 && lineParts[0].equals("#"); }

    private boolean isSleepCommandLine(String[] lineParts) { return lineParts.length == 2 && lineParts[0].equals("S"); }
}
