package bjoern.input.common.outputModules;

import bjoern.nodeStore.Node;
import bjoern.structures.annotations.Flag;
import bjoern.structures.edges.DirectedEdge;
import bjoern.structures.interpretations.BasicBlock;
import bjoern.structures.interpretations.Function;

public interface OutputModule
{

	void writeNode(Node node);

	void initialize(String outputDir);

	void finish();

	void writeFunctionNodes(Function function);

	void writeReferencesToFunction(Function function);

	void writeFunctionContent(Function function);

	void writeBasicBlock(BasicBlock block);

	void writeFlag(Flag flag);

	void attachFlagsToRootNodes(Flag flag);

	void writeCrossReference(DirectedEdge xref);
}
