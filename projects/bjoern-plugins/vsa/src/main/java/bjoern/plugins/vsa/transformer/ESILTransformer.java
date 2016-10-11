package bjoern.plugins.vsa.transformer;

import bjoern.pluginlib.radare.emulation.esil.ESILKeyword;
import bjoern.pluginlib.radare.emulation.esil.ESILTokenEvaluator;
import bjoern.pluginlib.radare.emulation.esil.ESILTokenStream;
import bjoern.plugins.vsa.data.*;
import bjoern.plugins.vsa.domain.AbstractEnvironment;
import bjoern.plugins.vsa.domain.ValueSet;
import bjoern.plugins.vsa.structures.Bool3;
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
	public DataObjectObserver observer;

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
					command.execute(esilStack, outEnv);
				} else {
					ESILCommand command = commands.get(keyword);
					if (keyword.sideEffect) {
						ESILStackItem result = command.execute(esilStack,
								outEnv);
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
			ValueSet value = outEnv.getRegister(token);
			if (value == null) {
				value = ValueSet.newTop(DataWidth.R64);
			}
			ObservableDataObject<ValueSet> dataObject = new ObservableDataObject<>(
					new Register(token, value));
			dataObject.addObserver(new RegisterObserver(outEnv));
			if (observer != null) {
				dataObject.addObserver(observer);
			}
			return new RegisterContainer(dataObject);
		} else if (esilParser.isFlag(token)) {
			Bool3 value = outEnv.getFlag(token);
			if (value == null) {
				value = Bool3.MAYBE;
			}
			ObservableDataObject<Bool3> dataObject = new ObservableDataObject<>(
					new Flag(token, value));
			dataObject.addObserver(new FlagObserver(outEnv));
			if (observer != null) {
				dataObject.addObserver(observer);
			}
			return new FlagContainer(dataObject);
		} else {
			throw new ESILTransformationException(
					"Cannot convert token: " + token);
		}
	}

	private static class RegisterObserver
			implements DataObjectObserver<ValueSet> {

		private final AbstractEnvironment env;

		public RegisterObserver(AbstractEnvironment env) {
			this.env = env;
		}

		@Override
		public void updateRead(
				final DataObject<ValueSet> dataObject) {

		}

		@Override
		public void updateWrite(
				final DataObject<ValueSet> dataObject, final ValueSet value) {
			this.env.setRegister(dataObject.getIdentifier(), value);
		}
	}

	private static class FlagObserver implements DataObjectObserver<Bool3> {

		private final AbstractEnvironment env;

		public FlagObserver(AbstractEnvironment env) {
			this.env = env;
		}

		@Override
		public void updateRead(
				final DataObject<Bool3> dataObject) {

		}

		@Override
		public void updateWrite(
				final DataObject<Bool3> dataObject, final Bool3 value) {
			this.env.setFlag(dataObject.getIdentifier(), value);

		}
	}

}
