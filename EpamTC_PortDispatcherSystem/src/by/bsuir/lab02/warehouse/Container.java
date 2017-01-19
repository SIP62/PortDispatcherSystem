package by.bsuir.lab02.warehouse;

/**
 * Container is the entity class that responsible for storage instances
 *  of containers with field id
 * 
 * @version 1.0
 * @author Sytau
 */
public class Container {
	
	/** id of container */
	private int id;
	
	/**
	 * Constructor builds a new instance of Container with preset value id
	 * @param id the id of the container
	 */
	public Container(int id){
		this.id = id;
	}
	
	/**
	 * Getter for the field {@link Container#id}
	 * @return the container <b>id</b>
	 */
	public int getId(){
		return id;
	}
}
