package bjoern.plugins.vsa;

import bjoern.pluginlib.LookupOperations;
import bjoern.pluginlib.Traversals;
import bjoern.pluginlib.plugintypes.OrientGraphConnectionPlugin;
import bjoern.pluginlib.structures.Function;
import bjoern.pluginlib.structures.Instruction;
import bjoern.plugins.vsa.domain.AbstractEnvironment;
import bjoern.plugins.vsa.domain.ValueSet;
import bjoern.plugins.vsa.domain.region.LocalRegion;
import bjoern.plugins.vsa.structures.DataWidth;
import bjoern.plugins.vsa.structures.StridedInterval;
import bjoern.plugins.vsa.transformer.Transformer;
import bjoern.plugins.vsa.transformer.esil.ESILTransformationException;
import bjoern.plugins.vsa.transformer.esil.ESILTransformer;
import bjoern.structures.BjoernEdgeProperties;
import bjoern.structures.BjoernNodeProperties;
import bjoern.structures.edges.EdgeTypes;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import java.util.*;

public class VSAPlugin extends OrientGraphConnectionPlugin
{

	private Map<Instruction, AbstractEnvironment> assignment;
	private Map<Instruction, Integer> mycounter;

	@Override
	public void execute() throws Exception
	{
		OrientGraphNoTx graph = orientConnector.getNoTxGraphInstance();
		Iterable<Vertex> functions = LookupOperations.getAllFunctions(graph);
		for (Vertex v : functions)
		{
			Function function = new Function(v);
			getLogger().info(function.toString());
			performIntraProceduralVSA(function);
		}
		graph.shutdown();
	}

	private void performIntraProceduralVSA(Function function)
	{
		assignment = new HashMap<>();
		mycounter = new HashMap<>();
		Instruction entry = Traversals.functionToEntryInstruction(function);
		if (entry == null)
		{
			return;
		}

		Queue<Instruction> worklist = new LinkedList<>();
		Transformer transformer = new ESILTransformer();
		initAbstractEnvironment(entry);
		worklist.add(entry);
		while (!worklist.isEmpty())
		{
			AbstractEnvironment out;
			Instruction n = worklist.remove();
			try
			{
				out = transformer.transform(n, getAbstractEnvironment(n));
			} catch (ESILTransformationException e)
			{
				getLogger().error(e.getMessage());
				out = new AbstractEnvironment();
			} catch (NoSuchElementException e)
			{
				getLogger().error("Invalid esil stack");
				out = new AbstractEnvironment();
			}
			List<Instruction> successors = Traversals.instructionToSuccessors(n);
			for (Instruction successor : successors)
			{
				if (getCounter(n) < getCounter(successor))
				{
					performWidening(out, getAbstractEnvironment(successor));
				}
				if (updateAbstractEnvironment(successor, out))
				{
					worklist.add(successor);
				}
			}
			incrementCounter(n);
		}

		writeResults();
	}

	private void writeResults()
	{
		for (Instruction instr : assignment.keySet())
		{
			getLogger().info(instr.getEsilCode());
			getLogger().info(assignment.get(instr).toString());
			for (Edge edge : instr.getNode().getEdges(Direction.OUT, EdgeTypes.READ))
			{
				String aloc = edge.getVertex(Direction.IN).getProperty(BjoernNodeProperties.NAME);
				if (isFlag(aloc))
				{
					edge.setProperty(BjoernEdgeProperties.VALUE,
							assignment.get(instr).getValueOfFlag(aloc).toString());
				} else
				{
					edge.setProperty(BjoernEdgeProperties.VALUE,
							assignment.get(instr).getValueSetOfRegister(aloc).toString());
				}
			}

		}

	}

	private boolean isFlag(String aloc)
	{
		return aloc.startsWith("$") || (aloc.length() == 2 && aloc.endsWith("f"));
	}

	private int getCounter(Instruction n)
	{
		if (mycounter.containsKey(n))
		{
			return mycounter.get(n);
		} else
		{
			return 0;
		}
	}

	private void incrementCounter(Instruction n)
	{
		mycounter.put(n, getCounter(n) + 1);
	}

	private boolean updateAbstractEnvironment(Instruction n, AbstractEnvironment amc)
	{
		AbstractEnvironment oldEnv = getAbstractEnvironment(n);
		if (oldEnv == null)
		{
			setAbstractEnvironment(n, amc);
			return true;
		} else
		{
			AbstractEnvironment newEnv;
			newEnv = oldEnv.union(amc);
			if (oldEnv.equals(newEnv))
			{
				return false;
			} else
			{
				setAbstractEnvironment(n, newEnv);
				return true;
			}
		}
	}

	private void performWidening(AbstractEnvironment newEnv, AbstractEnvironment oldEnv)
	{
		getLogger().info("Performing widening: " + oldEnv + " [<=>] " + newEnv);
		for (String register : newEnv.getRegisters())
		{
			newEnv.setValueSetOfRegister(register,
					oldEnv.getValueSetOfRegister(register).widen(newEnv.getValueSetOfRegister(register)));
		}
	}

	private AbstractEnvironment getAbstractEnvironment(Instruction n)
	{
		return assignment.get(n);
	}

	private void setAbstractEnvironment(Instruction n, AbstractEnvironment env)
	{
		assignment.put(n, env);
	}

	private void initAbstractEnvironment(Instruction entry)
	{
		AbstractEnvironment initState = new AbstractEnvironment();
		ValueSet valueSet;
		valueSet = ValueSet.newSingle(LocalRegion.newLocalRegion(),
				StridedInterval.getSingletonSet(0, DataWidth.R64));
		initState.setValueSetOfRegister("rsp", valueSet);

		setAbstractEnvironment(entry, initState);
	}
}
