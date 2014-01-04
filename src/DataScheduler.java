import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.*;

public class DataScheduler {

	public static int numOfSECONDSBetweenRuns = 60;

	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private LinkedList<Task> taskList;
	private LinkedList<Reminder> reminderList;
	private LinkedList<Poll> pollList;
	private Runnable trigger;

	public DataScheduler() {
		taskList = new LinkedList<Task>();
		reminderList = new LinkedList<Reminder>();
		pollList = new LinkedList<Poll>();
		trigger = new Runnable() {
			public void run() {
				checkItemsAndHandleThem();
			}
		};
	}

	public void runDataScheduler() {
		scheduler.scheduleAtFixedRate(trigger, 0, numOfSECONDSBetweenRuns, SECONDS);
	}

	private void checkItemsAndHandleThem() {

		for (Task task : taskList) {
			if ((task.getDueDate().after(new Date())) && (task.isCompleted() == false)
					&& (task.isTaskExpiredHadBeenNotify() == false)) {
				task.handleExpiredTask();
			}
		}

		for (Reminder reminder : reminderList) {
			if (reminder.getDateOfReminding().after(new Date()) && (!reminder.isHadBeenSend())) {
				reminder.sendReminder();
			}
		}

		// for (Poll poll : pollList) {
		// if ()) {
		//
		// }
		// }
	}

	// need to synchronized lists.
	public LinkedList<Task> retrieveTasksByUser(String user) {

		LinkedList<Task> taskUserList = new LinkedList<Task>();

		for (Task task : taskList) {
			if (task.getTaskCreator().equals(user)) {
				taskUserList.add(task);
			}
		}

		return taskUserList;
	}

	public LinkedList<Reminder> retrieveReminderByUser(String user) {
		LinkedList<Reminder> reminderUserList = new LinkedList<Reminder>();

		for (Reminder reminder : reminderList) {
			if (reminder.getUser().equals(user)) {
				reminderUserList.add(reminder);
			}
		}

		return reminderUserList;
	}

	public LinkedList<Poll> retrievePollsByUser(String user) {
		LinkedList<Poll> pollUserList = new LinkedList<Poll>();

		for (Poll poll : pollList) {
			if (poll.getUser().equals(user)) {
				pollUserList.add(poll);
			}
		}

		return pollUserList;
	}

	public void addTask(Task newTask) {
		taskList.add(newTask);
	}

	public void addReminder(Reminder newReminder) {
		reminderList.add(newReminder);
	}

	public void addPoll(Poll newPoll) {
		pollList.add(newPoll);
	}

	public LinkedList<Task> getTaskList() {
		return taskList;
	}

	public void setTaskList(LinkedList<Task> taskList) {
		this.taskList = taskList;
	}

	public LinkedList<Reminder> getReminderList() {
		return reminderList;
	}

	public void setReminderList(LinkedList<Reminder> reminderList) {
		this.reminderList = reminderList;
	}

	public LinkedList<Poll> getPollList() {
		return pollList;
	}

	public void setPollList(LinkedList<Poll> pollList) {
		this.pollList = pollList;
	}

}
