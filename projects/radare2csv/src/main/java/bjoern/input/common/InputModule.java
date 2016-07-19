package bjoern.input.common;

import java.io.IOException;
import java.util.Iterator;

import bjoern.structures.annotations.Flag;
import bjoern.structures.edges.CallRef;
import bjoern.structures.interpretations.Function;

public interface InputModule
{
	void initialize(String filename, String projectFilename) throws IOException;

	void finish(String outputDir);

	Iterator<Function> getFunctions() throws IOException;

	Iterator<Flag> getFlags() throws IOException;


	Iterator<CallRef> getCallReferences() throws IOException;

}
