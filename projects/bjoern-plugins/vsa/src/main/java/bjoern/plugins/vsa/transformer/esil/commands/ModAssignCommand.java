package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.pluginlib.radare.emulation.esil.ESILKeyword;

public class ModAssignCommand extends CompoundAssignCommand
{
	public ModAssignCommand()
	{
		super(ESILCommandFactory.getCommand(ESILKeyword.MOD));
	}
}
