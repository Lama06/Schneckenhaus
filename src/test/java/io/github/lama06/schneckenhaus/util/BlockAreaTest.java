package io.github.lama06.schneckenhaus.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class BlockAreaTest {
    public record IteratorTestCase(BlockArea area, List<BlockPosition> blocks) { }

    public static List<IteratorTestCase> testIterator() {
        return List.of(
                new IteratorTestCase(
                        new BlockArea(new BlockPosition(1, 1, 1), new BlockPosition(1, 1, 1)),
                        List.of(new BlockPosition(1, 1, 1))
                ),
                new IteratorTestCase(
                        new BlockArea(new BlockPosition(1, 2, 1), new BlockPosition(1, 1, 1)),
                        List.of(new BlockPosition(1, 1, 1), new BlockPosition(1, 2, 1))
                ),
                new IteratorTestCase(
                        new BlockArea(new BlockPosition(2, 2, 2), new BlockPosition(1, 1, 1)),
                        List.of(
                                new BlockPosition(1, 1, 1),
                                new BlockPosition(2, 1, 1),
                                new BlockPosition(1, 1, 2),
                                new BlockPosition(2, 1, 2),
                                new BlockPosition(1, 2, 1),
                                new BlockPosition(2, 2, 1),
                                new BlockPosition(1, 2, 2),
                                new BlockPosition(2, 2, 2)
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource
    public void testIterator(final IteratorTestCase test) {
        final List<BlockPosition> blocks = new ArrayList<>();
        for (final BlockPosition position : test.area()) {
            blocks.add(position);
        }
        assertEquals(test.blocks(), blocks);
    }
}
