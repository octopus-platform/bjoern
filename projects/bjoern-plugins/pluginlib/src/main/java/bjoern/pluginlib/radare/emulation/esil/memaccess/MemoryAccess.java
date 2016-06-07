package bjoern.pluginlib.radare.emulation.esil.memaccess;

public class MemoryAccess {

	private String esilExpression;
	private String address;
	private String instructionRepr;

	public String getEsilExpression()
	{
		return esilExpression;
	}

	public void setEsilExpression(String esilExpression)
	{
		this.esilExpression = esilExpression;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public void debugOut()
	{
		System.out.println("Repr: " + instructionRepr);
		System.out.println("Expr: " + esilExpression);
		System.out.println("Addr: " + address);
	}

	public String getInstructionRepr()
	{
		return instructionRepr;
	}

	public void setInstructionRepr(String instructionRepr)
	{
		this.instructionRepr = instructionRepr;
	}

}
