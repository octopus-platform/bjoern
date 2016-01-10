package exporters.structures.interpretations;

public class DisassemblyLine
{

	private Long addr;
	private String instruction;
	private String comment;

	public void setAddr(Long addr)
	{
		this.addr = addr;
	}

	public void setInstruction(String instruction)
	{
		this.instruction = instruction;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}

	public Long getAddr()
	{
		return addr;
	}

	public String getInstruction()
	{
		return instruction;
	}

	public String getComment()
	{
		return comment;
	}

}
