package by.bsuir.lab02.ship;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import by.bsuir.lab02.port.Berth;
import by.bsuir.lab02.port.Port;
import by.bsuir.lab02.port.PortException;
import by.bsuir.lab02.warehouse.Container;
import by.bsuir.lab02.warehouse.Warehouse;
/**
 * This class is responsible for behavior of every ship in their own thread
 * 
 * @version 1.1
 * @author Sytau
 */
public class Ship implements Runnable {

	private final static Logger logger = Logger.getRootLogger();
	private final Logger shipLogger = Logger.getLogger(Ship.class);
	
	/** Time limit for loading one container */
	static int containerTimeLimit = 300;
	
	/** Flag that indicate the stop of the ship thread */
	private volatile boolean stopThread = false;
	
	/** Ship name */
	private String name;
	
	/** Ship priority */
	private int shipPriority;

	private Port port;
	private Warehouse shipWarehouse;
	
	/**
	 * Constructor builds a new instance of Ship with preset values
	 * @param name the name of this ship
	 * @param shipPriority the priority of this ship
	 * @param port the instance of Port class 
	 * @param shipWarehouseSize - the capacity of this ship warehouse
	 */
	public Ship(String name, int shipPriority, Port port, int shipWarehouseSize) {
		this.name = name;
		this.shipPriority = shipPriority;
		this.port = port;
		shipWarehouse = new Warehouse(shipWarehouseSize);
		
		port.initViolation(this); // initialization of ship violations
	}
	
	/**
	 * Fills this ship warehouse with containers
	 * @param containerList list of containers that includes id
	 */
	public void setContainersToWarehouse(List<Container> containerList) {
		shipWarehouse.addContainer(containerList);
	}
	
	/**
	 * Getter for the field {@link Ship#name}
	 * @return this ship <b>name</b>
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gives true to the flag indicating the stop of this ship thread
	 */
	public void stopThread() {
		stopThread = true;
	}
	
	/**
	 * Run this ship thread
	 */
	public void run() {
		try {

			while (!stopThread) {
				atSea(); // Ship at the sea
				inPort(); // Ship in the port
			}
		} catch (InterruptedException e) {
			logger.error("С кораблем случилась неприятность и он уничтожен.", e);
		} catch (PortException e) {
			logger.error("С кораблем случилась авария в порту.", e);
		}
	}
	
	/**
	 * Defines the behavior of this ship at the sea
	 * @throws InterruptedException If exception occurred  at the sea
	 */
	private void atSea() throws InterruptedException {
		logger.debug("Корабль " + name + " в море ");
		Thread.sleep(4000); // ship thread pause while at the sea
	}

	/**
	 * Defines the behavior of this ship in the port
	 * @throws PortException If all berths are engaged exception occurred
	 * @throws InterruptedException If exception occurred  in the port
	 */
	private void inPort() throws PortException, InterruptedException {
		
		/** Flag that indicate that berth is engaged by this ship */
		boolean isLockedBerth = false;
		
		Berth berth = null;
		port.shipIsWaiting(this); // Adding this ship to the collection of ships waiting their turn

		Random importanceRandom = new Random();
		boolean important = importanceRandom.nextBoolean(); // Calculating the importance of cargo
		Random urgentRandom = new Random();
		boolean urgent = urgentRandom.nextBoolean(); // Calculating the urgency of loading
		int prior = shipPriority - port.getViolations(this); // Calculating ship priority
		if(important) prior = prior + 2;
		if(urgent) prior = prior + 2;
		if(prior < 1) prior = 1;
		if(prior > 10) prior = 10;

		Thread.currentThread().setPriority(prior); //Assigning priority to the ship thread
		
		try {
			isLockedBerth = port.lockBerth(this); // allocation of berth for the ship
			port.shipIsNotWaiting(this); // Removing this ship from the collection of ships waiting their turn

			if (isLockedBerth) {
				
				berth = port.getBerth(this); // Getting the berth
				Thread.sleep(500);
				logger.debug("Корабль " + name + " пришвартовался к причалу " + berth.getId());
				ShipAction action = getNextAction(); //Getting the ship mission
				executeAction(action, berth); // Executing the ship mission 
			} else {
				
				logger.debug("Кораблю " + name + " отказано в швартовке к причалу ");
			}
		} finally {
			if (isLockedBerth){
				Thread.sleep(500);
				port.unlockBerth(this);
				logger.debug("Корабль " + name + " отошел от причала " + berth.getId());
			}
		}
		
	}
	
	/**
	 * Causes the ship mission: Load to port or Load from port
	 * @param action the instance of ShipAction enumeration to choose the ship mission
	 * @param berth the instance of Berth class
	 * @throws InterruptedException If exception occurred  in the port
	 */
	private void executeAction(ShipAction action, Berth berth) throws InterruptedException {
		switch (action) {
		case LOAD_TO_PORT:
 				loadToPort(berth);
			break;
		case LOAD_FROM_PORT:
				loadFromPort(berth);
			break;
		}
	}
	
