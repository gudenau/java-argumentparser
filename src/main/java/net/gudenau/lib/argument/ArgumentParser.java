package net.gudenau.lib.argument;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.gudenau.lib.annotation.NonNull;
import net.gudenau.lib.annotation.Nullable;
import net.gudenau.lib.argument.implementation.DoubleArgument;
import net.gudenau.lib.argument.implementation.EmptyArgument;
import net.gudenau.lib.argument.implementation.EnumArgumentImplementation;
import net.gudenau.lib.argument.implementation.FileArgument;
import net.gudenau.lib.argument.implementation.IntegerArgument;
import net.gudenau.lib.argument.implementation.Pair;
import net.gudenau.lib.argument.implementation.StringArgument;

/**
 * A simple argument parsing library.
 *
 * You can add your own argument types with the
 * {@link net.gudenau.lib.argument.Argument Argument} interface.
 * */
@SuppressWarnings({"WeakerAccess", "unused"})
public class ArgumentParser{
    /**
     * The regex pattern for param names
     * */
    private static final Pattern PATTERN_NAME = Pattern.compile("^-+([A-Za-z0-9]+)");
    /**
     * The regex pattern for param values
     * */
    private static final Pattern PATTERN_VALUE = Pattern.compile("^-+[A-Za-z0-9]+=([A-Za-z0-9 /\\\\_\"'.]+)$");
    
    /**
     * The map of registered arguments.
     * */
    private Map<String, Argument> argumentMap = new HashMap<>();
    
    /**
     * Creates a new instance with no arguments defined.
     * */
    public ArgumentParser(){}
    
    /**
     * Registers a new argument with this parser.
     *
     * @param name The name of the argument
     * @param argument The argument interface instance
     *
     * @return The current {@link net.gudenau.lib.argument.ArgumentParser ArgumentParser}
     *          for chaining
     * */
    public ArgumentParser registerArgument(@NonNull String name, @NonNull Argument<?> argument){
        Objects.requireNonNull(name);
        Objects.requireNonNull(argument);
        
        if(argumentMap.containsKey(name)){
            throw new IllegalStateException(String.format(
                "Argument \"%s\" was already registered", name
            ));
        }
        argumentMap.put(name, argument);
        return this;
    }
    
    /**
     * Parses arguments from the command line based on the registered
     * arguments of this instance.
     *
     * @param arguments The arguments passed to the program
     *
     * @return The results of the parse
     * */
    public ArgumentResult parse(@NonNull String[] arguments){
        Objects.requireNonNull(arguments);
        
        int length = arguments.length;
        Map<String, Pair<Argument<?>, Object>> results = new HashMap<>();
        
        for(int i = 0; i < length; i++){
            String current = arguments[i];
            
            if(!current.startsWith("-")){
                throw new IllegalArgumentException("Illegal argument: " + current);
            }else{
                // Find the name of the param
                Matcher matcher = PATTERN_NAME.matcher(current);
                if(!matcher.find()){
                    throw new IllegalArgumentException("Illegal argument:" + current);
                }
                String name = matcher.group(1);
                
                // Get the argument for the name
                Argument argument = argumentMap.get(name);
                if(argument == null){
                    throw new IllegalArgumentException(String.format(
                        "Unknown argument \"%s\"", name
                    ));
                }
                
                // Check if the value is part of this argument or the next one
                boolean valueIncluded = current.contains("=");
                if(argument instanceof EmptyArgument){
                    // An empty argument should not have a value
                    if(valueIncluded){
                        throw new IllegalArgumentException(String.format(
                            "%s can not have a value!",
                            name
                        ));
                    }
                    
                    // It exists
                    results.put(name, new Pair<>(argument, true));
                }else{
                    String rawValue;
                    
                    // Extract the value
                    if(valueIncluded){
                        Matcher valueMatcher = PATTERN_VALUE.matcher(current);
                        if(!valueMatcher.find()){
                            throw new IllegalArgumentException("Malformed param: " + current);
                        }
                        rawValue = valueMatcher.group(1);
                    }else if(i < length - 1){
                        rawValue = arguments[i + 1];
                        i++;
                    }else{
                        throw new IllegalArgumentException("Malformed param: " + current);
                    }
                    
                    // Parse the value
                    results.put(name, new Pair<>(argument, argument.getValue(rawValue)));
                }
            }
        }
        
        for(Map.Entry<String, Argument> entry : argumentMap.entrySet()){
            Argument argument = entry.getValue();
            String name = entry.getKey();
            if(argument.hasDefault() && !results.containsKey(name)){
                results.put(name, new Pair<>(argument, argument.getDefault()));
            }
        }
        
        if(argumentMap.entrySet().stream()
            .filter(entry->entry.getValue().isRequired())
            .anyMatch(entry->!results.containsKey(entry.getKey()))
        ){
            throw new IllegalArgumentException("Required param is missing");
        }
        
        return new ArgumentResult(results);
    }
    
