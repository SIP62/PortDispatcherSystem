package by.bsuir.lab02.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;

import by.bsuir.lab02.port.Port;
import by.bsuir.lab02.ship.Ship;
import by.bsuir.lab02.warehouse.Container;
/**
 * This class is responsible for start port dispatcher system
 * 
 * @version 1.0
 * @author Sytau
 */
public class Main {

	public static void main(String[] args) throws InterruptedException {

		// Input data of ships and port

		/** Ship quantity */
		int shipQuantity = 5;
		
		/** Massive of Ship capacities */
		int[] shipWarehouseCapacity = {40, 70, 60, 80, 50};
		
		/** Massive of Ship initial loading */
		int[] shipWarehouseLoad = {15, 25, 40, 30, 5};
		
		/** Berth quantity */
		int berthQuantity = 3;
		
		/** Port warehouse capacity */
		int portWarehouseCapacity = 90;
		
		/** Initial loading of the port warehouse */
		int portWarehouseLoad = 50;
		
		/** Massive of Ships */
		Ship[] ship = new Ship[shipQuantity];
		
		/** Ship name */
		String shipName;
		
		/** Collection of containers */
		List<Container> containerList;
		
		containerList = new ArrayList<Container>(portWarehouseLoad);
		for (int i=0; i<portWarehouseLoad; i++){
			// Assign numbers to containers in the port warehouse
			containerList.add(new Container(i));
		}

		Port port = new Port(berthQuantity, portWarehouseCapacity);// Port initialization
		

		port.setContainersToWarehouse(containerList);// Put containers to the port warehouse
		

		int k = portWarehouseLoad;
		for (int i = 0; i < shipQuantity; i++) {
			containerList = new ArrayList<Container>(shipWarehouseLoad[i]);
			for (int j = 0; j < shipWarehouseLoad[i]; j++){
				// Assign numbers to containers in ship warehouses
				containerList.add(new Container(j+k));
			}
			k = k + shipWarehouseLoad[i];
			shipName = "Ship" + (i+1); // Assign ship names
			Random priorityRandom = new Random(); //for calculating ship priority
			// Ships initialization
			ship[i] = new Ship(shipName, priorityRandom.nextInt(10), port, shipWarehouseCapacity[i]);
			ship[i].setContainersToWarehouse(containerList); // Put containers to ships
		}

		Timer portTimer = new Timer();

		// Start of ship threads
		for (int i = 0; i < shipQuantity; i++) {
			new Thread(ship[i]).start();	
		}
		
		// Start of timer for port status displaying
		portTimer.schedule(port, 5000, 5000);
		try {
			Thread.sleep(25000);
		} catch (InterruptedException ex) {}
		portTimer.cancel();
		
		// End of ship threads
		for (int i = 0; i < shipQuantity; i++) {
			ship[i].stopThread();	
		}

	}

}
