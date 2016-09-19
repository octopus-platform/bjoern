package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.pluginlib.radare.emulation.esil.ESILKeyword;

public class ShiftRightAssignCommand extends CompoundAssignCommand
{
	public ShiftRightAssignCommand()
	{
		super(ESILCommandFactory.getCommand(ESILKeyword.SHIFT_RIGHT));
	}
}
