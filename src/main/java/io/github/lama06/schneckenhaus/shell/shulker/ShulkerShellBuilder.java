package io.github.lama06.schneckenhaus.shell.shulker;

import io.github.lama06.schneckenhaus.shell.sized.SizedShellBuilder;
import org.bukkit.DyeColor;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Set;

public final class ShulkerShellBuilder extends SizedShellBuilder implements ShulkerShellData {
    private DyeColor color;
    private boolean rainbow;
    private Set<DyeColor> rainbowColors;

    @Override
    public ShulkerShellFactory getFactory() {
        return ShulkerShellFactory.INSTANCE;
    }

    @Override
    protected int buildDuringTransaction(Object prepared) throws SQLException {
        int id = super.buildDuringTransaction(prepared);

        String sql = """
            INSERT INTO shulker_shells(id, color, rainbow)
            VALUES (?, ?, ?)
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setString(2, color.name().toLowerCase(Locale.ROOT));
            statement.setBoolean(3, rainbow);
            statement.executeUpdate();
        }

        String colorSql = """
            INSERT INTO shulker_shell_rainbow_colors(id, color, enabled)
            VALUES (?, ?, TRUE)
            """;
        try (PreparedStatement statement = connection.prepareStatement(colorSql)) {
            for (DyeColor color : rainbowColors) {
                statement.setInt(1, id);
                statement.setString(2, color.name().toLowerCase(Locale.ROOT));
                statement.addBatch();
            }
            statement.executeBatch();
        }

        return id;
    }

    @Override
    public DyeColor getColor() {
        return color;
    }

    public void setColor(DyeColor color) {
        this.color = color;
    }

    @Override
    public boolean isRainbow() {
        return rainbow;
    }

    public void setRainbow(boolean rainbow) {
        this.rainbow = rainbow;
    }

    @Override
    public Set<DyeColor> getRainbowColors() {
        return rainbowColors;
    }

    public void setRainbowColors(Set<DyeColor> rainbowColors) {
        this.rainbowColors = rainbowColors;
    }
}
