package exporters.inputModules;

import java.util.List;

import exporters.structures.Function;


public interface InputModule
{
	public void initialize(String filename);

	public List<Function> getFunctions();

	public void initializeFunctionContents(Function function);

}
