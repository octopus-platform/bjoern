package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.pluginlib.radare.emulation.esil.ESILKeyword;

public class AndAssignCommand extends CompoundAssignCommand
{
	public AndAssignCommand()
	{
		super(ESILCommandFactory.getCommand(ESILKeyword.AND));
	}
}
