package com.gmail.subnokoii78.gpcore.scoreboard;

import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public final class ScoreObjective {
    private final Scoreboard scoreboard;

    private final Objective bukkit;

    ScoreObjective(Scoreboard scoreboard, Objective objective) {
        this.scoreboard = scoreboard;
        this.bukkit = objective;
    }

    @Override
    public int hashCode() {
        return Objects.hash(scoreboard, bukkit);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        else if (!(obj instanceof ScoreObjective)) return false;
        else return hashCode() == obj.hashCode();
    }

    public boolean hasScore(Entity entity) {
        return bukkit.getScoreFor(entity).isScoreSet();
    }

    public boolean hasScore(String name) {
        return bukkit.getScore(name).isScoreSet();
    }

    public int getScore(Entity entity) {
        return bukkit.getScoreFor(entity).getScore();
    }

    public int getScore(String name) {
        return bukkit.getScore(name).getScore();
    }

    public ScoreObjective setScore(Entity entity, int value) {
        bukkit.getScoreFor(entity).setScore(value);
        return this;
    }

    public ScoreObjective setScore(String name, int value) {
        bukkit.getScore(name).setScore(value);
        return this;
    }

    public ScoreObjective addScore(Entity entity, int value) {
        setScore(entity, getScore(entity) + value);
        return this;
    }

    public ScoreObjective addScore(String name, int value) {
        setScore(name, getScore(name) + value);
        return this;
    }

    public ScoreObjective subtractScore(Entity entity, int value) {
        setScore(entity, getScore(entity) - value);
        return this;
    }

    public ScoreObjective subtractScore(String name, int value) {
        setScore(name, getScore(name) - value);
        return this;
    }

    public ScoreObjective multiplyScore(Entity entity, int value) {
        setScore(entity, getScore(entity) * value);
        return this;
    }

    public ScoreObjective multiplyScore(String name, int value) {
        final int subtrahend = getScore(name) * value;
        setScore(name, subtrahend);
        return this;
    }

    public ScoreObjective divideScore(Entity entity, int value) {
        if (value == 0) return this;
        setScore(entity, getScore(entity) / value);
        return this;
    }

    public ScoreObjective divideScore(String name, int value) {
        if (value == 0) return this;
        setScore(name, getScore(name) / value);
        return this;
    }

    public ScoreObjective resetScore(Entity entity) {
        bukkit.getScoreFor(entity).resetScore();
        return this;
    }

    public ScoreObjective resetScore(String name) {
        bukkit.getScore(name).resetScore();
        return this;
    }

    public String getName() {
        return bukkit.getName();
    }

    public Criteria getCriteria() {
        return bukkit.getTrackedCriteria();
    }

    public Component getDisplayName() {
        return bukkit.displayName();
    }

    public void setDisplayName(Component displayName) {
        bukkit.displayName(displayName);
    }

    public boolean isDisplayed() {
        return bukkit.getDisplaySlot() != null;
    }

    public boolean isDisplayedOn(ScoreDisplay display) {
        return display.getObjective().equals(this);
    }

    public boolean hasNumberFormat() {
        return bukkit.numberFormat() != null;
    }

    public NumberFormat getNumberFormat() {
        return Objects.requireNonNull(bukkit.numberFormat(), "オブジェクティブ '" + bukkit.getName() + "' は数値の書式を持っていません");
    }

    public void setNumberFormat(NumberFormat format) {
        bukkit.numberFormat(format);
    }

    public void resetNumberFormat() {
        bukkit.numberFormat(null);
    }

    Objective toBukkit() {
        return bukkit;
    }
}
