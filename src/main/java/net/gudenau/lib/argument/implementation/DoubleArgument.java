package net.gudenau.lib.argument.implementation;

import net.gudenau.lib.argument.Argument;

/**
 * @hidden
 *
 * A simple floating point argument.
 * */
public class DoubleArgument implements Argument<Double>{
    private final double defaultValue;
    private final boolean required;
    
    public DoubleArgument(double defaultValue, boolean required){
        this.defaultValue = defaultValue;
        this.required = required;
    }
    
    @Override
    public boolean isRequired(){
        return required;
    }
    
    @Override
    public Double getValue(String argument){
        return Double.parseDouble(argument);
    }
    
    @Override
    public Double getDefault(){
        return defaultValue;
    }
    
    @Override
    public boolean hasDefault(){
        return defaultValue != 0;
    }
}
