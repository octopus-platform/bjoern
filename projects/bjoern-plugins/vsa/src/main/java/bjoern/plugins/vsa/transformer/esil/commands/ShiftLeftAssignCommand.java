package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.pluginlib.radare.emulation.esil.ESILKeyword;

public class ShiftLeftAssignCommand extends CompoundAssignCommand
{
	public ShiftLeftAssignCommand()
	{
		super(ESILCommandFactory.getCommand(ESILKeyword.SHIFT_LEFT));
	}
}
