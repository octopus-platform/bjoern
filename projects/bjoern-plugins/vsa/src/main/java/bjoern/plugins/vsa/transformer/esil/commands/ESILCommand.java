package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.plugins.vsa.domain.AbstractEnvironment;
import bjoern.plugins.vsa.transformer.esil.stack.ESILStack;

public interface ESILCommand
{
	void execute(AbstractEnvironment env, ESILStack stack);
}
