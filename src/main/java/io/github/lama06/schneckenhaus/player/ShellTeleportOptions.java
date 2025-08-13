package io.github.lama06.schneckenhaus.player;

public class ShellTeleportOptions {
    private boolean playSound = true;
    private boolean storePreviousPositionWhenNesting = true;

    public void setPlaySound(boolean playSound) {
        this.playSound = playSound;
    }

    public boolean isPlaySound() {
        return playSound;
    }

    public void setStorePreviousPositionWhenNesting(boolean storePreviousPositionWhenNesting) {
        this.storePreviousPositionWhenNesting = storePreviousPositionWhenNesting;
    }

    public boolean isStorePreviousPositionWhenNesting() {
        return storePreviousPositionWhenNesting;
    }
}
