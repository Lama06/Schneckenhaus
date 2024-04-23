package io.github.lama06.schneckenhaus.position;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public final class GridPositionTest {
    private record TestPosition(int id, int x, int z) {}

    private static final List<TestPosition> TEST_POSITIONS = List.of(
            new TestPosition(1, 0, 0),
            new TestPosition(2, 1, 0),
            new TestPosition(3, 1, 1),
            new TestPosition(4, 0, 1),
            new TestPosition(5, 2, 0),
            new TestPosition(6, 2, 1),
            new TestPosition(7, 2, 2),
            new TestPosition(8, 1, 2),
            new TestPosition(9, 0, 2),
            new TestPosition(10, 3, 0),
            new TestPosition(11, 3, 1),
            new TestPosition(12, 3, 2),
            new TestPosition(13, 3, 3),
            new TestPosition(14, 2, 3),
            new TestPosition(15, 1, 3),
            new TestPosition(16, 0, 3)
    );

    @Test
    public void testCoordinatesToId() {
        for (final TestPosition testPosition : TEST_POSITIONS) {
            final CoordinatesGridPosition position = new CoordinatesGridPosition(testPosition.x(), testPosition.z());
            assertEquals(testPosition.id(), position.getId());
        }
    }

    @Test
    public void testIdToCoordinates() {
        for (final TestPosition testPosition : TEST_POSITIONS) {
            final IdGridPosition position = new IdGridPosition(testPosition.id());
            assertEquals(testPosition.x(), position.getX());
            assertEquals(testPosition.z(), position.getZ());
        }
    }
}
