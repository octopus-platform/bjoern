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

	static List<Function> functions;

	public static void main(String[] args)
	{

		inputModule.initialize("/bin/ls");
		outputModule.initialize();

		loadAndOutputFunctionInfo();
		loadAndOutputFunctionContent();

		outputModule.finish();
	}

	private static void loadAndOutputFunctionInfo()
	{
		functions = inputModule.getFunctions();
		// TODO: remove current rudimentary function import code, and
		// reimplement a more thorough importer here.
	}

	private static void loadAndOutputFunctionContent()
	{
		for (Function function : functions)
		{
			NodeStore.clearCache();
			inputModule.initializeFunctionContents(function);
			processFunction(function);
		}

	}

	private static void processFunction(Function function)
	{
		if (function == null)
			return;
		outputModule.writeFunction(function);
	}

}
