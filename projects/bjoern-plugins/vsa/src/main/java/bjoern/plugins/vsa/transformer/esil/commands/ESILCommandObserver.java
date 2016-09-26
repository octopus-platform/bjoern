package bjoern.plugins.vsa.transformer.esil.commands;

public interface ESILCommandObserver
{
	void beforeExecution(ESILCommand command);

	void afterExecution(ESILCommand command);
}
