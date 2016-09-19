package bjoern.plugins.vsa.transformer;

import bjoern.pluginlib.radare.emulation.esil.ESILKeyword;
import bjoern.pluginlib.radare.emulation.esil.ESILTokenEvaluator;
import bjoern.pluginlib.radare.emulation.esil.ESILTokenStream;
import bjoern.plugins.vsa.domain.AbstractEnvironment;
import bjoern.plugins.vsa.domain.ValueSet;
import bjoern.plugins.vsa.structures.DataWidth;
import bjoern.plugins.vsa.structures.StridedInterval;
import bjoern.plugins.vsa.transformer.esil.ESILTransformationException;
import bjoern.plugins.vsa.transformer.esil.commands.ESILCommand;
import bjoern.plugins.vsa.transformer.esil.stack.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ESILTransformer implements Transformer
{
	private final Map<ESILKeyword, ESILCommand> commands;

	private Logger logger = LoggerFactory.getLogger(ESILTransformer.class);
	private AbstractEnvironment outEnv = null;
	private ESILTokenEvaluator esilParser = new ESILTokenEvaluator();

	public ESILTransformer(Map<ESILKeyword, ESILCommand> commands)
	{
		this.commands = commands;
	}

	@Override
	public AbstractEnvironment transform(String esilCode, AbstractEnvironment inEnv)
	{
		// copy environment
		outEnv = new AbstractEnvironment(inEnv);
		// initialize esil stack
		ESILStack esilStack = new ESILStack();
		ESILTokenStream tokenStream = new ESILTokenStream(esilCode);

		logger.info("Transforming: " + esilCode + "");

		if (esilCode.equals(""))
		{
			return outEnv;
		}

		while (tokenStream.hasNext())
		{
			String token = tokenStream.next();
			if (esilParser.isEsilKeyword(token))
			{
				ESILKeyword keyword = ESILKeyword.fromString(token);
				if (keyword == ESILKeyword.START_CONDITIONAL)
				{
					executeConditional(esilStack, tokenStream);
				} else if (keyword == ESILKeyword.END_CONDITIONAL)
				{
				} else
				{
					ESILCommand command = commands.get(keyword);
					command.execute(outEnv, esilStack);
				}
			} else
			{
				ESILStackItem item = convert(token);
				esilStack.push(item);
			}
		}
		return outEnv;
	}


	private ESILStackItem convert(String token)
	{
		if (esilParser.isNumericConstant(token))
		{
			ValueSet valueSet = ValueSet
					.newGlobal(StridedInterval.getSingletonSet(esilParser.parseNumericConstant(token), DataWidth.R64));
			return new ValueSetContainer(valueSet);
		} else if (esilParser.isRegister(token))
		{
			return new RegisterContainer(outEnv.getRegister(token));
		} else if (esilParser.isFlag(token))
		{
			return new FlagContainer(outEnv.getFlag(token));
		} else
		{
			throw new ESILTransformationException("Cannot convert token: " + token);
		}
	}

	private void executeConditional(ESILStack esilStack, ESILTokenStream tokenStream)
	{
		ValueSet valueSet = esilStack.popValueSet();
		if (valueSet.getValueOfGlobalRegion().isZero())
		{
			tokenStream.skipUntilToken(ESILKeyword.END_CONDITIONAL.keyword);
		} else if (!valueSet.getValueOfGlobalRegion().isOne())
		{
		} else
		{
			StringBuilder builder = new StringBuilder();
			do
			{
				builder.append(tokenStream.next()).append(",");
			} while (tokenStream.hasNext());

			// remove trailing comma
			builder.setLength(builder.length() - 1);
			String esilCode = builder.toString();

			AbstractEnvironment amc = new ESILTransformer(commands).transform(esilCode, outEnv);
			if (esilCode.indexOf("}") == esilCode.length() - 1)
			{
				outEnv = amc.union(outEnv);
			} else
			{
				outEnv = amc
						.union(new ESILTransformer(commands)
								.transform(esilCode.substring(esilCode.indexOf("}") + 2), outEnv));
			}
		}
	}

}
