package com.mojang.sdk;

public class Input {
    public enum Button {
        LEFT,
        RIGHT,
        FIRE
    }

    /**
     * Check whether a button is currently pressed.
     */
    public static boolean isButtonPressed(Button button) {
        switch (button) {
            case LEFT:
                return left;
            case RIGHT:
                return right;
            case FIRE:
                return fire;
        }
        return false;
    }

    static boolean left = false;
    static boolean right = false;
    static boolean fire = false;
}
