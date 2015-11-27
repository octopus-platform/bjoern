package server.components.gremlinShell;

import java.util.Set;

import com.tinkerpop.gremlin.groovy.Gremlin;

import groovy.lang.Script;

public abstract class BjoernScriptBase extends Script
{

	public Set<String> listSteps()
	{
		return Gremlin.getStepNames();
	}

	public Set<String> listSteps(String prefix)
	{
		Set<String> steps = listSteps();
		steps.removeIf(step -> !step.startsWith(prefix));
		return steps;
	}

	public Set listVariables()
	{
		return getBinding().getVariables().keySet();
	}

}