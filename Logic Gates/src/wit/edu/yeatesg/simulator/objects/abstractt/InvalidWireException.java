package wit.edu.yeatesg.simulator.objects.abstractt;

public class InvalidWireException extends RuntimeException
{
	private static final long serialVersionUID = -7872717227711036498L;

	@Override
	public String getMessage()
	{
		return "Invalid Wire! Must be a straight line! Do you think I'm some kind of fucking magician coder?";
	}
}
