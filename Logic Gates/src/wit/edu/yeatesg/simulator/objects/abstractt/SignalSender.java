package wit.edu.yeatesg.simulator.objects.abstractt;

import wit.edu.yeatesg.simulator.objects.other.Wire;

public abstract class SignalSender extends SignalEntity 
{
	private Wire connectingTo;
		
	@Override
	public void transmit()
	{
		if (status)
		{
			connectingTo.transmit();
		}
	}	
}		
