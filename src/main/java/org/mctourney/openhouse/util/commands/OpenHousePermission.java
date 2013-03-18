package org.mctourney.openhouse.util.commands;

@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(java.lang.annotation.ElementType.METHOD)

public @interface OpenHousePermission
{
	// permissions nodes required
	public String[] nodes() default {};

	// can console send this command?
	public boolean console() default true;
}
