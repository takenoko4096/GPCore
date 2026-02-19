package com.gmail.subnokoii78.gpcore.commands;

import com.gmail.subnokoii78.gpcore.GPCore;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jspecify.annotations.NullMarked;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@NullMarked
public abstract class AbstractCommand {
    protected AbstractCommand() {}

    protected abstract LiteralCommandNode<CommandSourceStack> getCommandNode();

    protected abstract String getDescription();

    protected Set<String> getAliases() {
        return Set.of();
    }

    public final void register(Commands registrar) {
        registrar.register(getCommandNode(), getDescription(), getAliases());
    }

    protected int failure(CommandContext<CommandSourceStack> context, String message) {
        final TextComponent.Builder builder = Component.text("コマンドの実行に失敗しました: ")
            .color(NamedTextColor.RED)
            .toBuilder()
            .appendNewline()
            .append(Component.text("    "))
            .append(Component.text(message).color(NamedTextColor.RED));

        context.getSource().getSender().sendMessage(builder);
        return 0;
    }

    protected int failure(CommandContext<CommandSourceStack> context, Throwable cause) {
        final TextComponent.Builder builder = Component.text("コマンドの実行に失敗しました: ")
            .color(NamedTextColor.RED)
            .toBuilder()
            .appendNewline()
            .append(Component.text("    "))
            .append(Component.text(
                cause.getMessage() == null ? cause.getClass().getSimpleName() : cause.getMessage()
            ).color(NamedTextColor.RED));

        final CommandSender sender = context.getSource().getSender();

        if (sender instanceof ConsoleCommandSender) {
            GPCore.getPlugin().getComponentLogger().trace(Component.text("コマンドの実行で発生した例外: ").color(NamedTextColor.RED), cause);
        }
        else if (sender.isOp()) {
            builder.appendNewline()
                .append(Component.text("    "))
                .append(causeOutputOption(cause));
        }

        sender.sendMessage(builder);
        return 0;
    }

    private Component causeOutputOption(Throwable cause) {
        final AtomicBoolean sent = new AtomicBoolean(false);

        return Component.text("例外をコンソールに出力")
            .color(NamedTextColor.GRAY)
            .decorate(TextDecoration.UNDERLINED)
            .hoverEvent(HoverEvent.showText(Component.text("printStackTrace() を実行します")))
            .clickEvent(ClickEvent.callback(audience -> {
                if (!sent.get()) {
                    sent.set(true);

                    GPCore.getPlugin().getComponentLogger().trace(Component.text("コマンドの実行で発生した例外: ").color(NamedTextColor.RED), cause);

                    audience.sendMessage(
                        Component.text("例外をコンソールに出力しました: ")
                            .color(NamedTextColor.GRAY)
                            .appendNewline()
                            .append(Component.text("    "))
                            .append(Component.text(cause.getClass().getName()))
                    );
                }
            }));
    }

    protected int success(CommandContext<CommandSourceStack> context, String message, int resultValue) {
        context.getSource().getSender().sendMessage(Component.text(message));
        return resultValue == 0 ? 1 : resultValue;
    }
}
