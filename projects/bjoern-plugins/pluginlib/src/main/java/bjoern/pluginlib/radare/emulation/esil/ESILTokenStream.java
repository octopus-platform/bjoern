package bjoern.pluginlib.radare.emulation.esil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ESILTokenStream {

	private String[] tokens;
	private int index = 0;

	public ESILTokenStream(String esilCode)
	{
		tokens = esilCode.split(",");
	}

	public boolean isEmpty()
	{
		return (index >= tokens.length);
	}

	public boolean hasNext()
	{
		return (index < tokens.length);
	}

	public String next()
	{
		if(isEmpty())
			return null;

		return tokens[++index];
	}

	public String getTokenAt(int i)
	{
		return tokens[i];
	}

	public int skipUntilToken(String token)
	{
		return skipUntilToken(new HashSet<String>(Arrays.asList(token)));
	}


	public int skipUntilToken(Set<String> tokens)
	{
		String t;

		while(hasNext()){
			t = next();

			if(tokens.contains(t)){
				index--;
				return index;
			}
		}

		return -1;
	}


}
