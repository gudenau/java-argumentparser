package net.gudenau.lib.argument;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import net.gudenau.lib.annotation.NonNull;
import net.gudenau.lib.argument.implementation.DoubleArgument;
import net.gudenau.lib.argument.implementation.FileArgument;
import net.gudenau.lib.argument.implementation.IntegerArgument;
import net.gudenau.lib.argument.implementation.Pair;
import net.gudenau.lib.argument.implementation.StringArgument;

public class ArgumentResult{
    private final Map<String, Pair<Argument<?>, Object>> results;
    
    ArgumentResult(Map<String, Pair<Argument<?>, Object>> results){
        this.results = results;
    }
    
    public <T> T getResult(@NonNull String name, @NonNull Class<? extends Argument<T>> argument){
        Objects.requireNonNull(argument);
        Pair<Argument<?>, Object> result = results.get(Objects.requireNonNull(name));
        
        if(result == null){
            return null;
        }
    
        //noinspection unchecked
        return (T)result.getItem2();
    }
    
    public String getString(@NonNull String name){
        return getResult(name, StringArgument.class);
    }
    
    public File getFile(@NonNull String name){
        return getResult(name, FileArgument.class);
    }
    
    public int getInteger(@NonNull String name){
        return getResult(name, IntegerArgument.class);
    }
    
    public double getDouble(@NonNull String name){
        return getResult(name, DoubleArgument.class);
    }
    
    public boolean getFlag(@NonNull String name){
        return results.get(name) != null;
    }
}
