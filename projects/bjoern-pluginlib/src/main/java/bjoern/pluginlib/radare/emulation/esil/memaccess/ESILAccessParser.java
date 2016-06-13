package bjoern.pluginlib.radare.emulation.esil.memaccess;

import bjoern.pluginlib.radare.emulation.esil.ESILKeyword;
import bjoern.pluginlib.radare.emulation.esil.ESILTokenStream;

public class ESILAccessParser
{

	private static class ExtractorState{

		public ExtractorState(int i, String e)
		{
			index = i;
			expr = e;
		}

		int index;
		String expr;
	}

	public static String parse(ESILTokenStream tokenStream, int index)
	{
		ExtractorState state = extract_(tokenStream, index -1);
		return state.expr;
	}


	private static ExtractorState extract_(ESILTokenStream tokenStream, int index)
	{
		String curToken = tokenStream.getTokenAt(index);

		ESILKeyword accessKeyword = ESILKeyword.fromString(curToken);
		if(accessKeyword == null)
			// is token a non-keyword?
			return new ExtractorState(index -1, curToken);

		int nargs = ESILKeyword.nargsForKeyword(accessKeyword);

		String retString = curToken;

		int curIndex = index -1;
		for(int i = 0; i < nargs; i++){
			ExtractorState state = extract_(tokenStream, curIndex);
			curIndex = state.index;
			retString = state.expr + "," + retString;
		}

		return new ExtractorState(curIndex, retString);
	}

}
