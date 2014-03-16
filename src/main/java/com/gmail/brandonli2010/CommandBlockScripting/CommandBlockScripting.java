package com.gmail.brandonli2010.CommandBlockScripting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandBlockScripting extends JavaPlugin {
	public void onEnable()
	{
		if (CommandBlockScripting.savedvarmap == null)
		{
			CommandBlockScripting.savedvarmap = new HashMap<String, String>();
		}
		if (CommandBlockScripting.globalvarmap == null)
		{
			CommandBlockScripting.globalvarmap = new HashMap<String, String>();
		}
		this.saveDefaultConfig();
		for (String str : this.getConfig().getConfigurationSection("variables").getKeys(false))
		{
			CommandBlockScripting.savedvarmap.put(str, this.getConfig().getString("variables." + str));
		}
		this.scriptthreads = new ArrayList<Thread>();
		CommandBlockScripting.inputbuf = new LinkedList<String>();
	}
	public void onDisable()
	{
		for (Map.Entry<String, String> entry : savedvarmap.entrySet()) {
		    getConfig().set("variables." + entry.getKey(), entry.getValue());
		}
		this.saveConfig();
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("stopscripts"))
		{
			sender.sendMessage("\u00a74Killed " + this.killthreads() + " script(s).");
		}
		if (cmd.getName().equalsIgnoreCase("input"))
		{
			StringBuilder script = new StringBuilder("");
			for (int ind = 0; ind < args.length; ind ++)
			{
				script.append(args[ind]);
				script.append(' ');
			}
			CommandBlockScripting.inputbuf.add(script.substring(0, script.length() - 1));
		}
		if (cmd.getName().equalsIgnoreCase("script"))
		{
			if ((!(sender instanceof BlockCommandSender)) & (!(sender instanceof ConsoleCommandSender)))
			{
				sender.sendMessage("You must be non-player.");
			}
			StringBuilder script = new StringBuilder("");
			for (int ind = 0; ind < args.length; ind ++)
			{
				script.append(args[ind]);
				script.append(' ');
			}
			Script thisscript = new Script(script.substring(0, script.length() - 2), sender, this);
			try {
				thisscript.Parse();
			} catch(InvalidScriptException e) {
				this.getLogger().info("Error parsing script: " + e.getDescription());
				return true;
			}
			this.getLogger().info("Done parsing.");
			ScriptThread st = new ScriptThread(thisscript, this);
			this.scriptthreads.add(st);
			st.start();
			return true;
		}
		return false;
	}
	private int killthreads()
	{
		int len = this.scriptthreads.size();
		for (int ind = 0; ind < this.scriptthreads.size(); ind ++)
		{
			this.scriptthreads.get(ind).interrupt();
		}
		this.scriptthreads.clear();
		return len;
	}
	protected synchronized String getInput () throws InterruptedException
	{
		while (true)
		{
			if (Thread.currentThread().isInterrupted())
			{
				throw new InterruptedException();
			}
			try {
				Thread.sleep(1);
			} catch(InterruptedException e) {}
			if (CommandBlockScripting.inputbuf.size() > 0)
			{
				break;
			}
		}
		notify();
		return CommandBlockScripting.inputbuf.poll();
	}
	protected static HashMap<String, String> globalvarmap;
	protected static HashMap<String, String> savedvarmap;
	private List<Thread> scriptthreads;
	private static Queue<String> inputbuf;
}
