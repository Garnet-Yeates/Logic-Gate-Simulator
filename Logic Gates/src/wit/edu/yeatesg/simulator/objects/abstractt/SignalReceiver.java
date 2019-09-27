package wit.edu.yeatesg.simulator.objects.abstractt;

import wit.edu.yeatesg.simulator.objects.other.Wire;

public abstract class SignalReceiver extends SignalEntity
{
	private Wire connectingFrom;
	
	public void transmit()
	{
		status = true;
		justUpdated = true;
	}
	
	public Wire getConnectedWire()
	{
		return connectingFrom;
	}
}
