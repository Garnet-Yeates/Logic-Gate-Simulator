package wit.edu.yeatesg.simulator.objects.abstractt;

public abstract class SignalEntity extends Entity
{
	protected boolean status;
	protected boolean justUpdated;
	
	public abstract void transmit();

	public void setJustUpdated(boolean b)
	{
		justUpdated = b;
	}
	
	public void setStatus(boolean b)
	{
		status = b;
	}
	
	public boolean justUpdated()
	{
		return justUpdated;
	}
	
	public boolean getStatus()
	{
		return status;
	}
}
