package net.gudenau.lib.argument.implementation;

import net.gudenau.lib.argument.Argument;

/**
 * @hidden
 *
 * Basically a flag.
 * */
public final class EmptyArgument implements Argument<Boolean>{
    @Override
    public boolean isRequired(){
        return false;
    }
    
    @Override
    public Boolean getValue(String argument){
        return true;
    }
    
    @Override
    public Boolean getDefault(){
        return false;
    }
    
    @Override
    public boolean hasDefault(){
        return false;
    }
}
