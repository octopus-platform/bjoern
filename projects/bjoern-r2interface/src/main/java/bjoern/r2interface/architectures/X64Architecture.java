package bjoern.r2interface.architectures;

public class X64Architecture extends Architecture {

	@Override
	public boolean isCall(String esilCode)
	{
		return esilCode.startsWith("rip,8,rsp,-=,rsp,=[],");
	}

	@Override
	public boolean isFlag(String registerName)
	{
		return (registerName.length() == 2 && registerName.endsWith("f"));
	}

}
