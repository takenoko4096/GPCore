package com.gmail.subnokoii78.gpcore;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.JarLibrary;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

import java.nio.file.Path;
import java.util.Set;

public abstract class AbstractPluginLoader implements PluginLoader {
    public abstract Set<String> getDependencies();

    public abstract Set<Path> getJarLibraries();

    @Override
    public final void classloader(PluginClasspathBuilder classpathBuilder) {
        final MavenLibraryResolver resolver = new MavenLibraryResolver();

        resolver.addRepository(new RemoteRepository.Builder(
            "central",
            "default",
            MavenLibraryResolver.MAVEN_CENTRAL_DEFAULT_MIRROR
        ).build());

        resolver.addRepository(new RemoteRepository.Builder(
            "paper",
            "default",
            "https://repo.papermc.io/repository/maven-public/"
        ).build());

        resolver.addRepository(new RemoteRepository.Builder(
            "minecraft",
            "default",
            "https://libraries.minecraft.net/"
        ).build());

        for (final String library : getDependencies()) {
            resolver.addDependency(new Dependency(
                new DefaultArtifact(library),
                null
            ));
        }

        for (final Path path : getJarLibraries()) {
            classpathBuilder.addLibrary(new JarLibrary(path));
        }

        classpathBuilder.addLibrary(resolver);
    }
}
