package bjoern.input.common.outputModules;

import bjoern.structures.annotations.Flag;
import bjoern.structures.edges.DirectedEdge;
import bjoern.structures.interpretations.Function;

public interface OutputModule
{

	void initialize(String outputDir);

	void finish();

	void writeFunction(Function function);

	void writeFlag(Flag flag);

	void writeCrossReference(DirectedEdge xref);

}
