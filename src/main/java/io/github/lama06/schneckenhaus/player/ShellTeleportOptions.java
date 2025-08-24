package io.github.lama06.schneckenhaus.player;

import io.github.lama06.schneckenhaus.shell.ShellPlacement;

public final class ShellTeleportOptions {
    private boolean playSound = true;
    private boolean storePreviousPositionWhenNesting = true;
    private boolean checkGeneralEnterPermission = true;
    private ShellPlacement placementUsed;

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

    public ShellPlacement getPlacementUsed() {
        return placementUsed;
    }

    public void setPlacementUsed(ShellPlacement placementUsed) {
        this.placementUsed = placementUsed;
    }
}
