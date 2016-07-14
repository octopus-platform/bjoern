package bjoern.input.common.outputModules;

import bjoern.nodeStore.Node;
import bjoern.structures.annotations.Flag;
import bjoern.structures.edges.DirectedEdge;
import bjoern.structures.interpretations.Function;

public interface OutputModule
{

	void initialize(String outputDir);

	void finish();

	void writeNode(Node node);

	void writeNodeNoReplace(Node node);

	void writeEdge(DirectedEdge edge);

}
