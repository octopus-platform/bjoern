package bjoern.pluginlib.radare.emulation.esil;

public class MemoryAccess {

	private String register1 = "";
	private long multiplier = 0;
	private String register2 = "";
	private long displacement = 0;

	public String getRegister1() {
		return register1;
	}
	public void setRegister1(String register1) {
		this.register1 = register1;
	}
	public long getMultiplier() {
		return multiplier;
	}
	public void setMultiplier(long multiplier) {
		this.multiplier = multiplier;
	}
	public String getRegister2() {
		return register2;
	}
	public void setRegister2(String register2) {
		this.register2 = register2;
	}
	public long getDisplacement() {
		return displacement;
	}
	public void setDisplacement(long displacement) {
		this.displacement = displacement;
	}

}
