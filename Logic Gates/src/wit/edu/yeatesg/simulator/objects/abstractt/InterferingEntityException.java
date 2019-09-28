package wit.edu.yeatesg.simulator.objects.abstractt;

public class InterferingEntityException extends RuntimeException
{
	@Override
	public String getMessage()
	{
		return "Two entities cannot have the same BigPoint location!";
	}
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
