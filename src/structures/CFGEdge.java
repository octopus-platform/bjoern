package structures;

public class CFGEdge
{
	private String type;
	private BasicBlock from;
	private BasicBlock to;

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public BasicBlock getFrom()
	{
		return from;
	}

	public void setFrom(BasicBlock from)
	{
		this.from = from;
	}

	public BasicBlock getTo()
	{
		return to;
	}

	public void setTo(BasicBlock to)
	{
		this.to = to;
	}

}
