package bjoern.plugins.vsa.transformer;

import bjoern.pluginlib.structures.Instruction;
import bjoern.plugins.vsa.domain.AbstractEnvironment;

public abstract class Transformer
{
	public abstract AbstractEnvironment transform(Instruction instruction, AbstractEnvironment env);
}
