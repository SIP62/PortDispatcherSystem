package by.bsuir.lab02.warehouse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Warehouse is the class that is responsible for storage of containers and their motion
 * 
 * @version 1.0
 * @author Sytau
 */
public class Warehouse {
	private List<Container> containerList;
	private int size;
	private Lock lock;
	
	/**
	 * Constructor builds a new instance of Warehouse with preset values
	 * @param size the capacity of the warehouse
	 */
	public Warehouse(int size) {
		containerList = new ArrayList<Container>(size);
		lock = new ReentrantLock();
		this.size = size;
	}
	
	/**
	 * Adds container to the container list
	 * @param container the instance of Container class
	 * @return <b>true</b> if the container is added successfully
	 */
	public boolean addContainer(Container container) {	
		return containerList.add(container);
	}
	
	/**
	 * Adds all containers to the container list
	 * @param containers the list of instances of Container class
	 * @return <b>true</b> if all containers is added successfully
	 */
	public boolean addContainer(List<Container> containers) {
		boolean result = false;
		if(containerList.size() + containers.size() <= size){
			result = containerList.addAll(containers);
		}
		return result;
	}
	
	/**
	 * Extracts container from container list
	 * @return the instance of Container class
	 */
	public Container getContainer() {
		if (containerList.size() > 0) {
			return containerList.remove(0);
		}
		return null;
	}
	
	/**
	 * Extracts sublist of containers from container list
	 * @param amount the quantity of containers to extract
	 * @return the sublist of instances of Container class
	 */
	public List<Container> getContainer(int amount) {
		if (containerList.size() >= amount) {			
			List<Container> cargo = new ArrayList<Container>(containerList.subList(0, amount));
			containerList.removeAll(cargo);
			return cargo;
		}
		return null;
	}
	
	/**
	 * Getter for this field {@link Warehouse#size}
	 * @return size the capacity of the warehouse
	 */
	public int getSize(){
		return size;
	}
	
	/**
	 * Gets real quantity of containers in the warehouse
	 * @return real quantity of containers in the warehouse
	 */
	public int getRealSize(){
		return containerList.size();
	}
	
	/**
	 * Gets empty space in the warehouse
	 * @return quantity of containers that can be put in the warehouse
	 */
	public int getFreeSize(){
		return size - containerList.size();
	}
	
	/**
	 * Getter for this field {@link Warehouse#lock}
	 * @return lock the instance of ReentrantLock class
	 */
	public Lock getLock(){
		return lock;
	}	
}
