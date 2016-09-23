#!/usr/local/bin/python

import RPi.GPIO as GPIO, time

GPIO.setmode(GPIO.BCM)

# Checks if given pin is receiving a high signal
def isHigh(pin):
    GPIO.setup(pin, GPIO.IN)
    time.sleep(0.1)
    if(GPIO.input(pin) == GPIO.HIGH):
        return True
    return False

# Sets the given pin according to the flag
def setPin(pin, flag):
    GPIO.setup(pin, GPIO.OUT)
    if(flag):
        GPIO.output(pin, GPIO.HIGH)
    else:
        GPIO.output(pin, GPIO.LOW)

# Main program loop
while True:
    setPin(2, not isHigh(4))
    time.sleep(0.4)
