package bjoern.input.common.outputModules.CSV;

import bjoern.input.common.outputModules.OutputModule;
import bjoern.structures.Node;
import bjoern.structures.edges.DirectedEdge;

import java.util.HashMap;
import java.util.Map;

public class CSVOutputModule implements OutputModule
{

	@Override
	public void initialize(String outputDir)
	{
		CSVWriter.changeOutputDir(outputDir);
	}

	@Override
	public void finish()
	{
		CSVWriter.finish();
	}

	@Override
	public void writeNode(Node node)
	{
		CSVWriter.addNode(node);
	}

	@Override
	public void writeNodeNoReplace(Node node)
	{
		CSVWriter.addNoReplaceNode(node);
	}

	@Override
	public void writeEdge(DirectedEdge edge)
	{
		String sourceKey = edge.getSourceKey().toString();
		String destKey = edge.getDestKey().toString();
		String label = edge.getType();
		Map<String, Object> properties = new HashMap<>();
		// TODO: add edge properties.
		CSVWriter.addEdge(sourceKey, destKey, properties, label);
	}

}
