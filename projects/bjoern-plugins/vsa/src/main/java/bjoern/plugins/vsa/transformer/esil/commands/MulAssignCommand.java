package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.pluginlib.radare.emulation.esil.ESILKeyword;

public class MulAssignCommand extends CompoundAssignCommand
{
	public MulAssignCommand()
	{
		super(ESILCommandFactory.getCommand(ESILKeyword.MUL));
	}
}
