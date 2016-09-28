package bjoern.plugins.vsa.transformer;

import bjoern.pluginlib.radare.emulation.esil.ESILKeyword;
import bjoern.pluginlib.radare.emulation.esil.ESILTokenEvaluator;
import bjoern.pluginlib.radare.emulation.esil.ESILTokenStream;
import bjoern.plugins.vsa.domain.AbstractEnvironment;
import bjoern.plugins.vsa.domain.ValueSet;
import bjoern.plugins.vsa.structures.DataWidth;
import bjoern.plugins.vsa.structures.StridedInterval;
import bjoern.plugins.vsa.transformer.esil.ESILTransformationException;
import bjoern.plugins.vsa.transformer.esil.commands.ConditionalCommand;
import bjoern.plugins.vsa.transformer.esil.commands.ESILCommand;
import bjoern.plugins.vsa.transformer.esil.commands.PopCommand;
import bjoern.plugins.vsa.transformer.esil.stack.ESILStackItem;
import bjoern.plugins.vsa.transformer.esil.stack.FlagContainer;
import bjoern.plugins.vsa.transformer.esil.stack.RegisterContainer;
import bjoern.plugins.vsa.transformer.esil.stack.ValueSetContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;

public class ESILTransformer implements Transformer {
	private final Map<ESILKeyword, ESILCommand> commands;

	private Logger logger = LoggerFactory.getLogger(ESILTransformer.class);
	private AbstractEnvironment outEnv = null;
	private ESILTokenEvaluator esilParser = new ESILTokenEvaluator();

	public ESILTransformer(Map<ESILKeyword, ESILCommand> commands) {
		this.commands = commands;
	}

	@Override
	public AbstractEnvironment transform(
			String esilCode,
			AbstractEnvironment inEnv) {
		// copy environment
		outEnv = new AbstractEnvironment(inEnv);
		// initialize esil stack
		Deque<ESILCommand> esilStack = new LinkedList<>();
		ESILTokenStream tokenStream = new ESILTokenStream(esilCode);

		logger.info("Transforming: " + esilCode + "");

		if (esilCode.equals("")) {
			return outEnv;
		}

		while (tokenStream.hasNext()) {
			String token = tokenStream.next();
			if (esilParser.isEsilKeyword(token)) {
				ESILKeyword keyword = ESILKeyword.fromString(token);
				if (keyword == ESILKeyword.START_CONDITIONAL) {
					ConditionalCommand command = new ConditionalCommand(
							conditionalFromTokenStream(tokenStream));
					command.execute(esilStack);
				} else {
					ESILCommand command = commands.get(keyword);
					if (keyword.sideEffect) {
						ESILStackItem result = command.execute(esilStack);
						if (result != null) {
							esilStack.push(new PopCommand(result));
						}
					} else {
						esilStack.push(command);
					}
				}
			} else {
				ESILStackItem item = convert(token);
				esilStack.push(new PopCommand(item));
			}
		}
		return outEnv;
	}

	private String conditionalFromTokenStream(ESILTokenStream tokenStream) {
		StringBuilder builder = new StringBuilder();
		do {
			String s = tokenStream.next();
			if (s.equals(ESILKeyword.END_CONDITIONAL.keyword)) {
				break;
			}
			builder.append(s);
		} while (tokenStream.hasNext());
		return builder.toString();
	}

	private ESILStackItem convert(String token) {
		if (esilParser.isNumericConstant(token)) {
			ValueSet valueSet = ValueSet
					.newGlobal(StridedInterval.getSingletonSet(
							esilParser.parseNumericConstant(token),
							DataWidth.R64));
			return new ValueSetContainer(valueSet);
		} else if (esilParser.isRegister(token)) {
			return new RegisterContainer(outEnv.getRegister(token));
		} else if (esilParser.isFlag(token)) {
			return new FlagContainer(outEnv.getFlag(token));
		} else {
			throw new ESILTransformationException(
					"Cannot convert token: " + token);
		}
	}

}
