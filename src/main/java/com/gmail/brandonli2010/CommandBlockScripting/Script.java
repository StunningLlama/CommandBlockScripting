package com.gmail.brandonli2010.CommandBlockScripting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.bukkit.Bukkit;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandBlockScripting extends JavaPlugin {

	@Override
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
		this.scriptthreads = new ArrayList<ScriptThread>();
		if (CommandBlockScripting.inputbuf == null)
		{
			CommandBlockScripting.inputbuf = new LinkedList<String>();
		}
	}

	@Override
	public void onDisable()
	{
		for (Map.Entry<String, String> entry : savedvarmap.entrySet()) {
		    getConfig().set("variables." + entry.getKey(), entry.getValue());
		}
		this.saveConfig();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("stopscripts"))
		{
			Integer[] totalthreads = this.killthreads();
			sender.sendMessage("\u00a74Killed " + totalthreads[1] + " running script(s) out of a total " + totalthreads[0] + " scripts.");
			return true;
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
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("output"))
		{
			if ((!(sender instanceof BlockCommandSender)) & (!(sender instanceof ConsoleCommandSender)))
			{
				sender.sendMessage("\u00a74You must be a command block or the console.");
				return true;
			}
			if (args.length < 2)
			{
				return false;
			}
			StringBuilder msg = new StringBuilder("");
			for (int ind = 1; ind < args.length; ind ++)
			{
				msg.append(args[ind]);
				msg.append(' ');
			}
			msg = new StringBuilder(msg.substring(0, msg.length() - 1));
			if (!args[0].equals(""))
			{
				msg = new StringBuilder(msg.toString().replace(args[0], "\u00a7"));
			}
			Bukkit.broadcastMessage(msg.toString());
		}
		if (cmd.getName().equalsIgnoreCase("script"))
		{
			if ((!(sender instanceof BlockCommandSender)) & (!(sender instanceof ConsoleCommandSender)))
			{
				sender.sendMessage("\u00a74You must be a command block or the console.");
				return true;
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

	private Integer[] killthreads()
	{
		int len = this.scriptthreads.size();
		int running = 0;
		for (int ind = 0; ind < this.scriptthreads.size(); ind ++)
		{
			if (this.scriptthreads.get(ind).isRunningScript())
			{
				running ++;
			}
			this.scriptthreads.get(ind).interrupt();
		}
		Integer[] toreturn = {len, running};
		this.scriptthreads.clear();
		return toreturn;
	}

	protected synchronized String getInput() throws InterruptedException
	{
		while (true)
		{
			if (Thread.currentThread().isInterrupted())
			{
				throw new InterruptedException();
			}
			Thread.sleep(1);
			
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
	private List<ScriptThread> scriptthreads;
	private static Queue<String> inputbuf;
	protected static long UUID = 0;
}
