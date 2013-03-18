package org.mctourney.openhouse.util.commands;

@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(java.lang.annotation.ElementType.METHOD)

public @interface OpenHouseCommand
{
	// name of command
	public String[] name();

	// command description
	public String description() default "";

	// number of arguments
	public int argmin() default 0;
	public int argmax() default Integer.MAX_VALUE;

	// options
	public String options() default "";
	public String optionsHelp() default "";
}
