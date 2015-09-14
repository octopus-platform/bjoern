package outputModules;

import structures.BasicBlock;
import structures.Function;

public interface OutputModule
{
	public void initialize();

	public void clearCache();

	public void finish();

	public void writeFunctionInfo(Function function);

	public void writeFunctionContent(Function function);

	public void writeBasicBlock(BasicBlock block);

	public void writeUnresolvedContentEdges(Function function);

}
