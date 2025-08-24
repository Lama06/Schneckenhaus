package io.github.lama06.schneckenhaus.database;

@FunctionalInterface
public interface FailableRunnable<X extends Throwable> {
    void run() throws X;
}
