package bjoern.r2interface.architectures;

public abstract class Architecture {

	public abstract boolean isCall(String esilCode);
	public abstract boolean isFlag(String registerName);

}
