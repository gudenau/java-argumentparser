package net.gudenau.lib.argument;

/**
 * The interface that is used in argument implementations.
 *
 * @param <T> The object this param represents
 * */
public interface Argument<T>{
    /**
     * Checks if this argument is required.
     *
     * @return Is the argument required?
     * */
    boolean isRequired();
    
    /**
     * Gets the value from the provided argument.
     *
     * @param argument The string that was provided
     *
     * @return The instance of the object that was passed
     * */
    T getValue(String argument);
    
    /**
     * Gets the default value if none was supplied.
     *
     * @return The default value
     * */
    T getDefault();
    
    /**
     * Checks if this has a default value, since null could
     * be a real default.
     *
     * @return Is there a default?
     * */
    boolean hasDefault();
}
