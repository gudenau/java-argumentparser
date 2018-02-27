package net.gudenau.lib.argument.implementation;

import net.gudenau.lib.argument.Argument;

/**
 * @hidden
 *
 * A small helper class for implementing object based
 * arguments.
 *
 * @param <T> The object this represents
 * */
abstract class AbstractArgument<T> implements Argument<T>{
    private final T defaultValue;
    private final boolean required;
    
    AbstractArgument(T defaultValue, boolean required){
        this.defaultValue = defaultValue;
        this.required = required;
    }
    
    @Override
    public final boolean isRequired(){
        return required;
    }
    
    @Override
    public final T getDefault(){
        return defaultValue;
    }
    
    @Override
    public final boolean hasDefault(){
        return defaultValue != null;
    }
}
