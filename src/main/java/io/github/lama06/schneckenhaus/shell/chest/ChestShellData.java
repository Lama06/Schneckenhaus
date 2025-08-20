package io.github.lama06.schneckenhaus.shell.chest;

import io.github.lama06.schneckenhaus.shell.sized.SizedShellData;
import io.github.lama06.schneckenhaus.util.WoodType;

public interface ChestShellData extends SizedShellData {
    WoodType getWood();
}
