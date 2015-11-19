package exporters.outputModules;

import exporters.structures.BasicBlock;
import exporters.structures.Function;

public interface OutputModule
{
	public void initialize(String outputDir);

	public void clearCache();

	public void finish();

	public void writeFunctionInfo(Function function);

	public void writeReferencesToFunction(Function function);

	public void writeFunctionContent(Function function);

	public void writeBasicBlock(BasicBlock block);

	public void writeUnresolvedContentEdges(Function function);

}
