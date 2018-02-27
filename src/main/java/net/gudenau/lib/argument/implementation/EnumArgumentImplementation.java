package net.gudenau.lib.argument.implementation;

import java.util.HashMap;
import java.util.Map;

import net.gudenau.lib.argument.EnumArgument;

public class EnumArgumentImplementation<T extends Enum<?> & EnumArgument> extends AbstractArgument<T>{
    private Map<String, T> valueMap = new HashMap<>();
    
    public EnumArgumentImplementation(T defaultValue, boolean required, Class<T> type){
        super(defaultValue, required);
        
        T[] constants = type.getEnumConstants();
        for(T constant : constants){
            valueMap.put(constant.getArgumentName(), constant);
        }
    }
    
    @Override
    public T getValue(String argument){
        return valueMap.get(argument);
    }
}
