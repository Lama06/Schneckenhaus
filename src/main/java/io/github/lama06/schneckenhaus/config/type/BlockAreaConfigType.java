package io.github.lama06.schneckenhaus.config.type;

import io.github.lama06.schneckenhaus.config.ConfigException;
import io.github.lama06.schneckenhaus.util.BlockArea;
import io.github.lama06.schneckenhaus.util.Range;

import java.util.Objects;

public class BlockAreaConfigType implements ConfigType<BlockArea> {
    public static final BlockAreaConfigType INSTANCE = new BlockAreaConfigType(Range.ALL);

    private final Range widthRange;

    public BlockAreaConfigType(final Range widthRange) {
        this.widthRange = Objects.requireNonNull(widthRange);
    }

    @Override
    public BlockArea parse(final Object data) throws ConfigException {
        if (!(data instanceof final String string)) {
            throw new ConfigException("Expected string: " + data);
        }
        final BlockArea area = BlockArea.fromString(string);
        if (area == null) {
            throw new ConfigException("Invalid block area: " + string);
        }
        return area;
    }

    @Override
    public void verify(final BlockArea data) throws ConfigException {
        if (!widthRange.contains(data.getWidthX()) || !widthRange.contains(data.getWidthZ())) {
            throw new ConfigException("The area's width must be " + widthRange);
        }
    }

    @Override
    public Object store(final BlockArea area) {
        return area.toString();
    }
}
