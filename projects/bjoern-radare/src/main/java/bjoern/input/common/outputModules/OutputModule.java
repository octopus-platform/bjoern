package bjoern.input.common.outputModules;

import bjoern.structures.annotations.Flag;
import bjoern.structures.interpretations.BasicBlock;
import bjoern.structures.interpretations.Function;

public interface OutputModule
{
	public void initialize(String outputDir);

	public void finish();

	public void writeFunctionNodes(Function function);

	public void writeReferencesToFunction(Function function);

	public void writeFunctionContent(Function function);

	public void writeBasicBlock(BasicBlock block);

	public void writeFlag(Flag flag);

	public void attachFlagsToRootNodes(Flag flag);

}
