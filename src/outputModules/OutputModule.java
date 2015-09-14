package outputModules;

import structures.BasicBlock;
import structures.Function;

public interface OutputModule
{
	public void initialize();

	public void finish();

	public void writeFunctionContent(Function function);

	public void writeBasicBlock(BasicBlock block);

	public void writeFunctionInfo(Function function);

}