    // ---------- File ----------
    
    /**
     * Registers a new file argument with this parser.
     *
     * @param name The name of the argument
     *
     * @return The current {@link net.gudenau.lib.argument.ArgumentParser ArgumentParser}
     *          for chaining
     * */
    public ArgumentParser registerFileArgument(@NonNull String name){
        return registerFileArgument(name, false);
    }
    
    /**
     * Registers a new file argument with this parser.
     *
     * @param name The name of the argument
     * @param required Pass true if the argument is required
     *
     * @return The current {@link net.gudenau.lib.argument.ArgumentParser ArgumentParser}
     *          for chaining
     * */
    public ArgumentParser registerFileArgument(@NonNull String name, boolean required){
        return registerArgument(name, new FileArgument(null, required));
    }
    
    /**
     * Registers a new file argument with this parser.
     *
     * @param name The name of the argument
     * @param defaultValue The default value of this argument
     *
     * @return The current {@link net.gudenau.lib.argument.ArgumentParser ArgumentParser}
     *          for chaining
     * */
    public ArgumentParser registerArgument(@NonNull String name, @Nullable File defaultValue){
        return registerArgument(name, defaultValue, false);
    }
    
    /**
     * Registers a new file argument with this parser.
     *
     * @param name The name of the argument
     * @param defaultValue The default value of this argument
     * @param required Pass true if the argument is required
     *
     * @return The current {@link net.gudenau.lib.argument.ArgumentParser ArgumentParser}
     *          for chaining
     * */
    public ArgumentParser registerArgument(@NonNull String name, @Nullable File defaultValue, boolean required){
        return registerArgument(name, new FileArgument(defaultValue, required));
    }
    
    // ---------- String ----------
    
    /**
     * Registers a new string argument with this parser.
     *
     * @param name The name of the argument
     *
     * @return The current {@link net.gudenau.lib.argument.ArgumentParser ArgumentParser}
     *          for chaining
     * */
    public ArgumentParser registerStringArgument(@NonNull String name){
        return registerStringArgument(name, false);
    }
    
    /**
     * Registers a new string argument with this parser.
     *
     * @param name The name of the argument
     * @param required Pass true if the argument is required
     *
     * @return The current {@link net.gudenau.lib.argument.ArgumentParser ArgumentParser}
     *          for chaining
     * */
    public ArgumentParser registerStringArgument(@NonNull String name, boolean required){
        return registerArgument(name, new StringArgument(null, required));
    }
    
    /**
     * Registers a new string argument with this parser.
     *
     * @param name The name of the argument
     * @param defaultValue The default value of this argument
     *
     * @return The current {@link net.gudenau.lib.argument.ArgumentParser ArgumentParser}
     *          for chaining
     * */
    public ArgumentParser registerArgument(@NonNull String name, @Nullable String defaultValue){
        return registerArgument(name, defaultValue, false);
    }
    
    /**
     * Registers a new string argument with this parser.
     *
     * @param name The name of the argument
     * @param defaultValue The default value of this argument
     * @param required Pass true if the argument is required
     *
     * @return The current {@link net.gudenau.lib.argument.ArgumentParser ArgumentParser}
     *          for chaining
     * */
    public ArgumentParser registerArgument(@NonNull String name, @Nullable String defaultValue, boolean required){
        return registerArgument(name, new StringArgument(defaultValue, required));
    }
    
    // ---------- Double ----------
    
    /**
     * Registers a double new argument with this parser.
     *
     * @param name The name of the argument
     *
     * @return The current {@link net.gudenau.lib.argument.ArgumentParser ArgumentParser}
     *          for chaining
     * */
    public ArgumentParser registerDoubleArgument(@NonNull String name){
        return registerDoubleArgument(name, false);
    }
    
    /**
     * Registers a new double argument with this parser.
     *
     * @param name The name of the argument
     * @param required Pass true if the argument is required
     *
     * @return The current {@link net.gudenau.lib.argument.ArgumentParser ArgumentParser}
     *          for chaining
     * */
    public ArgumentParser registerDoubleArgument(@NonNull String name, boolean required){
        return registerArgument(name, new DoubleArgument(0, required));
    }
    
    /**
     * Registers a new double argument with this parser.
     *
     * @param name The name of the argument
     * @param defaultValue The default value of this argument
     *
     * @return The current {@link net.gudenau.lib.argument.ArgumentParser ArgumentParser}
     *          for chaining
     * */
    public ArgumentParser registerArgument(@NonNull String name, @Nullable double defaultValue){
        return registerArgument(name, defaultValue, false);
    }
    
