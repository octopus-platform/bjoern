package bjoern.plugins.vsa.transformer;

import bjoern.plugins.vsa.domain.AbstractEnvironment;

public interface Transformer
{
	AbstractEnvironment transform(String esilCode, AbstractEnvironment env);
}
