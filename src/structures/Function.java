package structures;

import nodeStore.Node;
import nodeStore.NodeTypes;

public class Function extends Node
{

	FunctionContent content = new FunctionContent();

	private String name = "";

	public Function()
	{
		setType(NodeTypes.FUNCTION);
	}

	public FunctionContent getContent()
	{
		return content;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setContent(FunctionContent content)
	{
		this.content = content;
	}

}
