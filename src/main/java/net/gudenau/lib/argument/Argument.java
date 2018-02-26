package net.gudenau.lib.argument;

public interface Argument<T>{
    boolean isRequired();
    T getValue(String argument);
    T getDefault();
    boolean hasDefault();
}
