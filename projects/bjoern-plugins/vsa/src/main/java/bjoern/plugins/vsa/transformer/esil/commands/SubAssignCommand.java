package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.pluginlib.radare.emulation.esil.ESILKeyword;

public class SubAssignCommand extends CompoundAssignCommand
{
	public SubAssignCommand()
	{
		super(ESILCommandFactory.getCommand(ESILKeyword.SUB));
	}
}
