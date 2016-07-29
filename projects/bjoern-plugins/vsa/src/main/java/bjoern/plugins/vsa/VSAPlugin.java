package bjoern.plugins.vsa;

import bjoern.pluginlib.LookupOperations;
import bjoern.pluginlib.Traversals;
import bjoern.pluginlib.structures.Aloc;
import bjoern.pluginlib.structures.Function;
import bjoern.pluginlib.structures.Instruction;
import bjoern.plugins.vsa.domain.AbstractEnvironment;
import bjoern.plugins.vsa.domain.ValueSet;
import bjoern.plugins.vsa.domain.region.LocalRegion;
import bjoern.plugins.vsa.structures.Bool3;
import bjoern.plugins.vsa.structures.DataWidth;
import bjoern.plugins.vsa.structures.StridedInterval;
import bjoern.plugins.vsa.transformer.ESILTransformer;
import bjoern.plugins.vsa.transformer.Transformer;
import bjoern.plugins.vsa.transformer.esil.ESILTransformationException;
import bjoern.plugins.vsa.transformer.esil.stack.Flag;
import bjoern.plugins.vsa.transformer.esil.stack.Register;
import bjoern.structures.BjoernEdgeProperties;
import bjoern.structures.edges.EdgeTypes;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import octopus.lib.plugintypes.OrientGraphConnectionPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class VSAPlugin extends OrientGraphConnectionPlugin
{

	private Logger logger = LoggerFactory.getLogger(ESILTransformer.class);

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
			logger.info(function.toString());
			try
			{
				performIntraProceduralVSA(function);
			} catch (Exception e)
			{
				logger.error("Error for function " + function + ": " + e.getMessage());
			}
		}
		graph.shutdown();
	}

	private void performIntraProceduralVSA(Function function)
	{
		assignment = new HashMap<>();
		mycounter = new HashMap<>();
		Queue<Instruction> worklist = new LinkedList<>();
		Transformer transformer = new ESILTransformer();

		Instruction entry = Traversals.functionToEntryInstruction(function);
		if (entry == null)
		{
			return;
		}
		setAbstractEnvironment(entry, createAbstractEnvironment(function));
		worklist.add(entry);
		while (!worklist.isEmpty())
		{
			AbstractEnvironment out;
			Instruction n = worklist.remove();
			try
			{
				out = transformer.transform(n.getEsilCode(), getAbstractEnvironment(n));
			} catch (ESILTransformationException e)
			{
				logger.error(e.getMessage());
				out = new AbstractEnvironment();
			} catch (NoSuchElementException e)
			{
				logger.error("Invalid esil stack");
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

	private AbstractEnvironment createAbstractEnvironment(Function function)
	{
		AbstractEnvironment env = new AbstractEnvironment();
		for (Aloc aloc : Traversals.functionToAlocs(function))
		{
			if (aloc.isFlag())
			{
				env.setFlag(new Flag(aloc.getName(), Bool3.MAYBE));
			} else if (aloc.isRegister())
			{
				// TODO: Read initial values and the data width from aloc node.
				ValueSet valueSet;
				if (aloc.getName().equals("rsp"))
				{
					valueSet = ValueSet.newSingle(LocalRegion.newLocalRegion(),
							StridedInterval.getSingletonSet(0, DataWidth.R64));
				} else
				{
					valueSet = ValueSet.newTop(DataWidth.R64);
				}
				env.setRegister(new Register(aloc.getName(), valueSet));
			}
		}
		return env;
	}

	private void writeResults()
	{
		for (Instruction instr : assignment.keySet())
		{
			logger.info(instr.getEsilCode());
			logger.info(assignment.get(instr).toString());
			for (Edge edge : instr.getEdges(Direction.OUT, EdgeTypes.READ))
			{
				Aloc aloc = new Aloc(edge.getVertex(Direction.IN));
				if (aloc.isFlag())
				{
					edge.setProperty(BjoernEdgeProperties.VALUE,
							assignment.get(instr).getFlag(aloc.getName()).getBooleanValue().toString());
				} else
				{
					edge.setProperty(BjoernEdgeProperties.VALUE,
							assignment.get(instr).getRegister(aloc.getName()).getValue().toString());
				}
			}

		}

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
		logger.info("Performing widening: " + oldEnv + " [<=>] " + newEnv);
		for (Register register : newEnv.getRegisters())
		{
			String identifier = register.getIdentifier();
			ValueSet valueSet = oldEnv.getRegister(identifier).getValue().widen(register.getValue());
			newEnv.setRegister(new Register(identifier, valueSet));
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

}
