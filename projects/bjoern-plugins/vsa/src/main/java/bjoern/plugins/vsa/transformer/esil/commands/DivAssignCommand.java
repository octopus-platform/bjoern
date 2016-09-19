package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.pluginlib.radare.emulation.esil.ESILKeyword;

public class DivAssignCommand extends CompoundAssignCommand
{
	public DivAssignCommand()
	{
		super(ESILCommandFactory.getCommand(ESILKeyword.DIV));
	}
}
