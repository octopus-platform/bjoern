package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.pluginlib.radare.emulation.esil.ESILKeyword;

public class XorAssignCommand extends CompoundAssignCommand
{
	public XorAssignCommand()
	{
		super(ESILCommandFactory.getCommand(ESILKeyword.XOR));
	}
}
