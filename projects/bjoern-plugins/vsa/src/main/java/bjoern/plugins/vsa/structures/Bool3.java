package bjoern.plugins.vsa.structures;

public enum Bool3
{
	TRUE,
	FALSE,
	MAYBE;

	public Bool3 and(Bool3 bool)
	{
		if (this == TRUE)
		{
			return bool;
		} else if (this == FALSE)
		{
			return FALSE;
		} else
		{
			if (bool == FALSE)
			{
				return MAYBE;
			} else
			{
				return bool;
			}
		}
	}

	public Bool3 or(Bool3 bool)
	{
		if (this == TRUE)
		{
			return TRUE;
		} else if (this == FALSE)
		{
			return bool;
		} else
		{
			if (bool == FALSE)
			{
				return MAYBE;
			} else
			{
				return bool;
			}
		}
	}

	public Bool3 not()
	{
		if (this == TRUE)
		{
			return FALSE;
		} else if (this == FALSE)
		{
			return TRUE;
		} else
		{
			return MAYBE;
		}
	}

	public Bool3 xor(Bool3 bool)
	{
		if (this == TRUE)
		{
			return bool.not();
		} else if (this == FALSE)
		{
			return bool;
		} else
		{
			return MAYBE;
		}
	}

	public Bool3 join(Bool3 bool)
	{
		if (this == TRUE)
		{
			if (bool == FALSE)
			{
				return MAYBE;
			} else
			{
				return bool;
			}
		} else if (this == FALSE)
		{
			if (bool == TRUE)
			{
				return MAYBE;
			} else
			{
				return bool;
			}
		} else
		{
			return MAYBE;
		}
	}
}
