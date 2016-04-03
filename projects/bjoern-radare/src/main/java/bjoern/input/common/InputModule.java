package bjoern.input.common;

import java.io.IOException;
import java.util.List;

import bjoern.input.common.structures.annotations.Flag;
import bjoern.input.common.structures.edges.Xref;
import bjoern.input.common.structures.interpretations.Function;

public interface InputModule
{
	public void initialize(String filename) throws IOException;

	public List<Function> getFunctions() throws IOException;

	public List<Flag> getFlags() throws IOException;

	public void initializeFunctionContents(Function function)
			throws IOException;

	public void finish();

	public List<Xref> getCrossReferences() throws IOException;

}
