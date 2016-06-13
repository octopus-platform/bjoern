package bjoern.pluginlib.radare.emulation.esil;

public class ESILTokenEvaluator
{

	public long parseNumericConstant(String token)
	{
		if (token.startsWith("0x"))
		{
			return Long.parseUnsignedLong(token.substring(2), 16);
		}
		return Long.parseLong(token, 10);
	}

	public boolean isNumericConstant(String token)
	{
		if (token.startsWith("0x"))
		{
			return isHexadecimalConstant(token.substring(2));
		} else
		{
			return isDecimalConstant(token);
		}
	}

	public boolean isHexadecimalConstant(String token)
	{
		if (token.equals(""))
		{
			return false;
		}
		for (Character c : token.toCharArray())
		{
			if (Character.digit(c, 16) == -1)
			{
				return false;
			}
		}
		return true;
	}

	public boolean isDecimalConstant(String token)
	{
		if (token.equals(""))
		{
			return false;
		}
		if (token.startsWith(("-")) && token.length() > 1)
		{
			token = token.substring(1);
		}
		for (Character c : token.toCharArray())
		{
			if (Character.digit(c, 10) == -1)
			{
				return false;
			}
		}
		return true;
	}

	public boolean isFlag(String token)
	{
		return isInternalFlag(token) || (token.length() == 2 && token.endsWith("f"));
	}

	private boolean isInternalFlag(String token)
	{
		return token.startsWith("$");
	}

	public boolean isRegister(String token)
	{
		return !isFlag(token);
	}

	public boolean isEsilKeyword(String token)
	{
		return ESILKeyword.fromString(token) != null;
	}

}
