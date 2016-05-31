package bjoern.plugins.vsa.transformer.esil;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

public class ESILTokenStream {

	private Iterator<String> stream;

	public ESILTokenStream(String esilCode)
	{
		stream = (new LinkedList<>(Arrays.asList(esilCode.split(",")))).iterator();
	}

	public boolean hasNext()
	{
		return stream.hasNext();
	}

	public String next()
	{
		return stream.next();
	}

	public void skipUntilToken(String token)
	{
		String t;
		do
		{
			t = next();
		} while (!token.equals(t));
	}

}
