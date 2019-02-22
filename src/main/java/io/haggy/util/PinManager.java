package io.haggy.util;
import com.pi4j.io.gpio.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PinManager {

    private final GpioController gpioController;

    private final Map<String, GpioPinDigitalOutput> pinMap;

    private static final String PIN_NAME_PREFIX = "GPIO ";

    public PinManager(GpioController gc) {
        this.gpioController = gc;
        pinMap = new HashMap<>();
    }

    private String getPinNameForId(String pinId) { return PinManager.PIN_NAME_PREFIX + pinId; }

    private GpioPinDigitalOutput getPin(String pinId) {
        String pinName = getPinNameForId(pinId);
        if(pinMap.containsKey(pinName)) {
            return pinMap.get(pinName);
        } else {
            GpioPinDigitalOutput p = gpioController.provisionDigitalOutputPin(
                    RaspiPin.getPinByName(pinName), UUID.randomUUID().toString(), PinState.LOW
            );
            if(p != null) {
                p.setShutdownOptions(true, PinState.LOW);
                pinMap.put(pinName, p);
            }
            return p;
        }
    }

    private void pinLow(GpioPinDigitalOutput p) {
        if(p != null) {
            p.low();
        } else {
            System.err.println("PIN is not a valid digital output pin");
        }
    }

    private void pinHigh(GpioPinDigitalOutput p) {
        if(p != null) {
            p.high();
        } else {
            System.err.println("PIN is not a valid digital output pin");
        }
    }

    private void pulsePin(GpioPinDigitalOutput p, long duration, boolean blocking) {
        if(p != null) {
            p.pulse(duration, blocking);
        } else {
            System.err.println("PIN is not a valid digital output pin");
        }
    }

    public void pinLow(String pinId) {
        pinLow(getPin(pinId));
    }

    public void pinHigh(String pinId) {
        pinHigh(getPin(pinId));
    }

    public void pulsePin(String pinId, long duration, boolean blocking) {
        pulsePin(getPin(pinId), duration, blocking);
    }

    public void runExample() throws InterruptedException {

        System.out.println("<--Pi4J--> GPIO Control Example ... started.");

        // provision gpio pin #01 as an output pin and turn on
        final GpioPinDigitalOutput pin = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_01, "MyLED", PinState.HIGH);

        // set shutdown state for this pin
        pin.setShutdownOptions(true, PinState.LOW);

        System.out.println("--> GPIO state should be: ON");

        Thread.sleep(5000);

        // turn off gpio pin #01
        pin.low();
        System.out.println("--> GPIO state should be: OFF");

        Thread.sleep(5000);

        // toggle the current state of gpio pin #01 (should turn on)
        pin.toggle();
        System.out.println("--> GPIO state should be: ON");

        Thread.sleep(5000);

        // toggle the current state of gpio pin #01  (should turn off)
        pin.toggle();
        System.out.println("--> GPIO state should be: OFF");

        Thread.sleep(5000);

        // turn on gpio pin #01 for 1 second and then off
        System.out.println("--> GPIO state should be: ON for only 1 second");
        pin.pulse(1000, true); // set second argument to 'true' use a blocking call
    }
}
