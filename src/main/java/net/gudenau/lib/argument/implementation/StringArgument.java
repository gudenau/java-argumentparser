package net.gudenau.lib.argument.implementation;

/**
 * @hidden
 *
 * A simple string argument implementation.
 * */
public final class StringArgument extends AbstractArgument<String>{
    public StringArgument(String defaultValue, boolean required){
        super(defaultValue, required);
    }
    
    @Override
    public String getValue(String argument){
        return argument.trim();
    }
}
