package net.gudenau.lib.argument.implementation;

import java.io.File;

public final class FileArgument extends AbstractArgument<File>{
    public FileArgument(File defaultValue, boolean required){
        super(defaultValue, required);
    }
    
    @Override
    public final File getValue(String argument){
        return new File(argument);
    }
}
