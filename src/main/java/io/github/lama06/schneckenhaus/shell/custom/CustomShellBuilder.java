package io.github.lama06.schneckenhaus.shell.custom;

import io.github.lama06.schneckenhaus.shell.ShellBuilder;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class CustomShellBuilder extends ShellBuilder implements CustomShellData {
    private String template;

    @Override
    public CustomShellFactory getFactory() {
        return CustomShellFactory.INSTANCE;
    }

    @Override
    protected int buildDuringTransaction() throws SQLException {
        int id = super.buildDuringTransaction();
        String sql = """
            INSERT INTO custom_shells(id, template)
            VALUES(?, ?)
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setString(2, template);
            statement.executeUpdate();
        }
        return id;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}
