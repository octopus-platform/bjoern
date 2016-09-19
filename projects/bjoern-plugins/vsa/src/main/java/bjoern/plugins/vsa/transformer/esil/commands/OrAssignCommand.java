package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.pluginlib.radare.emulation.esil.ESILKeyword;

public class OrAssignCommand extends CompoundAssignCommand
{
	public OrAssignCommand()
	{
		super(ESILCommandFactory.getCommand(ESILKeyword.OR));
	}
}
