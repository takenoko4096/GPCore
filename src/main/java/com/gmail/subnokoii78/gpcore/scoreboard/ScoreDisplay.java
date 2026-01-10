package com.gmail.subnokoii78.gpcore.scoreboard;

import org.bukkit.scoreboard.DisplaySlot;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;

@NullMarked
public final class ScoreDisplay {
    private final DisplaySlot slot;

    ScoreDisplay(DisplaySlot slot) {
        this.slot = slot;
    }

    @Override
    public int hashCode() {
        return Objects.hash(slot);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScoreDisplay that = (ScoreDisplay) o;
        return slot == that.slot;
    }

    public boolean hasObjective() {
        return !Scoreboard.getScoreboards().stream().allMatch(scoreboard -> scoreboard.getObjectives()
            .stream()
            .filter(objective -> objective.toBukkit().getDisplaySlot() == slot)
            .toList().isEmpty()
        );
    }

    public ScoreObjective getObjective() throws IllegalStateException {
        final List<ScoreObjective> objectives = Scoreboard.getScoreboards()
            .stream()
            .flatMap(scoreboard -> scoreboard.getObjectives()
                .stream()
                .filter(objective -> objective.toBukkit().getDisplaySlot() == slot))
            .toList();

        if (objectives.size() > 1) {
            throw new IllegalStateException("NEVER HAPPENS");
        }
        else if (objectives.size() == 1) {
            return objectives.getFirst();
        }
        else {
            throw new IllegalStateException("ディスプレイスロット '" + slot.getId() + "' にはオブジェクティブが表示されていません");
        }
    }

    public void setObjective(ScoreObjective objective) {
        objective.toBukkit().setDisplaySlot(slot);
    }

    public void clearObjective() {
        if (hasObjective()) {
            getObjective().toBukkit().setDisplaySlot(null);
        }
    }
}
