package com.gmail.subnokoii78.gpcore;

import com.gmail.subnokoii78.gpcore.entity.FakeArrowLauncher;
import com.gmail.subnokoii78.gpcore.events.BukkitEventObserver;
import com.gmail.subnokoii78.gpcore.events.Events;
import com.gmail.subnokoii78.gpcore.files.PluginConfigLoader;
import com.gmail.subnokoii78.gpcore.scoreboard.Scoreboard;
import com.gmail.subnokoii78.gpcore.ui.container.ContainerInteraction;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.raid.RaidSpawnWaveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class GPCore {
    private GPCore() {}

    @Nullable
    private static Plugin plugin;

    @Nullable
    private static PluginBootstrap bootstrap;

    @Nullable
    public static PluginConfigLoader pluginConfigLoader;

    @Nullable
    private static Scoreboard scoreboard;

    public static final Events events = new Events();

    public static Plugin getPlugin() throws IllegalStateException {
        if (plugin == null) {
            throw new IllegalStateException("プラグインのインスタンスが用意されていません");
        }
        else {
            return plugin;
        }
    }

    public static PluginBootstrap getPluginBootstrap() throws IllegalStateException {
        if (bootstrap == null) {
            throw new IllegalStateException("ブートストラップのインスタンスが用意されていません");
        }
        else {
            return bootstrap;
        }
    }

    public static PluginConfigLoader getPluginConfigLoader() throws IllegalStateException {
        if (pluginConfigLoader == null) {
            throw new IllegalStateException("プラグインコンフィグローダーのインスタンスが用意されていません");
        }
        else {
            return pluginConfigLoader;
        }
    }

    public static Scoreboard getScoreboard() throws IllegalStateException {
        if (scoreboard == null) {
            throw new IllegalStateException("スコアボードのインスタンスが用意されていません");
        }
        else {
            return scoreboard;
        }
    }

    public static void initialize(Plugin plugin, @Nullable PluginBootstrap pluginBootstrap, String configPath, String defaultConfigPath) throws IllegalStateException {
        if (GPCore.plugin == null) {
            if (plugin.getDataFolder().exists()) {
                plugin.getComponentLogger().info(Component.text("データフォルダが既に存在するため、作成をスキップしました"));
            }
            else {
                if (!plugin.getDataFolder().mkdir()) throw new IllegalStateException("データフォルダの作成に失敗しました; 致命的な例外のためプラグインは停止されます");
            }

            GPCore.plugin = plugin;
            GPCore.bootstrap = pluginBootstrap;
            GPCore.pluginConfigLoader = new PluginConfigLoader(configPath, defaultConfigPath);
            GPCore.scoreboard = new Scoreboard(Bukkit.getScoreboardManager().getMainScoreboard());

            final PluginManager manager = Bukkit.getPluginManager();
            manager.registerEvents(BukkitEventObserver.INSTANCE, plugin);
            manager.registerEvents(ContainerInteraction.ContainerEventObserver.INSTANCE, plugin);
            manager.registerEvents(FakeArrowLauncher.FakeArrowEventListener.INSTANCE, plugin);
            FakeArrowLauncher.FakeArrowEventListener.INSTANCE.runTaskTimer(plugin, 0L, 1L);

            plugin.getComponentLogger().info(Component.text("GPCore が起動しました"));
        }
        else {
            throw new IllegalStateException("プラグインのインスタンスが既に登録されています");
        }
    }
}
