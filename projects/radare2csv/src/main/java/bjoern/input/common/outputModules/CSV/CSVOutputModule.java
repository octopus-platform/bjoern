package bjoern.input.common.outputModules.CSV;

import bjoern.input.common.outputModules.OutputModule;
import bjoern.structures.Node;
import bjoern.structures.edges.DirectedEdge;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CSVOutputModule implements OutputModule
{

	@Override
	public void initialize(String outputDir) throws IOException
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
		CSVWriter.writeNode(node);
	}

	@Override
	public void writeNodeNoReplace(Node node)
	{
		CSVWriter.writeNoReplaceNode(node);
	}

	@Override
	public void writeEdge(DirectedEdge edge)
	{
		CSVWriter.writeEdge(edge);
	}

}
