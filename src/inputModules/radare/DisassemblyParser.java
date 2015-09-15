package inputModules.radare;

import exceptions.radareInput.EmptyDisassembly;

public class DisassemblyParser
{

	String lines[];
	int currentLine;

	public ParsedDisassembly parse(String disassembly) throws EmptyDisassembly
	{
		ParsedDisassembly retval = new ParsedDisassembly();

		initializeLines(disassembly);
		parseLines(retval);
		return retval;
	}

	private void parseLines(ParsedDisassembly retval)
	{
		Object thisLine;
		while ((thisLine = nextLine()) != null)
		{

		}
	}

	private Object nextLine()
	{
		if (currentLine == lines.length)
			return null;
		return lines[currentLine++];
	}

	private void initializeLines(String disassembly) throws EmptyDisassembly
	{
		currentLine = 0;
		lines = disassembly.split("\\r?\\n");
		if (lines.length == 0)
			throw new EmptyDisassembly();
		// we skip the first line as it only contains the function name.
		skipLine();
	}

	private void skipLine()
	{
		currentLine++;
	}

}
