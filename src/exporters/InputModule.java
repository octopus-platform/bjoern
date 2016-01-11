package exporters;

import java.io.IOException;
import java.util.List;

import exporters.structures.annotations.Flag;
import exporters.structures.edges.DirectedEdge;
import exporters.structures.interpretations.Function;

public interface InputModule
{
	public void initialize(String filename) throws IOException;

	public List<Function> getFunctions() throws IOException;

	public List<Flag> getFlags() throws IOException;

	public void initializeFunctionContents(Function function)
			throws IOException;

	public void finish();

	public List<DirectedEdge> getCrossReferences() throws IOException;

}
