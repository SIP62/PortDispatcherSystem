package by.bsuir.lab02.port;

/**
 * PortException is the bean class that responsible for message generation 
 * when exception situation is appeared in the port 
 * 
 * @version 1.0
 * @author Sytau
 */
public class PortException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * This constructor builds a new instance of PortException with preset value
	 * @param message the String specifies the PortException message.
	 */
	public PortException(String message){
		super(message);
	}

}
