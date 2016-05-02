package bjoern.plugins.vsa.structures;

public enum Bool3
{
	TRUE,
	FALSE,
	MAYBE;

	public Bool3 and(Bool3 bool)
	{
		switch (this)
		{
			case FALSE:
				return FALSE;
			case MAYBE:
				switch (bool)
				{
					case FALSE:
						return FALSE;
					case MAYBE:
					case TRUE:
						return MAYBE;
				}
			case TRUE:
			default:
				return bool;
		}
	}

	public Bool3 or(Bool3 bool)
	{
		switch (this)
		{
			case FALSE:
				return bool;
			case MAYBE:
				switch (bool)
				{
					case FALSE:
					case MAYBE:
						return MAYBE;
					case TRUE:
						return TRUE;
				}
			case TRUE:
			default:
				return TRUE;
		}
	}

	public Bool3 not()
	{
		switch (this)
		{
			case TRUE:
				return FALSE;
			case MAYBE:
				return MAYBE;
			case FALSE:
			default:
				return TRUE;
		}
	}

	public Bool3 xor(Bool3 bool)
	{
		switch (this)
		{
			case FALSE:
				return bool;
			case MAYBE:
				return MAYBE;
			case TRUE:
			default:
				return bool.not();
		}
	}

	public Bool3 join(Bool3 bool)
	{
		switch (this)
		{
			case FALSE:
				return MAYBE.and(bool);
			case MAYBE:
				return MAYBE;
			case TRUE:
			default:
				return MAYBE.or(bool);
		}
	}
}
