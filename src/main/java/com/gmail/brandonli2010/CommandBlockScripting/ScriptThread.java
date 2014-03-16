package com.gmail.brandonli2010.CommandBlockScripting;

public class ScriptThread extends Thread {
	private Script s;
	private int errorlevel;
	private CommandBlockScripting plugin;
	public ScriptThread(Script init, CommandBlockScripting instance)
	{
		this.s = init;
		this.errorlevel = 0;
		this.plugin = instance;
	}
	public void run()
	{
		this.errorlevel = s.run();
		plugin.getLogger().info("Script ended with errorlevel: " + this.errorlevel);
	}
	public int getErrorLevel()
	{
		return this.errorlevel;
	}
}