    /**
     * Registers a new double argument with this parser.
     *
     * @param name The name of the argument
     * @param defaultValue The default value of this argument
     * @param required Pass true if the argument is required
     *
     * @return The current {@link net.gudenau.lib.argument.ArgumentParser ArgumentParser}
     *          for chaining
     * */
    public ArgumentParser registerArgument(@NonNull String name, @Nullable double defaultValue, boolean required){
        return registerArgument(name, new DoubleArgument(defaultValue, required));
    }
    
    // ---------- Integer ----------
    
    /**
     * Registers a new integer argument with this parser.
     *
     * @param name The name of the argument
     *
     * @return The current {@link net.gudenau.lib.argument.ArgumentParser ArgumentParser}
     *          for chaining
     * */
    public ArgumentParser registerIntegerArgument(@NonNull String name){
        return registerIntegerArgument(name, false);
    }
    
    /**
     * Registers a new integer argument with this parser.
     *
     * @param name The name of the argument
     * @param required Pass true if the argument is required
     *
     * @return The current {@link net.gudenau.lib.argument.ArgumentParser ArgumentParser}
     *          for chaining
     * */
    public ArgumentParser registerIntegerArgument(@NonNull String name, boolean required){
        return registerArgument(name, new IntegerArgument(0, required));
    }
    
    /**
     * Registers a new integer argument with this parser.
     *
     * @param name The name of the argument
     * @param defaultValue The default value of this argument
     *
     * @return The current {@link net.gudenau.lib.argument.ArgumentParser ArgumentParser}
     *          for chaining
     * */
    public ArgumentParser registerArgument(@NonNull String name, @Nullable int defaultValue){
        return registerArgument(name, defaultValue, false);
    }
    
    /**
     * Registers a new integer argument with this parser.
     *
     * @param name The name of the argument
     * @param defaultValue The default value of this argument
     * @param required Pass true if the argument is required
     *
     * @return The current {@link net.gudenau.lib.argument.ArgumentParser ArgumentParser}
     *          for chaining
     * */
    public ArgumentParser registerArgument(@NonNull String name, @Nullable int defaultValue, boolean required){
        return registerArgument(name, new IntegerArgument(defaultValue, required));
    }
    
    // ---------- Enumeration ----------
    
    /**
     * Registers a new enum argument with this parser.
     *
     * @param name The name of the argument
     * @param <T> The type of the enum, must implement {@link net.gudenau.lib.argument.EnumArgument}
     *
     * @return The current {@link net.gudenau.lib.argument.ArgumentParser ArgumentParser}
     *          for chaining
     * */
    public <T extends Enum<?> & EnumArgument> ArgumentParser registerEnumArgument(@NonNull String name, @NonNull Class<T> type){
        return registerEnumArgument(name, false, type);
    }
    
    /**
     * Registers a new enum argument with this parser.
     *
     * @param name The name of the argument
     * @param required Pass true if the argument is required
     * @param <T> The type of the enum, must implement {@link net.gudenau.lib.argument.EnumArgument}
     *
     * @return The current {@link net.gudenau.lib.argument.ArgumentParser ArgumentParser}
     *          for chaining
     * */
    public <T extends Enum<?> & EnumArgument> ArgumentParser registerEnumArgument(@NonNull String name, boolean required, @NonNull Class<T> type){
        return registerArgument(name, new EnumArgumentImplementation<>(null, false, type));
    }
    
    /**
     * Registers a new enum argument with this parser.
     *
     * @param name The name of the argument
     * @param defaultValue The default value of this argument
     * @param <T> The type of the enum, must implement {@link net.gudenau.lib.argument.EnumArgument}
     *
     * @return The current {@link net.gudenau.lib.argument.ArgumentParser ArgumentParser}
     *          for chaining
     * */
    public <T extends Enum<?> & EnumArgument> ArgumentParser registerArgument(@NonNull String name, @Nullable T defaultValue, @NonNull Class<T> type){
        return registerArgument(name, defaultValue, false, type);
    }
    
    /**
     * Registers a new enum argument with this parser.
     *
     * @param name The name of the argument
     * @param defaultValue The default value of this argument
     * @param required Pass true if the argument is required
     * @param <T> The type of the enum, must implement {@link net.gudenau.lib.argument.EnumArgument}
     *
     * @return The current {@link net.gudenau.lib.argument.ArgumentParser ArgumentParser}
     *          for chaining
     * */
    public <T extends Enum<?> & EnumArgument> ArgumentParser registerArgument(@NonNull String name, @Nullable T defaultValue, boolean required, @NonNull Class<T> type){
        return registerArgument(name, new EnumArgumentImplementation<>(defaultValue, required, type));
    }
    
    // ---------- Flag ----------
    
    /**
     * Registers a new flag argument with this parser.
     *
     * @param name The name of the argument
     *
     * @return The current {@link net.gudenau.lib.argument.ArgumentParser ArgumentParser}
     *          for chaining
     * */
    public ArgumentParser registerFlagArgument(@NonNull String name){
        return registerArgument(name, new EmptyArgument());
    }
}
