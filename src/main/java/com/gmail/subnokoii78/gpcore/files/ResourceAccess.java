package com.gmail.subnokoii78.gpcore.files;

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

    public void copy(Path destination) {
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
                            Files.copy(origin, target, StandardCopyOption.REPLACE_EXISTING);
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
}
