package com.gmail.brandonli2010.CommandBlockScripting;

public class ScriptVariable {

	protected ScriptVariable(String val)
	{
		this.value = val;
	}

	String value;

	protected static ScriptVariable performOp(char op, ScriptVariable b, ScriptVariable a) throws IllegalArgumentException, NullPointerException
	{
		if ((a.value == null) | (b.value == null))
		{
			throw new NullPointerException();
		}
		int type = ScriptVariable.typeof(a.value, b.value);
		switch(op)
		{
		case '+':
			if (type == 1)
			{
				return new ScriptVariable(String.valueOf(Integer.valueOf(a.value) + Integer.valueOf(b.value)));
			}
			if (type == 2)
			{
				return new ScriptVariable(String.valueOf(Double.valueOf(a.value) + Double.valueOf(b.value)));
			}
			if (type == 3)
			{
				return new ScriptVariable("");
			}
		case '-':
			if (type == 1)
			{
				return new ScriptVariable(String.valueOf(Integer.valueOf(a.value) - Integer.valueOf(b.value)));
			}
			if (type == 2)
			{
				return new ScriptVariable(String.valueOf(Double.valueOf(a.value) - Double.valueOf(b.value)));
			}
			if (type == 3)
			{
				return new ScriptVariable("");
			}
		case '*':
			if (type == 1)
			{
				return new ScriptVariable(String.valueOf(Integer.valueOf(a.value) * Integer.valueOf(b.value)));
			}
			if (type == 2)
			{
				return new ScriptVariable(String.valueOf(Double.valueOf(a.value) * Double.valueOf(b.value)));
			}
			if (type == 3)
			{
				return new ScriptVariable("");
			}
		case '/':
			if (type == 1)
			{
				return new ScriptVariable(String.valueOf(Integer.valueOf(a.value) / Integer.valueOf(b.value)));
			}
			if (type == 2)
			{
				return new ScriptVariable(String.valueOf(Double.valueOf(a.value) / Double.valueOf(b.value)));
			}
			if (type == 3)
			{
				return new ScriptVariable("");
			}
		case '%':
			if (type == 1)
			{
				return new ScriptVariable(String.valueOf(Integer.valueOf(a.value) % Integer.valueOf(b.value)));
			}
			if (type == 2)
			{
				return new ScriptVariable(String.valueOf(Double.valueOf(a.value) % Double.valueOf(b.value)));
			}
			if (type == 3)
			{
				return new ScriptVariable("");
			}
		case '^':
			if (type == 1)
			{
				return new ScriptVariable(String.valueOf(Integer.valueOf(a.value) ^ Integer.valueOf(b.value)));
			}
			if (type == 2)
			{
				return new ScriptVariable(String.valueOf(Math.pow(Double.valueOf(a.value), Double.valueOf(b.value))));
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
		case '~':
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
		case '$':
			if (a.value.matches(b.value))
			{return new ScriptVariable("1");}
			else {return new ScriptVariable("0");}
		case '@':
			try {
				return new ScriptVariable(String.valueOf(a.value.charAt(Integer.valueOf(b.value))));
			} catch (NumberFormatException e) {
				return new ScriptVariable("");
			}
		case '_':
			return new ScriptVariable(a.value + b.value);
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
		case '?':
			if (a.value.contains("."))
			{
				int decimal = a.value.indexOf('.');
				try {
					decimal = decimal + Integer.valueOf(b.value);
				} catch (NumberFormatException e) {};
				return new ScriptVariable(a.value.substring(0, decimal));
			}
			return new ScriptVariable("");
		default:
			throw new IllegalArgumentException();
		}
	}

	private static int typeof(String a, String b)
	{
		try {
			Integer.valueOf(a);
			Integer.valueOf(b);
			return 1;
		} catch (NumberFormatException e){ }
		try {
			Double.valueOf(a);
			Double.valueOf(b);
			return 2;
		} catch (NumberFormatException e){ }
		return 0;
	}
}
