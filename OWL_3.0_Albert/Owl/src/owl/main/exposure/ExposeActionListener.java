package owl.main.exposure;

public interface ExposeActionListener
{
	public final byte EXPOSE = 0;
	public final byte ABORT  = 1;

	public void setExposeAction( byte action );
}
