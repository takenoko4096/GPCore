package com.gmail.subnokoii78.gpcore.events;

public final class EventTypes {
    private EventTypes() {}

    public static final EventType<PlayerClickEvent> PLAYER_CLICK = new EventType<>(PlayerClickEvent.class);

    public static final EventType<TickEvent> TICK = new EventType<>(TickEvent.class);

    public static final EventType<PluginConfigUpdateEvent> PLUGIN_CONFIG_UPDATE = new EventType<>(PluginConfigUpdateEvent.class);

    public static final EventType<PlayerBowShootEvent> PLAYER_BOW_SHOOT = new EventType<>(PlayerBowShootEvent.class);

    public static final EventType<PlayerUsingItemEvent> PLAYER_USING_ITEM = new EventType<>(PlayerUsingItemEvent.class);
}
