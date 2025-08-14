package io.github.lama06.schneckenhaus.shell.chest;

import io.github.lama06.schneckenhaus.shell.sized.SizedShellBuilder;
import io.github.lama06.schneckenhaus.util.WoodType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Locale;

public final class ChestShellBuilder extends SizedShellBuilder implements ChestShellData {
    private WoodType wood;

    @Override
    public ChestShellFactory getFactory() {
        return ChestShellFactory.INSTANCE;
    }

    @Override
    protected int buildDuringTransaction(Object prepared) throws SQLException {
        int id = super.buildDuringTransaction(prepared);

        String sql = """
            INSERT INTO chest_shells(id, wood)
            VALUES (?, ?)
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setString(2, wood.name().toLowerCase(Locale.ROOT));
            statement.executeUpdate();
        }

        return id;
    }

    public WoodType getWood() {
        return wood;
    }

    public void setWood(WoodType wood) {
        this.wood = wood;
    }
}
