package exporters.inputModules;

import java.io.IOException;
import java.util.List;

import exporters.structures.Function;

public interface InputModule
{
	public void initialize(String filename) throws IOException;

	public List<Function> getFunctions() throws IOException;

	public void initializeFunctionContents(Function function)
			throws IOException;

	public void finish();

}
