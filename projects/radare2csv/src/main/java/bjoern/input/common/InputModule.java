package bjoern.input.common;

import java.io.IOException;
import java.util.List;

import bjoern.structures.annotations.Flag;
import bjoern.structures.edges.CallRef;
import bjoern.structures.interpretations.Function;

public interface InputModule
{
	void initialize(String filename, String projectFilename) throws IOException;

	void finish(String outputDir);

	List<Function> getFunctions() throws IOException;

	List<Flag> getFlags() throws IOException;


	List<CallRef> getCallReferences() throws IOException;

}
