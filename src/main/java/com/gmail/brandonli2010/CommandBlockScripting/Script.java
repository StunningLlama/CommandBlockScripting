package com.gmail.brandonli2010.CommandBlockScripting;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Script
{

	public Script(String init, CommandSender initblock, CommandBlockScripting instance)
	{
		this.script_str = init;
		this.block = initblock;
		this.script_arr = new ArrayList<String>();
		this.server = instance;
		this.labelmap = new HashMap<String, Integer>();
		this.varmap = new HashMap<String, String>();
	}

	protected void Parse()
	{
		StringBuilder newscript = new StringBuilder(this.script_str + " ");
		for (int ind = 0; ind < newscript.length(); ind ++)
		{
			if (newscript.charAt(ind) == ';')
			{
				newscript.replace(ind, ind + 1, "\u000c");
			}
			else if (newscript.charAt(ind) == '#')
			{
				newscript.replace(ind, ind + 1, "\u0005");
			}
			if (newscript.charAt(ind) == '\\')
			{
				newscript.replace(ind, ind + 1, "");
			}
		}
		newscript = new StringBuilder(newscript.substring(0, newscript.length() - 1));
		String[] args = newscript.toString().split("\u000c");
		if (args.length == 0)
		{
			throw new InvalidScriptException("Script must have size!");
		}
		for (int ind = 0; ind < args.length; ind ++)
		{
			this.script_arr.add(args[ind]);
		}
		this.script_arr.add("\u0001\u0012\u001c0");
		int brackets = 0;
		List<Integer> levels = new ArrayList<Integer>();
		for (int ind = 0; ind < this.script_arr.size(); ind ++)
		{
			levels.add(-1);
		}
		for (int ind = 0; ind < this.script_arr.size(); ind ++)
		{
			if (this.script_arr.get(ind).startsWith(":if ") | this.script_arr.get(ind).startsWith(":while "))
			{
				levels.set(ind, brackets);
				brackets ++;
			}
			if (this.script_arr.get(ind).equals(":end"))
			{
				brackets --;
				if (brackets < 0)
				{
					throw new InvalidScriptException("Unmatched closing keyword 'end' at line " + ind + ".");
				}
				levels.set(ind, brackets);
			}
			if (this.script_arr.get(ind).equals(":else"))
			{
				brackets --;
				if (ind < 0)
				{
					throw new InvalidScriptException("Unmatched keyword 'else' at line " + ind + ".");
				}
				levels.set(ind, brackets);
				brackets ++;
			}
			if (this.script_arr.get(ind).startsWith(":break "))
			{
				levels.set(ind, brackets);
			}
		}
		if (brackets > 0)
		{
			throw new InvalidScriptException("Unbalanced control structure at line " + (this.script_arr.size() - 1) + ".");
		}
		List<String> temp_script_arr = new ArrayList<String>();
		for (int ind = 0; ind < this.script_arr.size(); ind ++)
		{
			temp_script_arr.add(this.script_arr.get(ind));
		}
		for (int ind = 0; ind < this.script_arr.size(); ind ++)
		{
			if (this.script_arr.get(ind).equals(":end") | this.script_arr.get(ind).equals(":else"))
			{
				for (int indn = ind; indn < this.script_arr.size(); indn ++)
				{
					if (this.script_arr.get(indn).equals(":end") & levels.get(ind).equals(levels.get(indn)))
					{
						temp_script_arr.set(ind, "\u0001\u0011\u001c" + (indn + 1) + "\u001c0");
						break;
					}
				}
				for (int indn = ind; indn > -1; indn --)
				{
					if ((this.script_arr.get(indn).startsWith(":while ") | this.script_arr.get(indn).startsWith(":if ")) & levels.get(ind).equals(levels.get(indn)))
					{
						if (this.script_arr.get(indn).startsWith(":while "))
						{
							temp_script_arr.set(ind, "\u0001\u0011\u001c" + indn + "\u001c0");
						}
						break;
					}
				}
			}
			if (this.script_arr.get(ind).startsWith(":if ") | this.script_arr.get(ind).startsWith(":while "))
			{
				for (int indn = ind; indn < this.script_arr.size(); indn ++)
				{
					if ((this.script_arr.get(indn).equals(":else") | this.script_arr.get(indn).equals(":end")) & levels.get(ind).equals(levels.get(indn)))
					{
						temp_script_arr.set(ind, "\u0001\u0011\u001c" + (indn + 1) + "\u001c" + this.FirstArg(this.script_arr.get(ind)));
						break;
					}
				}
			}
			if (this.script_arr.get(ind).startsWith(":break "))
			{
				int lvl = 0;
				try {
					lvl = levels.get(ind) - Integer.valueOf(this.FirstArg(this.script_arr.get(ind)));
				} catch (NumberFormatException e) { }
				if ((lvl < 0) | (Integer.valueOf(this.FirstArg(this.script_arr.get(ind))) <= 0)) {lvl = 0;}
				for (int indn = ind; indn < this.script_arr.size(); indn ++)
				{
					if (this.script_arr.get(indn).equals(":end") & levels.get(indn).equals(lvl))
					{
						temp_script_arr.set(ind, "\u0001\u0011\u001c" + (indn + 1) + "\u001c0");
					}
				}
			}
		}
		this.script_arr = temp_script_arr;
		for (int ind = 0; ind < this.script_arr.size(); ind ++)
		{
			if (this.script_arr.get(ind).startsWith(":label "))
			{
				labelmap.put(this.FirstArg(this.script_arr.get(ind)), ind + 1);
			}
		}
		for (int ind = 0; ind < this.script_arr.size(); ind ++)
		{
			if (this.script_arr.get(ind).startsWith(":goto "))
			{
				this.script_arr.set(ind, "\u0001\u0013\u001c" + this.labelmap.get(this.FirstArg(this.script_arr.get(ind))));
			}
			if (this.script_arr.get(ind).startsWith(":jump "))
			{
				this.script_arr.set(ind, "\u0001\u0011\u001c" + this.FirstArg(this.script_arr.get(ind)) + "\u001c0");
			}
			if (this.script_arr.get(ind).startsWith(":var "))
			{
				this.script_arr.set(ind, "\u0001\u0014\u001c" + this.FirstArg(this.script_arr.get(ind)));
			}
			if (this.script_arr.get(ind).startsWith(":wait "))
			{
				this.script_arr.set(ind, "\u0001\u0015\u001c" + this.FirstArg(this.script_arr.get(ind)));
			}
			if (this.script_arr.get(ind).startsWith(":iterator "))
			{
				this.script_arr.set(ind, "\u0001\u0016\u001c" + this.FirstArg(this.script_arr.get(ind)));
			}
			if (this.script_arr.get(ind).startsWith(":exit "))
			{
				this.script_arr.set(ind, "\u0001\u0012\u001c" + this.FirstArg(this.script_arr.get(ind)));
			}
		}
		for (int ind = 0; ind < this.script_arr.size(); ind ++)
		{
			if (this.script_arr.get(ind).startsWith(":"))
			{
				this.script_arr.set(ind, "\u0001\u0018\u001c");
			}
		}
	}

	private String parseLine(String str)
	{
		boolean inexp = false;
		StringBuilder tmpexp = new StringBuilder("");
		StringBuilder finishedstr = new StringBuilder("");
		for (int ind = 0; ind < str.length(); ind ++)
		{
			if (str.charAt(ind) == '\u0005')
			{
				if (inexp)
				{
					finishedstr.append(this.parseExpression(tmpexp.toString()));
					tmpexp = new StringBuilder("");
				}
				inexp = !inexp;
			}
			else if (inexp)
			{
				tmpexp.append(str.charAt(ind));
			}
			else
			{
				finishedstr.append(str.charAt(ind));
			}
		}
		return finishedstr.toString();
	}

	private String parseExpression(String str)
	{
		StringBuilder tmpvar = new StringBuilder("");
		boolean invar = false;
		Stack<ScriptVariable> varstack = new Stack<ScriptVariable>();
		int brackets = 0;
		for (int ind = 0; ind < str.length(); ind ++)
		{
			if (str.charAt(ind) == '\'')
			{
				if (ind < (str.length() - 1))
				{
					ind ++;
				}
			}
			else if (str.charAt(ind) == '{')
			{
				brackets ++;
			}
			else if (str.charAt(ind) == '}')
			{
				brackets --;
				if (brackets < 0)
				{
					this.errorlvl = 1;
					return null;
				}
			}
		}
		if (brackets != 0)
		{
			this.errorlvl = 1;
			return null;
		}
		brackets = 0;
		for (int ind = 0; ind < str.length(); ind ++)
		{
			if (str.charAt(ind) == '\'')
			{
				if (ind < (str.length() - 1))
				{
					ind ++;
					tmpvar.append(str.charAt(ind));
				}
			}
			else if (str.charAt(ind) == '{')
			{
				if (brackets > 0)
				{
					tmpvar.append("\u0002");
				}
				brackets ++;
				invar = true;
			}
			else if (str.charAt(ind) == '}')
			{
				brackets --;
				if (brackets == 0)
				{
					varstack.push(new ScriptVariable(processVarExp(tmpvar.toString())));
					tmpvar = new StringBuilder("");
					invar = !invar;
				}
				else
				{
					tmpvar.append("\u0003");
				}
			}
			else if (invar)
			{
				tmpvar.append(str.charAt(ind));
			}
			else
			{
				if (varstack.size() < 2)
				{
					varstack.push(new ScriptVariable(""));
				}
				else
				{
					ScriptVariable a = varstack.pop();
					ScriptVariable b = varstack.pop();
					try {
						varstack.push(ScriptVariable.performOp(str.charAt(ind), a, b));
					} catch (IllegalArgumentException e) {varstack.push(a); varstack.push(b);
					} catch (NullPointerException e) {varstack.push(new ScriptVariable(null)); this.errorlvl = 1;}
				}
			}
		}
		if (varstack.empty())
		{
			this.errorlvl = 1;
			return null;
		}
		return varstack.peek().value;
	}

	protected int run()
	{
		this.errorlvl = 0;
		this.iteratorindex = 0;
		this.interrupted = false;
		int index = 0;
		String parsedline = "";
		while (true)
		{
			if (Thread.currentThread().isInterrupted() | this.interrupted)
			{
				return -1;
			}
			if (index > this.script_arr.size() - 1)
			{
				return -2;
			}
			parsedline = this.script_arr.get(index);
				if (parsedline.startsWith("\u0001"))
				{
					if (parsedline.startsWith("\u0001\u0011"))
					{
						parsedline = parseLine(this.script_arr.get(index));
						if (parsedline.split("\u001c")[2].equals("0"))
						{
							try {
								index = Integer.valueOf(parsedline.split("\u001c")[1]);
							} catch(NumberFormatException e) {
								return -3;
							}
						}
						else
						{
							index ++;
						}
					}
					if (parsedline.startsWith("\u0001\u0012"))
					{
						parsedline = parseLine(this.script_arr.get(index));
						try {
							return Integer.valueOf(parsedline.split("\u001c")[1]);
						} catch(NumberFormatException e) {
							return -3;
						}
					}
					if (parsedline.startsWith("\u0001\u0013"))
					{
						parsedline = parseLine(this.script_arr.get(index));
						try {
							index = Integer.valueOf(parsedline.split("\u001c")[1]);
							if ((index < 0) | (index >= this.script_arr.size()))
							{
								return -2;
							}
						} catch(NumberFormatException e) {
							return -3;
						}
					}
					if (parsedline.startsWith("\u0001\u0014"))
					{
						String var = this.parseLine(parsedline.split("\u001c")[1].split(" ")[0]);
						String val = this.parseLine(parsedline.split("\u001c")[1].split(" ")[1]);
						if (var.matches("[a-zA-Z0-9\\-\\_]+"))
						{
							if (var.startsWith("__"))
							{
								CommandBlockScripting.savedvarmap.put(var, val);
							}
							else if (var.startsWith("_"))
							{
								CommandBlockScripting.globalvarmap.put(var, val);
							}
							else
							{
								varmap.put(var, val);
							}
						}
						index ++;
					}
					if (parsedline.startsWith("\u0001\u0015"))
					{
						parsedline = parseLine(this.script_arr.get(index));
						int millis = 0;
						try {
							millis = Integer.valueOf(parsedline.split("\u001c")[1]);
						} catch(NumberFormatException e) {millis = -1;}
						if (millis != -1)
						{
							try {
								Thread.sleep(millis);
							} catch (InterruptedException e) {return -1;}
						}
						index ++;
					}
					if (parsedline.startsWith("\u0001\u0016"))
					{
						if (this.script_arr.get(index).split("\u001c")[1].equals("get"))
						{
							this.iteratorindex = this.iteratorindex + 1;
						}
						if (this.script_arr.get(index).split("\u001c")[1].equals("reset"))
						{
							this.iteratorindex = 0;
						}
					}
				}
				else
				{
					parsedline = parseLine(this.script_arr.get(index));
					this.server.getServer().dispatchCommand(block, parsedline);
					index ++;
				}
			this.errorlvl = 0;
		}
	}

	private String processVarExp(String str)
	{
		StringBuilder tmpvar = new StringBuilder("");
		StringBuilder finished = new StringBuilder("");
		StringBuilder ref = new StringBuilder(str);
		int brackets = 0;
		for (int ind = 0; ind < ref.length(); ind ++)
		{
			if (ref.charAt(ind) == '\u0002')
			{
				if (brackets == 0)
				{
					tmpvar = new StringBuilder("");
				}
				else
				{
					tmpvar.append('\u0002');
				}
				brackets ++;
			}
			else if (ref.charAt(ind) == '\u0003')
			{
				brackets --;
				if (brackets == 0)
				{
					finished.append(this.processVarExp(tmpvar.toString()));
				}
				else
				{
					tmpvar.append('\u0003');
				}
			}
			else if (brackets != 0)
			{
				tmpvar.append(ref.charAt(ind));
			}
			else if (brackets == 0)
			{
				finished.append(ref.charAt(ind));
			}
		}
		return this.processVar(finished.toString());
	}

	@SuppressWarnings("deprecation")
	private String processVar(String str)
	{
		if (str.startsWith("*"))
		{
			return str.substring(1);
		}
		else if (str.startsWith("~"))
		{
			String[] env = str.substring(1).split("\\.");
			if (env.length < 1)
			{
				this.errorlvl = 1;
				return null;
			}
			if (env[0].equals("in"))
			{
				try {
					return this.server.getInput();
				} catch (InterruptedException e) {
					this.interrupted = true;
				}
			}
			if (env[0].equals("env"))
			{
				if (env.length < 2)
				{
					this.errorlvl = 1;
					return null;
				}
				if (env[1].equals("errorlvl"))
				{
					return String.valueOf(this.errorlvl);
				}
				if (env[1].equals("rand"))
				{
					return String.valueOf(Math.random());
				}
				if (env[1].equals("uuid"))
				{
					long id = CommandBlockScripting.UUID;
					CommandBlockScripting.UUID ++;
					return String.valueOf(id);
				}
				this.errorlvl = 1;
				return null;
			}
			if (env[0].equals("pl") | env[0].equals("p") | env[0].equals("it"))
			{
				if (env.length < 3)
				{
					this.errorlvl = 1;
					return null;
				}
				Player playertarget = null;
				if (env[0].equals("p"))
				{
					playertarget = Bukkit.getPlayer(env[1]);
				}
				if (env[0].equals("it"))
				{
					if (env[1].equals("current"))
					{
						if (this.iteratorindex < Bukkit.getOnlinePlayers().length)
						{
							playertarget = Bukkit.getOnlinePlayers()[this.iteratorindex];
						}
						else
						{
							this.errorlvl = 1;
							return null;
						}
					}
					if (env[1].equals("hasnext"))
					{
						if (this.iteratorindex < Bukkit.getOnlinePlayers().length)
						{
							return "1";
						}
						else
						{
							return "0";
						}
					}
				}
				if (env[0].equals("pl") & (block instanceof BlockCommandSender))
				{
					BlockCommandSender blocksender = (BlockCommandSender) this.block;
					if (env[1].equals("np"))
					{
						Double shortestd = Double.POSITIVE_INFINITY;
						Player finalp = null;
						for (Player p : Bukkit.getOnlinePlayers())
						{
							if (blocksender.getBlock().getWorld().equals(p.getWorld()) && (blocksender.getBlock().getLocation().distanceSquared(p.getLocation()) < shortestd))
							{
								shortestd = blocksender.getBlock().getLocation().distanceSquared(p.getLocation());
								finalp = p;
							}
						}
						playertarget = finalp;
					}
					if (env[1].equals("fp"))
					{
						Double longestd = 0.0;
						Player finalp = null;
						for (Player p : Bukkit.getOnlinePlayers())
						{
							if (blocksender.getBlock().getWorld().equals(p.getWorld()) && (blocksender.getBlock().getLocation().distanceSquared(p.getLocation()) > longestd))
							{
								longestd = blocksender.getBlock().getLocation().distanceSquared(p.getLocation());
								finalp = p;
							}
						}
						playertarget = finalp;
					}
					if (env[1].equals("rp"))
					{
						if (Bukkit.getOnlinePlayers().length > 0)
						{
							Random r = new Random();
							playertarget = Bukkit.getOnlinePlayers()[r.nextInt(Bukkit.getOnlinePlayers().length)];
						}
					}
				}
				if (playertarget == null) {this.errorlvl = 1; return null;}
				String playerreturn = "";
				if (env[2].equals("name"))
				{
					return playertarget.getName();
				}
				if (env[2].equals("flying"))
				{
					if (playertarget.isFlying()) {return "1";}
					else {return "0";}
				}
				if (env[2].equals("sneaking"))
				{
					if (playertarget.isSneaking()) {return "1";}
					else {return "0";}
				}
				if (env[2].equals("sprinting"))
				{
					if (playertarget.isSprinting()) {return "1";}
					else {return "0";}
				}
				if (env[2].equals("blocking"))
				{
					if (playertarget.isBlocking()) {return "1";}
					else {return "0";}
				}
				if (env[2].equals("sleeping"))
				{
					if (playertarget.isSleeping()) {return "1";}
					else {return "0";}
				}
				if (env[2].equals("hunger"))
				{
					return String.valueOf(playertarget.getFoodLevel());
				}
				if (env[2].equals("exp"))
				{
					return String.valueOf((int) playertarget.getExp());
				}
				if (env[2].equals("fall"))
				{
					return String.valueOf((int) playertarget.getFallDistance());
				}
				if (env[2].equals("gamemode"))
				{
					if (playertarget.getGameMode() == GameMode.SURVIVAL) {playerreturn = "0";}
					if (playertarget.getGameMode() == GameMode.CREATIVE) {playerreturn = "1";}
					if (playertarget.getGameMode() == GameMode.ADVENTURE) {playerreturn = "2";}
					return playerreturn;
				}
				if (env[2].equals("health"))
				{
					return String.valueOf((int) playertarget.getHealth());
				}
				if (env[2].equals("level"))
				{
					return String.valueOf(playertarget.getLevel());
				}
				if (env[2].equals("air"))
				{
					return String.valueOf(playertarget.getRemainingAir());
				}
				if (env[2].equals("item") | env[2].equals("inv"))
				{
					if (env.length < 5)
					{
						this.errorlvl = 1;
						return null;
					}
					ItemStack itemtarget = new ItemStack(Material.AIR);
					if (env[2].equals("item"))
					{
						if (env[3].equals("hand"))
						{
							itemtarget = playertarget.getItemInHand();
						}
					}
					if (env[2].equals("inv"))
					{
						try {
							itemtarget = playertarget.getInventory().getItem(Integer.valueOf(env[3]));
						} catch(NumberFormatException e) {}
					}
					if (itemtarget == null) {this.errorlvl = 1; return null;}
					if (env[4].equals("type"))
					{
						return String.valueOf(playertarget.getItemInHand().getTypeId());
					}
					if (env[4].equals("data"))
					{
						return String.valueOf(playertarget.getItemInHand().getData().getData());
					}
					if (env[4].equals("amount"))
					{
						return String.valueOf(playertarget.getItemInHand().getAmount());
					}
				}
				if (env[2].equals("bx"))
				{
					return String.valueOf(playertarget.getLocation().getBlockX());
				}
				if (env[2].equals("by"))
				{
					return String.valueOf(playertarget.getLocation().getBlockY());
				}
				if (env[2].equals("bz"))
				{
					return String.valueOf(playertarget.getLocation().getBlockZ());
				}
				if (env[2].equals("x"))
				{
					return String.valueOf(playertarget.getLocation().getX());
				}
				if (env[2].equals("y"))
				{
					return String.valueOf(playertarget.getLocation().getY());
				}
				if (env[2].equals("z"))
				{
					return String.valueOf(playertarget.getLocation().getZ());
				}
			}
			if (env[0].equals("wl") | env[0].equals("w"))
			{
				if (env.length < 3)
				{
					this.errorlvl = 1;
					return null;
				}
				World worldtarget = Bukkit.getWorlds().get(0);
				if (env[0].equals("w"))
				{
					worldtarget = Bukkit.getWorld(env[1]);
				}
				if (env[0].equals("wl") & (this.block instanceof BlockCommandSender))
				{
					if (env[1].equals("cw"))
					{
						worldtarget = ((BlockCommandSender) this.block).getBlock().getWorld();
					}
				}
				if (worldtarget == null) {this.errorlvl = 1; return null;}
				String worldreturn = "";
				Location loc;
				if (env[2].equals("difficulty"))
				{
					if (worldtarget.getDifficulty() == Difficulty.PEACEFUL) {worldreturn = "0";}
					if (worldtarget.getDifficulty() == Difficulty.EASY) {worldreturn = "1";}
					if (worldtarget.getDifficulty() == Difficulty.NORMAL) {worldreturn = "2";}
					if (worldtarget.getDifficulty() == Difficulty.HARD) {worldreturn = "3";}
					return worldreturn;
				}
				if (env[2].equals("time"))
				{
					return String.valueOf(worldtarget.getTime());
				}
				if (env[2].equals("players"))
				{
					return String.valueOf(worldtarget.getPlayers().size());
				}
				if (env[2].equals("type"))
				{
					if (worldtarget.getWorldType() == WorldType.NORMAL) {worldreturn = "0";}
					if (worldtarget.getWorldType() == WorldType.FLAT) {worldreturn = "1";}
					if (worldtarget.getWorldType() == WorldType.LARGE_BIOMES) {worldreturn = "2";}
					return worldreturn;
				}
				if (env[2].equals("weather"))
				{
					if ((!worldtarget.hasStorm()) & (!worldtarget.isThundering())) {worldreturn = "0";}
					if (worldtarget.hasStorm() & (!worldtarget.isThundering())) {worldreturn = "1";}
					if (worldtarget.hasStorm() & worldtarget.isThundering()) {worldreturn = "2";}
					if ((!worldtarget.hasStorm()) & worldtarget.isThundering()) {worldreturn = "3";}
					return worldreturn;
				}
				if (env[2].equals("seed"))
				{
					return String.valueOf(worldtarget.getSeed());
				}
				if (env[2].equals("blocktype"))
				{
					if (env.length != 6)
					{
						this.errorlvl = 1;
						return null;
					}
					try {
						if (this.block instanceof BlockCommandSender)
						{
							loc = new Location(((BlockCommandSender) this.block).getBlock().getWorld(), Double.valueOf(env[3]), Double.valueOf(env[4]), Double.valueOf(env[5]));
						}
						else
						{
							loc = new Location(Bukkit.getWorlds().get(0), Double.valueOf(env[3]), Double.valueOf(env[4]), Double.valueOf(env[5]));
						}
					} catch(NumberFormatException e) {
						this.errorlvl = 1;
						return null;
					}
					return String.valueOf(worldtarget.getBlockAt(loc).getTypeId());
				}
				if (env[2].equals("blockdata"))
				{
					if (env.length < 6)
					{
						this.errorlvl = 1;
						return null;
					}
					try {
						if (this.block instanceof BlockCommandSender)
						{
							loc = new Location(((BlockCommandSender) this.block).getBlock().getWorld(), Double.valueOf(env[3]), Double.valueOf(env[4]), Double.valueOf(env[5]));
						}
						else
						{
							loc = new Location(Bukkit.getWorlds().get(0), Double.valueOf(env[3]), Double.valueOf(env[4]), Double.valueOf(env[5]));
						}
					} catch(NumberFormatException e) {
						this.errorlvl = 1;
						return null;
					}
					return String.valueOf(worldtarget.getBlockAt(loc).getData());
				}
			}
		}
		else if (str.startsWith("_"))
		{
			return CommandBlockScripting.savedvarmap.get(str);
		}
		else if (str.startsWith("$"))
		{
			return CommandBlockScripting.globalvarmap.get(str);
		}
		else
		{
			return varmap.get(str);
		}
		this.errorlvl = 1;
		return null;
	}

	private String FirstArg(String str)
	{
		for (int ind = 0; ind < str.length(); ind ++)
		{
			if (str.charAt(ind) == ' ')
			{
				if (ind == (str.length() - 1))
				{
					this.errorlvl = 1;
					return "";
				}
				return str.substring(ind + 1);
			}
		}
		return "";
	}

	protected Thread getThread()
	{
		return Thread.currentThread();
	}

	private CommandBlockScripting server;
	private CommandSender block;
	private String script_str;
	private List<String> script_arr;
	private HashMap<String, String> varmap;
	private HashMap<String, Integer> labelmap;
	private boolean interrupted;
	private int iteratorindex;
	private int errorlvl;
}
