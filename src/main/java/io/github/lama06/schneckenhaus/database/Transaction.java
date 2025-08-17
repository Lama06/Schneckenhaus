package io.github.lama06.schneckenhaus.database;

@FunctionalInterface
public interface Transaction<R> {
    R run() throws Exception;
}
