package exporters.outputModules;

import exporters.structures.annotations.Flag;
import exporters.structures.interpretations.BasicBlock;
import exporters.structures.interpretations.Function;

public interface OutputModule
{
	public void initialize(String outputDir);

	public void finish();

	public void writeFunctionNodes(Function function);

	public void writeReferencesToFunction(Function function);

	public void writeFunctionContent(Function function);

	public void writeBasicBlock(BasicBlock block);

	public void writeFlag(Flag flag);

	public void writeReferenceToFlag(Flag flag);

}
