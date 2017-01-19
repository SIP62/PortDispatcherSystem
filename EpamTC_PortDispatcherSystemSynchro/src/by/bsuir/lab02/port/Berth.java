package by.bsuir.lab02.port;

import java.util.List;

import by.bsuir.lab02.warehouse.Container;
import by.bsuir.lab02.warehouse.Warehouse;

/**
 * Berth is the class that responsible for berth condition at any time
 * 
 * @version 1.1
 * @author Sytau
 */
public class Berth {
	
	/** A berth id */
	private int id;
	
	/** A Port warehouse */
	private Warehouse portWarehouse;

	/**
	 * This constructor builds a new instance of Berth with preset values
	 * @param id the id of this berth
	 * @param warehouse  - the instance of Warehouse class
	 */
	public Berth(int id, Warehouse warehouse) {
		this.id = id;
		portWarehouse = warehouse;
	}
	
	/**
	 * Getter for the field {@link Berth#id}
	 * @return Returns the berth <b>id</b>
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Locks port warehouse for unloading containers from this ship
	 * @param shipWarehouse the instance of Warehouse class for this ship
	 * @param numberOfContainers quantity of containers to unload
	 * @return result <b>true</b> if unloading is fulfilled successfully and <b>false</b> if isn't
	 * @throws InterruptedException If exception occurred  in the port
	 */
	public boolean add(Warehouse shipWarehouse, int numberOfContainers) throws InterruptedException {
		boolean result = false;
		
		synchronized (portWarehouse) {
			if (numberOfContainers <= portWarehouse.getFreeSize()) {
				result = doMoveFromShip(shipWarehouse, numberOfContainers);	
			}
		}
		return result;
	}
	
	/**
	 * Locks ship warehouse for unloading containers from this ship
	 * 	and move containers to the port warehouse
	 * @param shipWarehouse the instance of Warehouse class for this ship
	 * @param numberOfContainers quantity of containers to unload
	 * @return <b>true</b> if unloading is fulfilled successfully and <b>false</b> if isn't
	 * @throws InterruptedException If exception occurred  in the port
	 */
	private boolean doMoveFromShip(Warehouse shipWarehouse, int numberOfContainers) throws InterruptedException{

		synchronized (shipWarehouse) {
			if(shipWarehouse.getRealSize() >= numberOfContainers){
				List<Container> containers = shipWarehouse.getContainer(numberOfContainers);
				portWarehouse.addContainer(containers);
				return true;
			}
		}
		return false;		
	}
	
	/**
	 * Locks port warehouse for loading containers to this ship
	 * @param shipWarehouse the instance of Warehouse class for this ship
	 * @param numberOfContainers quantity of containers to load
	 * @return result <b>true</b> if loading is fulfilled successfully and <b>false</b> if isn't
	 * @throws InterruptedException If exception occurred  in the port
	 */
	public boolean get(Warehouse shipWarehouse, int numberOfContainers) throws InterruptedException {
		boolean result = false;
		
		synchronized (portWarehouse) {
			if (numberOfContainers <= portWarehouse.getRealSize()) {
				result = doMoveFromPort(shipWarehouse, numberOfContainers);	
			}
		}
		return result;
	}
	
	/**
	 * Locks ship warehouse for loading containers to this ship
	 * 	and move containers from the port warehouse
	 * @param shipWarehouse the instance of Warehouse class for this ship
	 * @param numberOfContainers quantity of containers to load
	 * @return <b>true</b> if loading is fulfilled successfully and <b>false</b> if isn't
	 * @throws InterruptedException If exception occurred  in the port
	 */
	private boolean doMoveFromPort(Warehouse shipWarehouse, int numberOfContainers) throws InterruptedException{

		synchronized (shipWarehouse) {
			if(numberOfContainers <= shipWarehouse.getFreeSize()){
				List<Container> containers = portWarehouse.getContainer(numberOfContainers);
				shipWarehouse.addContainer(containers);
				return true;
			}
		}
		return false;		
	}
}
