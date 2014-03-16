package com.gmail.brandonli2010.CommandBlockScripting;

public class ScriptVariable {
	protected ScriptVariable(String val)
	{
		this.value = val;
	}
	String value;
	protected static ScriptVariable performOp(char op, ScriptVariable b, ScriptVariable a)
	{
		if ((a.value == null) | (b.value == null))
		{
			return new ScriptVariable("");
		}
		if (a.value.equals("") | b.value.equals(""))
		{
			return new ScriptVariable("");
		}
		int type = 0;
		Object typecheck = ScriptVariable.typeof(a.value, b.value);
		if (typecheck instanceof Integer)
		{
			type = 1;
		}
		if (typecheck instanceof Double)
		{
			type = 2;
		}
		if (typecheck instanceof String)
		{
			type = 3;
		}
		switch(op)
		{
		case '+':
			if (type == 1)
			{
				return new ScriptVariable((Integer.valueOf(a.value) + Integer.valueOf(b.value)) + "");
			}
			if (type == 2)
			{
				return new ScriptVariable((Double.valueOf(a.value) + Double.valueOf(b.value)) + "");
			}
			if (type == 3)
			{
				return new ScriptVariable("");
			}
		case '-':
			if (type == 1)
			{
				return new ScriptVariable((Integer.valueOf(a.value) - Integer.valueOf(b.value)) + "");
			}
			if (type == 2)
			{
				return new ScriptVariable((Double.valueOf(a.value) - Double.valueOf(b.value)) + "");
			}
			if (type == 3)
			{
				return new ScriptVariable("");
			}
		case '*':
			if (type == 1)
			{
				return new ScriptVariable((Integer.valueOf(a.value) * Integer.valueOf(b.value)) + "");
			}
			if (type == 2)
			{
				return new ScriptVariable((Double.valueOf(a.value) * Double.valueOf(b.value)) + "");
			}
			if (type == 3)
			{
				return new ScriptVariable("");
			}
		case '/':
			if (type == 1)
			{
				return new ScriptVariable((Integer.valueOf(a.value) / Integer.valueOf(b.value)) + "");
			}
			if (type == 2)
			{
				return new ScriptVariable((Double.valueOf(a.value) / Double.valueOf(b.value)) + "");
			}
			if (type == 3)
			{
				return new ScriptVariable("");
			}
		case '&':
			if (type == 1)
			{
				if ((Integer.valueOf(a.value) != 0) & (Integer.valueOf(b.value) != 0))
				{ return new ScriptVariable("1"); }
				else { return new ScriptVariable("0"); }
			}
			if (type == 2)
			{
				if ((Double.valueOf(a.value) != 0.0) & (Double.valueOf(b.value) != 0.0))
				{ return new ScriptVariable("1"); }
				else { return new ScriptVariable("0"); }
			}
			if (type == 3)
			{
				if ((!a.value.equals("0")) & (!b.value.equals("0")))
				{ return new ScriptVariable("1"); }
				else { return new ScriptVariable("0"); }
			}
		case '|':
			if (type == 1)
			{
				if ((Integer.valueOf(a.value) != 0) | (Integer.valueOf(b.value) != 0))
				{ return new ScriptVariable("1"); }
				else { return new ScriptVariable("0"); }
			}
			if (type == 2)
			{
				if ((Double.valueOf(a.value) != 0.0) | (Double.valueOf(b.value) != 0.0))
				{ return new ScriptVariable("1"); }
				else { return new ScriptVariable("0"); }
			}
			if (type == 3)
			{
				if ((!a.value.equals("0")) | (!b.value.equals("0")))
				{ return new ScriptVariable("1"); }
				else { return new ScriptVariable("0"); }
			}
		case '^':
			if (type == 1)
			{
				if ((Integer.valueOf(a.value) != 0) ^ (Integer.valueOf(b.value) != 0))
				{ return new ScriptVariable("1"); }
				else { return new ScriptVariable("0"); }
			}
			if (type == 2)
			{
				if ((Double.valueOf(a.value) != 0.0) ^ (Double.valueOf(b.value) != 0.0))
				{ return new ScriptVariable("1"); }
				else { return new ScriptVariable("0"); }
			}
			if (type == 3)
			{
				if ((!a.value.equals("0")) ^ (!b.value.equals("0")))
				{ return new ScriptVariable("1"); }
				else { return new ScriptVariable("0"); }
			}
		case '=':
			if (type == 1)
			{
				if (Integer.valueOf(a.value) == Integer.valueOf(b.value))
				{ return new ScriptVariable("1"); }
				else { return new ScriptVariable("0"); }
			}
			if (type == 2)
			{
				if (Double.valueOf(a.value) == Double.valueOf(b.value))
				{ return new ScriptVariable("1"); }
				else { return new ScriptVariable("0"); }
			}
			if (type == 3)
			{
				if (a.value.equals(b.value))
				{ return new ScriptVariable("1"); }
				else { return new ScriptVariable("0"); }
			}
		case '!':
			if (type == 1)
			{
				if (Integer.valueOf(a.value) != Integer.valueOf(b.value))
				{ return new ScriptVariable("1"); }
				else { return new ScriptVariable("0"); }
			}
			if (type == 2)
			{
				if (Double.valueOf(a.value) != Double.valueOf(b.value))
				{ return new ScriptVariable("1"); }
				else { return new ScriptVariable("0"); }
			}
			if (type == 3)
			{
				if (!a.value.equals(b.value))
				{ return new ScriptVariable("1"); }
				else { return new ScriptVariable("0"); }
			}
		case '>':
			if (type == 1)
			{
				if (Integer.valueOf(a.value) > Integer.valueOf(b.value))
				{ return new ScriptVariable("1"); }
				else { return new ScriptVariable("0"); }
			}
			if (type == 2)
			{
				if (Double.valueOf(a.value) > Double.valueOf(b.value))
				{ return new ScriptVariable("1"); }
				else { return new ScriptVariable("0"); }
			}
			if (type == 3)
			{
				return new ScriptVariable("");
			}
		case '<':
			if (type == 1)
			{
				if (Integer.valueOf(a.value) < Integer.valueOf(b.value))
				{ return new ScriptVariable("1"); }
				else { return new ScriptVariable("0"); }
			}
			if (type == 2)
			{
				if (Double.valueOf(a.value) < Double.valueOf(b.value))
				{ return new ScriptVariable("1"); }
				else { return new ScriptVariable("0"); }
			}
			if (type == 3)
			{
				return new ScriptVariable("");
			}
		case '"':
			if (a.value.matches(b.value))
			{return new ScriptVariable("1");}
			else {return new ScriptVariable("0");}
		default:
			return new ScriptVariable("");
		}
	}
	private static Object typeof(String a, String b)
	{
		if ((a == null) | (b == null))
		{
			return "";
		}
		try {
			Integer.valueOf(a);
			Integer.valueOf(b);
			return new Integer(0);
		} catch (NumberFormatException e){ }
		try {
			Double.valueOf(a);
			Double.valueOf(b);
			return new Double(0.0);
		} catch (NumberFormatException e){ }
		return new String("");
	}
}
