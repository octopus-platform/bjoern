package bjoern.plugins.vsa.transformer;

import bjoern.plugins.vsa.domain.AbstractEnvironment;

public abstract class Transformer
{
	public abstract AbstractEnvironment transform(String esilCode, AbstractEnvironment env);
}
