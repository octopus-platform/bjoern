package bjoern.r2interface.architectures;

public abstract class Architecture {

	public abstract boolean isCall(String repr);
	public abstract boolean isRet(String repr);
	public abstract boolean isPop(String repr);
	public abstract boolean isPush(String repr);

	public abstract boolean isFlag(String registerName);
	public abstract String getStackRegisterName();
	public abstract String getBaseRegisterName();

}
