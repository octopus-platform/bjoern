package outputModules;

import structures.BasicBlock;
import structures.Function;

public interface OutputModule
{
	public void initialize();

	public void finish();

	public void writeFunction(Function function);

	public void writeBasicBlock(BasicBlock block);

}
