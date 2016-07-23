package bjoern.input.common.outputModules;

import bjoern.structures.Node;
import bjoern.structures.edges.DirectedEdge;

public interface OutputModule
{

	void initialize(String outputDir);

	void finish();

	void writeNode(Node node);

	void writeNodeNoReplace(Node node);

	void writeEdge(DirectedEdge edge);

}
