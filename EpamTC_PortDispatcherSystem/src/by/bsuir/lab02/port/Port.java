package by.bsuir.lab02.port;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import by.bsuir.lab02.ship.Ship;
import by.bsuir.lab02.warehouse.Container;
import by.bsuir.lab02.warehouse.Warehouse;

/**
 * Port is the class that responsible for port condition at any time
 * 
 * @version 1.0
 * @author Sytau
 */
public class Port extends TimerTask{
	private final static Logger logger = Logger.getRootLogger();
	private final Logger portLogger = Logger.getLogger(Port.class);
	
	/** A berth queue */
	private BlockingQueue<Berth> berthList; // очередь причалов
	
	/** A port warehouse */
	private Warehouse portWarehouse; // хранилище порта
	
	/** A map of ship - berth link */
	private Map<Ship, Berth> usedBerths; // какой корабль у какого причала стоит
	
	/** A ship list waiting for a berth */
	private HashSet<Ship> waitingShip; // список кораблей в ожидании причала
	
	/** A map of ship - quantity of loading duration violations */
	private Map<Ship, Integer> loadingViolations; //карта нарушений времени загрузки

	/**
	 * This constructor builds a new instance of Port with preset values
	 * @param berthSize the berth quantity
	 * @param warehouseSize the capacity of the port warehouse
	 */
	public Port(int berthSize, int warehouseSize) {
		portWarehouse = new Warehouse(warehouseSize); // создаем пустое хранилище
		berthList = new ArrayBlockingQueue<Berth>(berthSize); // создаем очередь причалов
		for (int i = 0; i < berthSize; i++) { // заполн€ем очередь причалов непосредственно самими причалами
			berthList.add(new Berth(i, portWarehouse));
		}
		usedBerths = new HashMap<Ship, Berth>(); // создаем объект, который будет
		// хранить св€зь между кораблем и причалом
		waitingShip = new HashSet<Ship>();
		loadingViolations = new HashMap<Ship, Integer>();

		logger.debug("ѕорт создан.");
	}
	
	/**
	 * Fills port warehouse with containers
	 * @param containerList list of containers that includes id
	 */
	public void setContainersToWarehouse(List<Container> containerList){
		portWarehouse.addContainer(containerList);
	}

	/**
	 * Allocates a berth for this ship
	 * @param ship the instance of Ship class
	 * @return <b>true</b> if this ship has berthed successfully and <b>false</b> if hasn't
	 */
	public boolean lockBerth(Ship ship) {
		Berth berth;
		try {
			berth = berthList.take();
			usedBerths.put(ship, berth);
		} catch (InterruptedException e) {
			logger.debug(" ораблю " + ship.getName() + " отказано в швартовке.");
			return false;
		}		
		return true;
	}
	
	/**
	 * Releases this berth after this ship loading
	 * @param ship the instance of Ship class
	 * @return <b>true</b> if the berth has been released successfully and <b>false</b> if hasn't
	 */
	public boolean unlockBerth(Ship ship) {
		Berth berth = usedBerths.get(ship);
		
		try {
			berthList.put(berth);
			usedBerths.remove(ship);
		} catch (InterruptedException e) {
			logger.debug(" орабль " + ship.getName() + " не смог отшвартоватьс€.");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Gets a berth for this ship
	 * @param ship the instance of Ship class
	 * @return the instance of Berth class
	 * @throws PortException If all berths are engaged exception occurred
	 */
	public Berth getBerth(Ship ship) throws PortException {
		
		Berth berth = usedBerths.get(ship);
		if (berth == null){
			throw new PortException("Try to use Berth without blocking.");
		}
		return berth;		
	}

	/**
	 * Runs Timer thread for logging port situation every 5 seconds
	 */
	@Override
	public void run() {
		portLogger.info("\n");
		portLogger.info(" оличество контейнеров на складе порта: " + portWarehouse.getRealSize() + "\n" + "Ўвартовка: ");
		Set<Map.Entry<Ship, Berth>> setub = usedBerths.entrySet();
		Iterator<Map.Entry<Ship, Berth>> iub = setub.iterator();
		while (iub.hasNext()) {
			Map.Entry<Ship, Berth> me = iub.next();
			portLogger.info(" орабль " + me.getKey().getName() + " у причала " + me.getValue().getId());
		}
		
		Iterator<Ship> it = waitingShip.iterator();
		while (it.hasNext()) {
			portLogger.info(" орабль " + it.next().getName() + "  в очереди на швартовку ");
			
		}
	
	}
	
	/**
	 * Adds this ship to the collection of ships waiting their turn
	 * @param ship the instance of Ship class
	 */
	public void shipIsWaiting(Ship ship) {
		waitingShip.add(ship);
	}
	
	/**
	 * Removes this ship from the collection of ships waiting their turn
	 * @param ship the instance of Ship class
	 */
	public void shipIsNotWaiting(Ship ship) {
		waitingShip.remove(ship);
	}
	
	/**
	 * Adds violation for this ship in violation collection
	 * @param ship the instance of Ship class
	 */
	public void addViolation(Ship ship) {
		loadingViolations.put(ship, loadingViolations.get(ship) + 1);
//		System.out.println(" орабль " + ship.getName() + " имеет " + loadingViolations.get(ship) + " нарушений");
	}
	
	/**
	 * Extracts the quantity of violations for this ship in violation collection
	 * @param ship the instance of Ship class
	 * @return the quantity of violations for this ship
	 */
	public int getViolations(Ship ship) {
		return loadingViolations.get(ship);
	}
	
	/**
	 * Initiates the ship violation collection
	 * @param ship the instance of Ship class
	 */
	public void initViolation(Ship ship) {
		loadingViolations.put(ship, 0);
	}

}
