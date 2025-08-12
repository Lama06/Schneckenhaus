package io.github.lama06.schneckenhaus.shell.sized;

import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.shell.ShellInformation;
import io.github.lama06.schneckenhaus.shell.builtin.BuiltinShell;
import io.github.lama06.schneckenhaus.util.BlockArea;
import net.kyori.adventure.text.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class SizedShell extends BuiltinShell implements SizedShellData {
    protected SizedShell(int id) {
        super(id);
    }

    private int size;

    @Override
    protected boolean load() {
        if (!super.load()) {
            return false;
        }

        String sql = """
            SELECT size
            FROM sized_shells
            WHERE id = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            if (!result.next()) {
                return false;
            }

            size = result.getInt("size");
            return true;
        } catch (SQLException e) {
            logger.error("failed to load shell: {}", id, e);
            return false;
        }
    }

    @Override
    public final BlockArea getFloor() {
        return new BlockArea(
            position.getCornerBlock().getRelative(1, 0, 1),
            position.getCornerBlock().getRelative(getSize(), 0, getSize())
        );
    }

    @Override
    public BlockArea getArea() {
        return new BlockArea(
            position.getCornerBlock(), position.getCornerBlock().getRelative(size - 1, size - 1, size - 1)
        );
    }

    @Override
    protected void addInformation(List<ShellInformation> information) {
        super.addInformation(information);
        information.add(new ShellInformation(Message.SIZE.toComponent(), Component.text(size)));
    }

    @Override
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
        String sql = """
            UPDATE sized_shells
            SET size = ?
            WHERE id = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, size);
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("failed to update shell size: {}", id, e);
        }
    }
}
