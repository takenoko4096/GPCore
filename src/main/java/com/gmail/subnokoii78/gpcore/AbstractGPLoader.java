package com.gmail.subnokoii78.gpcore;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

import java.util.Set;

public abstract class AbstractGPLoader implements PluginLoader {
    public Set<String> getMavenDependencies() {
        return Set.of(/*"net.dmulloy2:ProtocolLib:5.4.0"*/);
    }

    @Override
    public final void classloader(PluginClasspathBuilder classpathBuilder) {
        final MavenLibraryResolver resolver = new MavenLibraryResolver();
        resolver.addRepository(new RemoteRepository.Builder(
            "central",
            "default",
            MavenLibraryResolver.MAVEN_CENTRAL_DEFAULT_MIRROR
        ).build());

        for (final String library : getMavenDependencies()) {
            resolver.addDependency(new Dependency(
                new DefaultArtifact(library),
                null
            ));
        }

        classpathBuilder.addLibrary(resolver);
    }
}
