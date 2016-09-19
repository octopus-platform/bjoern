package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.pluginlib.radare.emulation.esil.ESILKeyword;

public class DecAssignCommand extends CompoundAssignCommand
{
	public DecAssignCommand()
	{
		super(ESILCommandFactory.getCommand(ESILKeyword.DEC));
	}
}
