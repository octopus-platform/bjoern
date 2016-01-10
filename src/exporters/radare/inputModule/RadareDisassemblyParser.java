package exporters.radare.inputModule;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exporters.radare.inputModule.exceptions.EmptyDisassembly;
import exporters.structures.annotations.VariableOrArgument;
import exporters.structures.interpretations.Disassembly;
import exporters.structures.interpretations.DisassemblyLine;

public class RadareDisassemblyParser
{

	String lines[];
	int currentLine;

	static Pattern varAndArgPattern = Pattern
			.compile("^; (var|arg) (\\w+?) (\\w+?)[ ]+?@ (.*)$");

	static Pattern instructionPattern = Pattern
			.compile("0x(.*?)[ ]+?(.*?)( ;(.*))?$");

	public Disassembly parse(String disassembly, long functionAddr) throws EmptyDisassembly
	{
		Disassembly retval = new Disassembly(functionAddr);

		initializeLines(disassembly);
		parseLines(retval);
		return retval;
	}

	private void parseLines(Disassembly retval)
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

	private void handleComment(Disassembly retval, String line)
	{
		Matcher matcher = varAndArgPattern.matcher(line);
		if (matcher.matches())
		{
			handleVarOrArg(retval, matcher);
		}
	}

	private void handleVarOrArg(Disassembly retval, Matcher matcher)
	{
		VariableOrArgument parsedVarOrArg = new VariableOrArgument();

		parsedVarOrArg.setType(matcher.group(1));
		parsedVarOrArg.setVarType(matcher.group(2));
		parsedVarOrArg.setName(matcher.group(3));
		parsedVarOrArg.setRegPlusOffset(matcher.group(4));

		retval.addVarOrArg(parsedVarOrArg);
	}

	private void handleInstruction(Disassembly retval, String line)
	{
		Matcher matcher = instructionPattern.matcher(line);
		if (!matcher.matches())
			return;

		Long addr = new BigInteger(matcher.group(1), 16).longValue();
		String instruction = matcher.group(2);
		String comment = matcher.group(3);

		DisassemblyLine disasmLine = new DisassemblyLine();
		disasmLine.setAddr(addr);
		disasmLine.setInstruction(instruction.trim());
		if (comment != null)
			disasmLine.setComment(comment.trim());

		retval.addLine(disasmLine);
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
