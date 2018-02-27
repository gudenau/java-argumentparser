package net.gudenau.lib.argument.implementation;

import net.gudenau.lib.argument.Argument;

/**
 * @hidden
 *
 * A simple integer based argument.
 * */
public class IntegerArgument implements Argument<Integer>{
    private final int defaultValue;
    private final boolean required;
    
    public IntegerArgument(int defaultValue, boolean required){
        this.defaultValue = defaultValue;
        this.required = required;
    }
    
    @Override
    public boolean isRequired(){
        return required;
    }
    
    @Override
    public Integer getValue(String argument){
        return Integer.parseInt(argument);
    }
    
    @Override
    public Integer getDefault(){
        return defaultValue;
    }
    
    @Override
    public boolean hasDefault(){
        return defaultValue != 0;
    }
}
