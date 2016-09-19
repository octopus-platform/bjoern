package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.pluginlib.radare.emulation.esil.ESILKeyword;

public class IncAssignCommand extends CompoundAssignCommand
{
	public IncAssignCommand()
	{
		super(ESILCommandFactory.getCommand(ESILKeyword.INC));
	}
}
