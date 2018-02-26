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
import net.gudenau.lib.argument.implementation.FileArgument;
import net.gudenau.lib.argument.implementation.IntegerArgument;
import net.gudenau.lib.argument.implementation.Pair;
import net.gudenau.lib.argument.implementation.StringArgument;

public class ArgumentParser{
    private static final Pattern PATTERN_NAME = Pattern.compile("^-+([A-Za-z0-9]+)");
    private static final Pattern PATTERN_VALUE = Pattern.compile("^-+[A-Za-z0-9]+=([A-Za-z0-9 /\\\\_\"'\\.]+)$");
    
    private Map<String, Argument> argumentMap = new HashMap<>();
    
    public ArgumentParser(){}
    
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
    
    public ArgumentParser registerFileArgument(@NonNull String name){
        return registerFileArgument(name, false);
    }
    
    public ArgumentParser registerFileArgument(@NonNull String name, boolean required){
        return registerArgument(name, new FileArgument(null, required));
    }
    
    public ArgumentParser registerArgument(@NonNull String name, @Nullable File defaultValue){
        return registerArgument(name, defaultValue, false);
    }
    
    public ArgumentParser registerArgument(@NonNull String name, @Nullable File defaultValue, boolean required){
        return registerArgument(name, new FileArgument(defaultValue, required));
    }
    
    // ---------- String ----------
    
    public ArgumentParser registerStringArgument(@NonNull String name){
        return registerStringArgument(name, false);
    }
    
    public ArgumentParser registerStringArgument(@NonNull String name, boolean required){
        return registerArgument(name, new StringArgument(null, required));
    }
    
    public ArgumentParser registerArgument(@NonNull String name, @Nullable String defaultValue){
        return registerArgument(name, defaultValue, false);
    }
    
    public ArgumentParser registerArgument(@NonNull String name, @Nullable String defaultValue, boolean required){
        return registerArgument(name, new StringArgument(defaultValue, required));
    }
    
    // ---------- Double ----------
    
    public ArgumentParser registerDoubleArgument(@NonNull String name){
        return registerDoubleArgument(name, false);
    }
    
    public ArgumentParser registerDoubleArgument(@NonNull String name, boolean required){
        return registerArgument(name, new DoubleArgument(0, required));
    }
    
    public ArgumentParser registerArgument(@NonNull String name, @Nullable double defaultValue){
        return registerArgument(name, defaultValue, false);
    }
    
    public ArgumentParser registerArgument(@NonNull String name, @Nullable double defaultValue, boolean required){
        return registerArgument(name, new DoubleArgument(defaultValue, required));
    }
    
    // ---------- Integer ----------
    
    public ArgumentParser registerIntegerArgument(@NonNull String name){
        return registerIntegerArgument(name, false);
    }
    
    public ArgumentParser registerIntegerArgument(@NonNull String name, boolean required){
        return registerArgument(name, new IntegerArgument(0, required));
    }
    
    public ArgumentParser registerArgument(@NonNull String name, @Nullable int defaultValue){
        return registerArgument(name, defaultValue, false);
    }
    
    public ArgumentParser registerArgument(@NonNull String name, @Nullable int defaultValue, boolean required){
        return registerArgument(name, new IntegerArgument(defaultValue, required));
    }
    
    // ---------- Flag ----------
    
    public ArgumentParser registerFlagArgument(@NonNull String name){
        return registerArgument(name, new EmptyArgument());
    }
}
