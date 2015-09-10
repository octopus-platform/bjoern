package inputModules;

import java.util.List;

import structures.Function;

public interface InputModule
{
	public void initialize(String filename);

	public List<Long> getFunctionAddresses();

	public Function getFunctionAtAddress(Long addr);
}
