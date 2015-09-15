package inputModules.radare;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exceptions.radareInput.EmptyDisassembly;

public class DisassemblyParser
{

	String lines[];
	int currentLine;

	static Pattern varAndArgPattern = Pattern
			.compile("^; (var|arg) (\\w+?) (\\w+?)[ ]+?@ (.*)$");

	public ParsedDisassembly parse(String disassembly) throws EmptyDisassembly
	{
		ParsedDisassembly retval = new ParsedDisassembly();

		initializeLines(disassembly);
		parseLines(retval);
		return retval;
	}

	private void parseLines(ParsedDisassembly retval)
	{
		String line;
		while ((line = nextLine()) != null)
		{
			if (isLineInstruction(line))
				handleInstruction(retval, line);
			else if (isLineComment(line))
				handleComment(retval, line);
		}
	}

	private boolean isLineInstruction(String line)
	{
		return line.startsWith("0x");
	}

	private boolean isLineComment(String line)
	{
		return line.startsWith(";");
	}

	private void handleComment(ParsedDisassembly retval, String line)
	{
		Matcher matcher = varAndArgPattern.matcher(line);
		if (!matcher.matches())
		{
			handleVarOrArg(matcher);
		}
	}

	private void handleVarOrArg(Matcher matcher)
	{
		String varOrArg = matcher.group(1);
		String varType = matcher.group(2);
		String varName = matcher.group(3);
		String regPlusOffset = matcher.group(4);
	}

	private void handleInstruction(ParsedDisassembly retval, String line)
	{
		// TODO
	}

	private String nextLine()
	{
		if (currentLine == lines.length)
			return null;
		return lines[currentLine++].trim();
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
