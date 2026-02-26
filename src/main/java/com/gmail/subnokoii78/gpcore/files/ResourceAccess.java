package com.gmail.subnokoii78.gpcore.files;

import com.gmail.subnokoii78.gpcore.events.EventType;
import com.gmail.subnokoii78.gpcore.events.Events;
import com.gmail.subnokoii78.gpcore.events.IEvent;
import org.jspecify.annotations.NullMarked;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.stream.Stream;

@NullMarked
public class ResourceAccess {
    private final String name;

    public ResourceAccess(String name) {
        this.name = name;
    }

    public void useAccess(Consumer<Path> consumer) {
        final URL url = ResourceAccess.class.getResource('/' + name);

        if (url == null) {
            throw new RuntimeException("url is null");
        }

        final URI uri;
        try {
            uri = url.toURI();
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        final Path path;
        if (uri.getScheme().equals("jar")) {
            try (final FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
                path = fs.getPath('/' + name);
                consumer.accept(path);
            }
            catch (IOException e) {
                throw new RuntimeException();
            }
        }
        else {
            path = Paths.get(uri);
            consumer.accept(path);
        }
    }

    public void copy(Path destination, Consumer<Events> eventHandler) {
        useAccess(path -> {
            if (Files.isRegularFile(path)) {
                try {
                    Files.createDirectories(destination.getParent());
                    Files.copy(path, destination, StandardCopyOption.REPLACE_EXISTING);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            try (final Stream<Path> stream = Files.walk(path)) {
                stream.forEach(origin -> {
                    try {
                        final Path relative = path.relativize(origin);
                        final Path target = destination.resolve(relative.toString());

                        if (Files.isDirectory(origin)) {
                            Files.createDirectories(target);
                        }
                        else {
                            Files.createDirectories(target.getParent());

                            final Events events = new Events();
                            eventHandler.accept(events);
                            final ResourceCopyBeforeEvent beforeEvent = new ResourceCopyBeforeEvent(origin, target);
                            events.getDispatcher(RESOURCE_COPY_BEFORE).dispatch(beforeEvent);
                            if (!beforeEvent.ignored) {
                                Files.copy(origin, target, StandardCopyOption.REPLACE_EXISTING);
                                events.getDispatcher(RESOURCE_COPY_AFTER).dispatch(new ResourceCopyAfterEvent(origin, target));
                            }
                        }
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static abstract class AbstractResourceCopyEvent implements IEvent {
        protected final Path from;

        protected final Path to;

        protected AbstractResourceCopyEvent(Path from, Path to) {
            this.from = from;
            this.to = to;
        }

        public Path getFrom() {
            return from;
        }

        public Path getTo() {
            return to;
        }
    }

    public static final class ResourceCopyBeforeEvent extends AbstractResourceCopyEvent {
        private boolean ignored;

        private ResourceCopyBeforeEvent(Path from, Path to) {
            super(from, to);
        }

        @Override
        public EventType<? extends IEvent> getType() {
            return RESOURCE_COPY_BEFORE;
        }

        public void ignore() {
            ignored = true;
        }

        public void unignore() {
            ignored = false;
        }
    }

    public static final class ResourceCopyAfterEvent extends AbstractResourceCopyEvent {
        private ResourceCopyAfterEvent(Path from, Path to) {
            super(from, to);
        }

        @Override
        public EventType<? extends IEvent> getType() {
            return RESOURCE_COPY_AFTER;
        }
    }

    public static final EventType<ResourceCopyBeforeEvent> RESOURCE_COPY_BEFORE = new EventType<>(ResourceCopyBeforeEvent.class);

    public static final EventType<ResourceCopyAfterEvent> RESOURCE_COPY_AFTER = new EventType<>(ResourceCopyAfterEvent.class);
}
