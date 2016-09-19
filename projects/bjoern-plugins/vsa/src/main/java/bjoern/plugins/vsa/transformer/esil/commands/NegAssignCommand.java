package bjoern.plugins.vsa.transformer.esil.commands;

import bjoern.pluginlib.radare.emulation.esil.ESILKeyword;

public class NegAssignCommand extends CompoundAssignCommand
{
	public NegAssignCommand()
	{
		super(ESILCommandFactory.getCommand(ESILKeyword.NEG));
	}
}
