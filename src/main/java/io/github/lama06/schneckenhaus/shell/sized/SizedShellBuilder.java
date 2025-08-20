package io.github.lama06.schneckenhaus.shell.sized;

import io.github.lama06.schneckenhaus.SchneckenhausPlugin;
import io.github.lama06.schneckenhaus.shell.builtin.BuiltinShellBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class SizedShellBuilder extends BuiltinShellBuilder implements SizedShellData {
    private int size;

    @Override
    public abstract SizedShellFactory getFactory();

    @Override
    protected int buildDuringTransaction(Object prepared) throws SQLException {
        int id = super.buildDuringTransaction(prepared);

        size = Math.clamp(size, getFactory().getMinSize(), getFactory().getMaxSize());

        Connection connection = SchneckenhausPlugin.INSTANCE.getDatabaseConnection();
        String sql = """
            INSERT INTO sized_shells(id, size)
            VALUES (?, ?)
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setInt(2, size);
            statement.executeUpdate();
        }
        return id;
    }

    @Override
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
