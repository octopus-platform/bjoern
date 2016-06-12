package bjoern.r2interface.architectures;

public class X64Architecture extends Architecture {

	@Override
	public boolean isCall(String repr)
	{
		return repr.startsWith("call");
	}

	@Override
	public boolean isRet(String repr)
	{
		return repr.startsWith("ret");
	}

	@Override
	public boolean isPop(String repr)
	{
		return repr.startsWith("pop");
	}

	@Override
	public boolean isPush(String repr)
	{
		return repr.startsWith("push");
	}

	@Override
	public boolean isFlag(String registerName)
	{
		return (registerName.length() == 2 && registerName.endsWith("f"));
	}

	@Override
	public String getStackRegisterName()
	{
		return "rsp";
	}

	@Override
	public String getBaseRegisterName()
	{
		return "rbp";
	}

}