	/**
	 * Loads containers from this ship to the port
	 * @param berth the instance of Berth class
	 * @return result <b>true</b> if mission is fulfilled successfully and <b>false</b> if isn't
	 * @throws InterruptedException If exception occurred  in the port
	 */
	private boolean loadToPort(Berth berth) throws InterruptedException {

		int containersNumberToMove = containersCount(); // Container quantity to move
		if(containersNumberToMove > shipWarehouse.getRealSize())containersNumberToMove = shipWarehouse.getRealSize();
		int loadingDurationLimit = containersNumberToMove*containerTimeLimit; // Calculating offered loading duration
		boolean result = false;

		logger.debug("Корабль " + name + " хочет загрузить " + containersNumberToMove
				+ " контейнеров на склад порта за " + loadingDurationLimit + "мс.");

		synchronized (berth) {
			result = berth.add(shipWarehouse, containersNumberToMove);
		}
		

		if (!result) {
			
			Thread.sleep(400);
			logger.debug("Недостаточно места на складе порта для выгрузки кораблем "
					+ name + " " + containersNumberToMove + " контейнеров.");
		} else {
			logger.debug("Выгрузка с корабля " + name + " " + containersNumberToMove
					+ " контейнеров начата");
			// Calculating real loading duration
			Date beginDate = new Date();
			long loadingStartAt = beginDate.getTime();
			Random random = new Random();
			Thread.sleep(containersNumberToMove*(random.nextInt(120) + 240));
			Date finishDate = new Date();
			long loadingFinishAt = finishDate.getTime();
			long realLoadingDuration = loadingFinishAt - loadingStartAt;

			logger.debug("Корабль " + name + " выгрузил " + containersNumberToMove
					+ " контейнеров в порт за " + realLoadingDuration + "мс.");
			
			if(realLoadingDuration > loadingDurationLimit) {
				port.addViolation(this); // add violation for this ship in violation collection
				shipLogger.info("Корабль " + name + " превысил время выгрузки на " + (realLoadingDuration - loadingDurationLimit) + "мс.");
			}
			
		}
		return result;
	}
	
	/**
	 * Loads containers from the port to this ship
	 * @param berth the instance of Berth class
	 * @return result <b>true</b> if mission is fulfilled successfully and <b>false</b> if isn't
	 * @throws InterruptedException If exception occurred  in the port
	 */
	private boolean loadFromPort(Berth berth) throws InterruptedException {
		
		int containersNumberToMove = containersCount(); // Container quantity to move
		if(containersNumberToMove > shipWarehouse.getFreeSize())containersNumberToMove = shipWarehouse.getFreeSize();
		int loadingDurationLimit = containersNumberToMove*containerTimeLimit; // Calculating offered loading duration
		boolean result = false;

		logger.debug("Корабль " + name + " хочет загрузить " + containersNumberToMove
				+ " контейнеров со склада порта за " + loadingDurationLimit + "мс.");
		
		synchronized (berth) {
			result = berth.get(shipWarehouse, containersNumberToMove);
		}
		
		
		if (result) {
			logger.debug("Загрузка на корабль " + name + " " + containersNumberToMove
					+ " контейнеров начата");
			// Calculating real loading duration
			Date beginDate = new Date();
			long loadingStartAt = beginDate.getTime();
			Random random = new Random();
			Thread.sleep(containersNumberToMove*(random.nextInt(120) + 240));
			Date finishDate = new Date();
			long loadingFinishAt = finishDate.getTime();
			long realLoadingDuration = loadingFinishAt - loadingStartAt;
			
			logger.debug("Корабль " + name + " загрузил " + containersNumberToMove
					+ " контейнеров из порта за " + realLoadingDuration + "мс.");
			
			if(realLoadingDuration > loadingDurationLimit) {
				port.addViolation(this); // add violation for this ship in violation collection
				shipLogger.info("Корабль " + name + " превысил время загрузки на " + (realLoadingDuration - loadingDurationLimit) + "мс.");
			}
		} else {
			Thread.sleep(400);
			logger.debug("Недостаточно товаров на складе порта "
					+ " для погрузки " + containersNumberToMove + " контейнеров на корабль " + name);
		}
		return result;
	}
	
	/**
	 * Calculates number of containers to load
	 * @return number of containers
	 */
	private int containersCount() {
		Random random = new Random();
		return random.nextInt(20) + 1;
	}
	
	/**
	 * Gets the ship mission
	 * @return the ship mission
	 */
	private ShipAction getNextAction() {
		Random random = new Random();
		boolean value = random.nextBoolean();
		if (value && shipWarehouse.getRealSize() != 0) {
			return ShipAction.LOAD_TO_PORT;
		}else return ShipAction.LOAD_FROM_PORT;	
	}
	
	/**
	 * Enumeration of ship missions
	 * @author Sytau
	 *
	 */
	enum ShipAction {
		LOAD_TO_PORT, LOAD_FROM_PORT
	}
}
