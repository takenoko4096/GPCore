package com.gmail.subnokoii78.gpcore.database;

import com.gmail.takenokoii78.mojangson.MojangsonParser;
import com.gmail.takenokoii78.mojangson.MojangsonSerializer;
import com.gmail.takenokoii78.mojangson.values.MojangsonCompound;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.CommandStorage;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public final class CommandStorageAccess {
    private final Identifier identifier;

    private final MojangsonCompound compound;

    private CommandStorageAccess(String location) {
        final Identifier identifier = Identifier.tryParse(location);

        if (identifier == null) {
            throw new IllegalArgumentException("IDのパースに失敗しました: ' " + location + "'");
        }

        this.identifier = identifier;
        this.compound = MojangsonParser.compound(getCommandStorage().get(identifier).toString());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identifier);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CommandStorageAccess that = (CommandStorageAccess) o;
        return Objects.equals(identifier, that.identifier);
    }

    public boolean save() {
        try {
            getCommandStorage().set(identifier, TagParser.parseCompoundFully(MojangsonSerializer.serialize(compound)));
            return true;
        }
        catch (CommandSyntaxException e) {
            return false;
        }
    }

    public MojangsonCompound get() {
        return compound;
    }

    private CommandStorage getCommandStorage() {
        return MinecraftServer.getServer().getCommandStorage();
    }

    public static CommandStorageAccess load(String identifier) {
        return new CommandStorageAccess(identifier);
    }
}
