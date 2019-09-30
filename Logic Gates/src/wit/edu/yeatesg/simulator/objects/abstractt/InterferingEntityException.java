package wit.edu.yeatesg.simulator.objects.abstractt;

public class InterferingEntityException extends RuntimeException
{
	private static final long serialVersionUID = -7038672580824956049L;

	@Override
	public String getMessage()
	{
		return "Two entities cannot have the same BigPoint location!";
	}
}
