package io.github.lama06.schneckenhaus.player;

public final class ShellTeleportOptions {
    private boolean playSound = true;
    private boolean storePreviousPositionWhenNesting = true;
    private boolean checkGeneralEnterPermission = true;

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

    public boolean isCheckGeneralEnterPermission() {
        return checkGeneralEnterPermission;
    }

    public void setCheckGeneralEnterPermission(boolean checkGeneralEnterPermission) {
        this.checkGeneralEnterPermission = checkGeneralEnterPermission;
    }
}
