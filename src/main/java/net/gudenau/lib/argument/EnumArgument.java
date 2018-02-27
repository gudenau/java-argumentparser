package net.gudenau.lib.argument;

/**
 * The interface that is required for enums to be processed
 * by the default enum argument parser.
 * */
public interface EnumArgument{
    /**
     * Gets the name of the enum when passed on the command line.
     *
     * @return The value used on the command line
     * */
    String getArgumentName();
}
