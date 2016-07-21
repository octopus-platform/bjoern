package bjoern.r2interface;

import bjoern.r2interface.architectures.Architecture;
import bjoern.r2interface.architectures.X64Architecture;
import bjoern.r2interface.exceptions.InvalidRadareFunctionException;
import bjoern.structures.annotations.Flag;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Radare
{
	private R2Pipe r2Pipe;

	private static final Logger logger = LoggerFactory.getLogger(Radare.class);

	public void loadBinary(String filename) throws IOException
	{
		r2Pipe = new R2Pipe(filename);
		setRadareOptions();
	}

	public void analyzeBinary() throws IOException
	{
		r2Pipe.cmd("aaa");
	}

	private void setRadareVariable(String group, String variable, String value) throws IOException
	{
		r2Pipe.cmd("e " + group + "." + variable + " = " + value);
	}

	private String getRadareVariable(String group, String variable) throws IOException
	{
		return r2Pipe.cmd("e " + group + " " + variable);
	}

	private void setASMVariable(String variable, String value) throws IOException
	{
		setRadareVariable("asm", variable, value);
	}

	private String getASMVariable(String variable) throws IOException
	{
		return getRadareVariable("asm", variable);
	}

	private void setSCRVariable(String variable, String value) throws IOException
	{
		setRadareVariable("scr", variable, value);
	}

	private void setRadareOptions() throws IOException
	{
		setSCRVariable("color", "false");
		setASMVariable("bytes", "false");
		setASMVariable("lines", "false");
		setASMVariable("fcnlines", "false");
		setASMVariable("xrefs", "false");
		setASMVariable("lbytes", "false");
		setASMVariable("indentspace", "0");
	}

	public void saveProject(String projectFilename) throws IOException
	{
		r2Pipe.cmd("Ps " + projectFilename);
	}

	public void loadProject(String projectFilename) throws IOException
	{
		r2Pipe.cmd("Po " + projectFilename);
		// This is a workaround: for some reason, when loading a
		// project, r2 goes out of quiet mode
		setSCRVariable("interactive", "false");
		setSCRVariable("prompt", "false");
		setSCRVariable("color", "false");
	}

	public Architecture getArchitecture() throws IOException
	{
		String arch = getASMVariable("arch");
		String bits = getASMVariable("bits");

		// TODO: Actually take a look at `arch` and `bits` to decide which architecture to return
		return new X64Architecture();
	}

	public JSONArray getJSONFunctions() throws IOException
	{
		String str = r2Pipe.cmd("aflj");
		return new JSONArray(str);
	}

	public JSONObject getJSONFunctionContentAt(Long addr)
			throws InvalidRadareFunctionException, IOException
	{
		String jsonStr = r2Pipe.cmd("agj 0x" + Long.toHexString(addr));

		JSONArray jsonArray;
		try
		{
			jsonArray = new JSONArray(jsonStr);
		} catch (JSONException ex)
		{
			return null;
		}

		if (jsonArray.length() != 1)
			throw new InvalidRadareFunctionException("empty function at address 0x" + Long.toHexString(addr));

		JSONObject functionJSONObject = jsonArray.getJSONObject(0);
		Long addressOfReceivedFunction = functionJSONObject.getLong("offset");
		if (!addr.equals(addressOfReceivedFunction))
		{
			throw new InvalidRadareFunctionException("requested function content for address 0x"
					+ Long.toHexString(addr)
					+ " but received function content for address 0x"
					+ Long.toHexString(addressOfReceivedFunction));
		}
		return functionJSONObject;
	}

	public void shutdown() throws Exception
	{
		r2Pipe.quit();
	}

	public void askForFlags() throws IOException
	{
		r2Pipe.cmdNoResponse("f");
	}

	public Flag getNextFlag() throws IOException
	{
		String nextLine = r2Pipe.readNextLine();
		if (nextLine.length() == 0 || nextLine.endsWith("\0"))
			return null;
		return createFlagFromLine(nextLine);
	}

	private Flag createFlagFromLine(String line)
	{
		String[] parts = line.split(" ");
		long addr = Long.decode(parts[0]);
		Flag flag = new Flag(addr);
		if (parts.length != 3)
		{
			logger.info("Returning empty flag for line: {}", line);
			return flag;
		}

		int length = Integer.parseInt(parts[1]);
		String value = parts[2];
		flag.setLength(length);
		flag.setValue(value);

		return flag;
	}

	public JSONArray getReferences() throws IOException
	{
		askForCrossReferences();
		JSONArray answer = new JSONArray();
		while (true)
		{
			String line = r2Pipe.readNextLine();
			if (line.length() == 0 || line.endsWith("\0"))
				break;
			answer.put(parseReferenceLine(line));
		}
		return answer;
	}

	private JSONObject parseReferenceLine(String line)
	{
		// line format "type0.type1.type2.source=destination0,destination1,...,destinationN
		JSONObject answer = new JSONObject();
		String[] split = line.split("\\.");
		answer.put("type", split[0] + "." + split[1] + "." + split[2]);
		String[] addresses = split[3].split("=");
		answer.put("address", Long.decode(addresses[0]));
		answer.put("locations", new JSONArray(Arrays.stream(addresses[1].split(",")).map(Long::decode).toArray()));
		return answer;
	}

	private void askForCrossReferences() throws IOException
	{
		r2Pipe.cmdNoResponse("ax");
		// skip first line
		r2Pipe.readNextLine();
	}

	public void enableEsil() throws IOException
	{
		setASMVariable("esil", "true");
	}

	public void disableEsil() throws IOException
	{
		setASMVariable("esil", "false");
	}

	public void resetEsilState() throws IOException
	{
		r2Pipe.cmd("ar0");
		r2Pipe.cmd("aei");
		r2Pipe.cmd("aeim");
	}

	public String runEsilCode(String esilCode) throws IOException
	{
		return r2Pipe.cmd(String.format("\"ae %s\"", esilCode));
	}

	public String getRegisterValue(String registerStr) throws IOException
	{
		return r2Pipe.cmd(String.format("ar %s", registerStr));
	}

	public List<String> getRegistersWritten(String addr) throws IOException
	{
		String cmd = "aeaw @ " + addr;
		return cmdAndSplitResultAtWhitespace(cmd);
	}

	public List<String> getRegistersRead(String addr) throws IOException
	{
		String cmd = "aear @ " + addr;
		return cmdAndSplitResultAtWhitespace(cmd);
	}


	private List<String> cmdAndSplitResultAtWhitespace(String cmd) throws IOException
	{
		String registers = r2Pipe.cmd(cmd).trim();
		if (registers.length() == 0)
			return new LinkedList<>();

		String[] registersAr = registers.split(" ");
		return Arrays.asList(registersAr);
	}

}
