package bjoern.input.common.outputModules;

import bjoern.structures.Node;
import bjoern.structures.edges.DirectedEdge;

import java.io.IOException;

public interface OutputModule
{

	void initialize(String outputDir) throws IOException;

	void finish();

	void writeNode(Node node);

	void writeNodeNoReplace(Node node);

	void writeEdge(DirectedEdge edge);

}
