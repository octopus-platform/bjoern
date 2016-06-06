package bjoern.pluginlib.radare.emulation.esil;

public class ESILAccessExtractor
{

	public String extract(ESILTokenStream tokenStream, int index)
	{
		String accessOperation = tokenStream.getTokenAt(index);
		tokenStream.getTokenAt(index - 1);


		return null;
	}

}
