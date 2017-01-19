package by.bsuir.lab02.port;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import by.bsuir.lab02.warehouse.Container;
import by.bsuir.lab02.warehouse.Warehouse;

/**
 * Berth is the class that responsible for berth condition at any time
 * 
 * @version 1.0
 * @author Sytau
 */
public class Berth {
	
	/** A berth id */
	private int id;
	
	/** A port warehouse */
	private Warehouse portWarehouse;

	/**
	 * This constructor builds a new instance of Berth with preset values
	 * @param id the id of this berth
	 * @param warehouse the instance of Warehouse class
	 */
	public Berth(int id, Warehouse warehouse) {
		this.id = id;
		portWarehouse = warehouse;
	}
	
	/**
	 * Getter for the field {@link Berth#id}
	 * @return this berth <b>id</b>
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
		Lock portWarehouseLock = portWarehouse.getLock();	
		boolean portLock = false;

		try{
			portLock = portWarehouseLock.tryLock(30, TimeUnit.SECONDS);
			if (portLock) {
				if (numberOfContainers <= portWarehouse.getFreeSize()) {
					result = doMoveFromShip(shipWarehouse, numberOfContainers);	
				}
			}
		} finally{
			if (portLock) {
				portWarehouseLock.unlock();
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
		Lock shipWarehouseLock = shipWarehouse.getLock();
		boolean shipLock = false;
		
		try{
			shipLock = shipWarehouseLock.tryLock(30, TimeUnit.SECONDS);
			if (shipLock) {
				if(shipWarehouse.getRealSize() >= numberOfContainers){
					List<Container> containers = shipWarehouse.getContainer(numberOfContainers);
					portWarehouse.addContainer(containers);
					return true;
				}
			}
		}finally{
			if (shipLock) {
				shipWarehouseLock.unlock();
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
		Lock portWarehouseLock = portWarehouse.getLock();	
		boolean portLock = false;

		try{
			portLock = portWarehouseLock.tryLock(30, TimeUnit.SECONDS);
			if (portLock) {
				if (numberOfContainers <= portWarehouse.getRealSize()) {
					result = doMoveFromPort(shipWarehouse, numberOfContainers);	
				}
			}
		} finally{
			if (portLock) {
				portWarehouseLock.unlock();
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
		Lock shipWarehouseLock = shipWarehouse.getLock();
		boolean shipLock = false;
		
		try{
			shipLock = shipWarehouseLock.tryLock(30, TimeUnit.SECONDS);
			if (shipLock) {
				if(numberOfContainers <= shipWarehouse.getFreeSize()){
					List<Container> containers = portWarehouse.getContainer(numberOfContainers);
					shipWarehouse.addContainer(containers);
					return true;
				}
			}
		}finally{
			if (shipLock) {
				shipWarehouseLock.unlock();
			}
		}
		
		return false;		
	}
}
