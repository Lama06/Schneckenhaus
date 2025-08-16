package io.github.lama06.schneckenhaus.command.select;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

enum CombinationMode {
    REPLACE {
        @Override
        List<Integer> combine(List<Integer> oldSelection, List<Integer> newSelection) {
            return newSelection;
        }
    },
    APPEND {
        @Override
        List<Integer> combine(List<Integer> oldSelection, List<Integer> newSelection) {
            List<Integer> selection = new ArrayList<>(oldSelection);
            selection.addAll(newSelection);
            return selection;
        }
    },
    REMOVE {
        @Override
        List<Integer> combine(List<Integer> oldSelection, List<Integer> newSelection) {
            List<Integer> selection = new ArrayList<>(oldSelection);
            selection.removeAll(newSelection);
            return selection;
        }
    },
    INTERSECT {
        @Override
        List<Integer> combine(List<Integer> oldSelection, List<Integer> newSelection) {
            Set<Integer> newSelectionSet = new HashSet<>(newSelection);
            List<Integer> selection = new ArrayList<>();
            for (Integer id : oldSelection) {
                if (newSelectionSet.contains(id)) {
                    selection.add(id);
                }
            }
            return selection;
        }
    };

    abstract List<Integer> combine(List<Integer> oldSelection, List<Integer> newSelection);
}
