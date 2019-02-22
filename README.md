![PNN Logo](docs/images/pins-n-needles.png?raw=true "PNN Logo")

# Pins and Needles
`Pins and Needles` is a small interpreter and command-runner that allows you to quickly script digital signal flows on the
raspberry pi. In other words, you write lines consisting of simple commands that control pins on the Pi without having
to worry about code boilerplate and environment configuration.

## Project Status: Active development, Alpha stage
I have fully tested this on the Raspberry Pi 3 B+ model but that is all. I am still actively developing it and will be
pushing features quickly. On top of that, I've only tested this on Raspbian OS.

My motivation for this project was to allow me to experiment with controlling more complicated circuits without:
* Manual circuit controls like connecting components to buttons on a breadboard and manually switching them
* Having to deal with all the boilerplate code every time I wanted to connect a circuit to the micro-controller simply
to control pins in some deterministic, repeatable fashion.

So instead of dealing with all that, pins-n-needles allows you to write simple pin-control scripts and quickly
experiment with circuits controlled by your Raspberry Pi. This is a pins-n-needles script I wrote called
`shake-dem-hips` which makes 8 LEDs "dance" that are connected to a Serial-In, Parallel-Out shift register, the `SN74HC595`
(Don't worry about not knowing the commands yet. They are explained below with more examples):

```text
# Initial register setup
# Pull output-enable pin low
L 4 1
# Pull clear pin high
H 0 1
# Init done. Begin data sequence
# Pull data pin high
H 1 1
# Pulse clock 4 times to shift in 00001111
P 2 1
P 2 1
P 2 1
P 2 1
# Latch sequence
P 3 1
# Pull data pin low
L 1 1
# Pulse clock 4 more times to shift in 11110000
P 2 1
P 2 1
P 2 1
P 2 1
# Sleep
S 200
# Latch 11110000
P 3 1
# Sleep
S 200
```
This script will light up 4 LEDs on the right side, then the left 4 LEDs. If you loop this sequence (explained later) it makes
it look like the row of LEDs is shaking it's hips. I'm only partially insane I swear...

## Explanation of the commands
In the `shake-dem-hips` script we see several different lines each starting with either a letter or `#`. If you're 
familiar with programming you probably guessed that any line starting with `#` is a comment (and it is!). These are very
useful since the command syntax is so basic. Just like assembly language, the comments are essential keep your scripts
readable and maintainable over time.

Here's a breakdown of the commands starting with a letter (these actually control pins and execution):
* `H <pin> <sleep_duration>`: This command tells pins-n-needles to pull pin `<pin>` __HIGH__ and then wait `<sleep_duration>`
milliseconds before executing the next command
* `L <pin> <sleep_duration>`: This command tells pins-n-needles to pull pin `<pin>` __LOW__ and then wait `<sleep_duration>`
milliseconds before executing the next command
* `P <pin> <pulse_duration>`: This command tells pins-n-needles to __PULSE__ pin `<pin>`, waiting `<pulse_duration>`
milliseconds in between the state transition. For example, if the pin you pulsing is currently LOW, pulse will set the
state to HIGH, wait `<pulse_duration>` milliseconds then set it back to LOW.
* `S <sleep_duration>`: This command simply halts program execution for `<sleep_duration>` milliseconds. This is useful
if you want to hold a current pin state for some amount of time before continuing execution (or before looping).

## How to run Pins N Needles
Before we get into a simple example, you should know how to package and run it. Remember this is in very early stages
so right now, you need to package it yourself but it's easy thanks to Maven!

* Clone this repo to your machine
* Install [Maven](https://maven.apache.org/) if you don't have it. On mac it's just `brew install maven`
* Run `mvn package`
* SCP the `jar` in `target` folder to your raspberry pi. The name will be `pinsnneedles-<VERSION>.jar`
* Example scp command: `scp target/pinsnneedles-1.0.jar pi@<IP_ADDRESS>:~`. The SCP command will push the jar to the 
home directory of the `pi` user. Obviously if your RPi setup is different just modify what you need to!

Now that the jar is built and shipped to the RPi, read on below to run your first example!

## A very simple script example
Instead of a full-blown shift register script, let's pull back a bit and build a very simple script that pulses a pin
a few times. Keep in mind that the script does not care what your circuit looks like, it just activates pins the way
you tell it to.

Let's say we have a very simple LED circuit on a breadboard. Here's an example:

![Simple LED](docs/images/simple_led.png?raw=true "Simple LED")

Pin 1 on the raspberry pi (we are using the [Wiring Pi](http://wiringpi.com/) wiring scheme) is connected to the cathode
of our LED, followed by a 220ohm resistor then to ground.

In order to make the LED flash SSH to your Pi and create a new file called `flashing-lights.txt` and copy this into it:
```text
H 1 1000
L 1 0
```
This will set the state for pin 1 to HIGH (turning on the LED), wait 1 second, then set the pin state to LOW (without a pause).

To run it: `java -jar pinsinneedles-<VERSION>.jar --cmdFile /path/to/flashing-lights.txt --loopCount 0`
This will run the script file 1 time.

Simple right? We could make this even more concise by just using the pulse command:
```text
P 1 1000
```
This will enable the LED, wait 1 second, then set the pin state back to LOW.

## Looping your script file
What if I wanted my LED to blink many times? Sure I could just copy-paste the pulse command 100's or thousands of times
but that's tedious and very hard to maintain. Instead, we can just set the `loopCount` argument when we run our script!

Let's flash that LED 1000 times without modifying the script file:
```bash
java -jar pinsinneedles-<VERSION>.jar --cmdFile /path/to/flashing-lights.txt --loopCount 1000
```
That's it! Things get really interesting when you have more complicated scripts (like the `shake-dem-hips` script above).

## What pin numbers do I use?
When you pass commands such as `H 2 1000` to set the #2 pin to high, that is taken straight from the WiringPi pinout for
Raspberry Pi 3 B+ found [here](http://pi4j.com/pins/model-3b-plus-rev1.html). The commands should use numbers 1-31.

## What's next?

* Improve command parsing code (it's ugly and tough to extend and test in it's current implementation)
* Support for pin numbers instead of just ID's
* (Maybe) Support for variables in scripts so you could assign pin numbers to human-friendly names (like `SRCLK=2`)
* Anything else people might think of.
