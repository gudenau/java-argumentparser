package net.gudenau.lib.argument;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import net.gudenau.lib.annotation.NonNull;
import net.gudenau.lib.annotation.Nullable;
import net.gudenau.lib.argument.implementation.DoubleArgument;
import net.gudenau.lib.argument.implementation.FileArgument;
import net.gudenau.lib.argument.implementation.IntegerArgument;
import net.gudenau.lib.argument.implementation.Pair;
import net.gudenau.lib.argument.implementation.StringArgument;

/**
 * The result from {@link net.gudenau.lib.argument.ArgumentParser#parse(String[]) ArgumentParser.parse}.
 * */
@SuppressWarnings({"WeakerAccess", "unused"})
public class ArgumentResult{
    private final Map<String, Pair<Argument<?>, Object>> results;
    
    ArgumentResult(Map<String, Pair<Argument<?>, Object>> results){
        this.results = results;
    }
    
    /**
     * Gets a raw result.
     *
     * @param name The name of the argument to get
     *
     * @return The result
     * */
    @Nullable
    public Object getResult(@NonNull String name){
        return results.get(Objects.requireNonNull(name));
    }
    
    /**
     * Gets a result casted to the type of the argument.
     *
     * @param name The name of the argument to get
     * @param argument The instance of the argument handler
     *
     * @return The result
     * */
    @Nullable
    public <T> T getResult(@NonNull String name, @NonNull Class<? extends Argument<T>> argument){
        Objects.requireNonNull(argument);
        Pair<Argument<?>, Object> result = results.get(Objects.requireNonNull(name));
        
        if(result == null){
            return null;
        }
    
        //noinspection unchecked
        return (T)result.getItem2();
    }
    
    /**
     * Gets a string argument.
     *
     * @param name The name of the argument to get
     *
     * @return The result
     * */
    @Nullable
    public String getString(@NonNull String name){
        return getResult(name, StringArgument.class);
    }
    
    /**
     * Gets a file argument.
     *
     * @param name The name of the argument to get
     *
     * @return The result
     * */
    @Nullable
    public File getFile(@NonNull String name){
        return getResult(name, FileArgument.class);
    }
    
    /**
     * Gets an integer argument.
     *
     * @param name The name of the argument to get
     *
     * @return The result
     * */
    public int getInteger(@NonNull String name){
        return getResult(name, IntegerArgument.class);
    }
    
    /**
     * Gets a double argument.
     *
     * @param name The name of the argument to get
     *
     * @return The result
     * */
    public double getDouble(@NonNull String name){
        return getResult(name, DoubleArgument.class);
    }
    
    /**
     * Gets a flag argument.
     *
     * @param name The name of the argument to get
     *
     * @return The result
     * */
    public boolean getFlag(@NonNull String name){
        return results.get(name) != null;
    }
    
    /**
     * Gets an enum argument.
     *
     * @param name The name of the argument to get
     *
     * @return The result
     * */
    @Nullable
    public <T extends Enum<?> & EnumArgument> T getEnum(@NonNull String name, @NonNull Class<T> type){
        Objects.requireNonNull(type);
        Object result = getResult(Objects.requireNonNull(name));
        if(result == null){
            return null;
        }else{
            return type.cast(result);
        }
    }
}
