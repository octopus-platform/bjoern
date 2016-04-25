package bjoern.input.common;

import java.io.IOException;
import java.util.List;

import bjoern.structures.annotations.Flag;
import bjoern.structures.edges.Xref;
import bjoern.structures.interpretations.Function;

public interface InputModule
{
	public void initialize(String filename, String projectFilename) throws IOException;

	public List<Function> getFunctions() throws IOException;

	public List<Flag> getFlags() throws IOException;

	public void initializeFunctionContents(Function function)
			throws IOException;

	public void finish(String outputDir);

	public List<Xref> getCrossReferences() throws IOException;

}
