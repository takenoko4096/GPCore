package com.gmail.subnokoii78.gpcore.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jspecify.annotations.NullMarked;

import java.util.Set;

@NullMarked
public abstract class AbstractCommand {
    protected AbstractCommand() {}

    protected abstract LiteralCommandNode<CommandSourceStack> getCommandNode();

    protected abstract String getDescription();

    protected Set<String> getAliases() {
        return Set.of();
    }

    public void register(Commands registrar) {
        registrar.register(getCommandNode(), getDescription(), getAliases());
    }

    protected int failure(CommandSourceStack stack, Throwable cause) {
        final TextComponent.Builder component = Component.text("コマンドの実行に失敗しました: ").color(NamedTextColor.RED)
            .toBuilder()
            .appendNewline()
            .append(Component.text("    "))
            .append(Component.text(
                cause.getMessage() == null
                    ? cause.getClass().getSimpleName()
                    : cause.getMessage()
            ).color(NamedTextColor.RED));

        stack.getSender().sendMessage(component);
        return 0;
    }
}
