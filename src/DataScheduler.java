import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.*;

public class DataScheduler {

	public static int numOfSECONDSBetweenRuns = 60;

	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private Runnable trigger;

	public DataScheduler(final DataBase dataBase) {
		trigger = new Runnable() {
			public void run() {
				dataBase.checkItemsAndHandleThem();
			}
		};
	}

	public void runDataScheduler() {
		scheduler.scheduleAtFixedRate(trigger, 0, numOfSECONDSBetweenRuns, SECONDS);
	}
}
