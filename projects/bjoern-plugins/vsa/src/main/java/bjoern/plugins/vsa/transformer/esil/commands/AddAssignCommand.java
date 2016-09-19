package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.pluginlib.radare.emulation.esil.ESILKeyword;

public class AddAssignCommand extends CompoundAssignCommand
{
	public AddAssignCommand()
	{
		super(ESILCommandFactory.getCommand(ESILKeyword.ADD));
	}
}

