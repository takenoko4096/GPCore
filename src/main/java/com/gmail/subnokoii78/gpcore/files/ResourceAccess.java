package com.gmail.subnokoii78.gpcore.files;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

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
    private final Path path;

    public ResourceAccess(String name) {
        this.path = getSourcePath(name);
    }

    public Path getPath() {
        return path;
    }

    public URI getUri() {
        return path.toUri();
    }

    private Path getSourcePath(String resource) {
        final URL url = ResourceAccess.class.getResource('/' + resource);

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

        final Path sourcePath;
        if (uri.getScheme().equals("jar")) {
            try (final FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
                sourcePath = fs.getPath('/' + resource);
            }
            catch (IOException e) {
                throw new RuntimeException();
            }
        }
        else {
            sourcePath = Paths.get(uri);
        }

        return sourcePath;
    }

    public void copy(Path destination) {
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
            stream.forEach(path -> {
                try {
                    final Path relative = this.path.relativize(path);
                    final Path target = destination.resolve(relative.toString());

                    if (Files.isDirectory(path)) {
                        Files.createDirectories(target);
                    }
                    else {
                        Files.createDirectories(target.getParent());
                        Files.copy(path, target, StandardCopyOption.REPLACE_EXISTING);
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
    }
}
