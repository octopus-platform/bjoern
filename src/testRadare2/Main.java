package testRadare2;

import java.util.List;

import inputModules.InputModule;
import inputModules.radare.RadareInputModule;
import nodeStore.NodeStore;
import outputModules.CSV.CSVOutputModule;
import structures.Function;

public class Main
{

	static InputModule inputModule = new RadareInputModule();
	static CSVOutputModule outputModule = new CSVOutputModule();

	public static void main(String[] args)
	{

		inputModule.initialize("/bin/ls");
		outputModule.initialize();

			List<Long> funcAddresses = inputModule.getFunctionAddresses();

		outputFunctionContents(funcAddresses);

		outputModule.finish();
	}

	private static void outputFunctionContents(List<Long> funcAddresses)
	{
		for (Long addr : funcAddresses)
		{
			NodeStore.clearCache();
			processFunction(addr);
		}
	}

	private static void processFunction(Long addr)
	{
		Function function = inputModule.getFunctionAtAddress(addr);
		if (function == null)
			return;
		outputModule.writeFunction(function);
	}

}
