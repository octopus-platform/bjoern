package bjoern.pluginlib.radare.emulation.esil;

public class ESILAccessExtractor
{

	public String extract(ESILTokenStream tokenStream, int index)
	{
		String accessOperation = tokenStream.getTokenAt(index);

		ESILKeyword accessKeyword = ESILKeyword.fromString(accessOperation);
		if(accessKeyword == null)
			return accessOperation;

		int nargs = ESILKeyword.nargsForKeyword(accessKeyword);

		String retval = accessOperation;

		for(int i = 0; i < nargs; i++){
			retval = extract(tokenStream, index -1 -i) + "," + retval;
		}

		return retval;
	}

}
