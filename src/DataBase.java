import java.util.Date;
import java.util.LinkedList;

public class DataBase {
	private LinkedList<Task> taskList;
	private LinkedList<Reminder> reminderList;
	private LinkedList<Poll> pollList;
	private DataScheduler seheduler;
	private DataXMLManager dataXMLManager;

	public DataBase() {
		taskList = new LinkedList<Task>();
		reminderList = new LinkedList<Reminder>();
		pollList = new LinkedList<Poll>();
		seheduler = new DataScheduler(this);
		seheduler.runDataScheduler();
		dataXMLManager = new DataXMLManager();
	}

	public void checkItemsAndHandleThem() {
		synchronized (taskList) {
			for (Task task : taskList) {
				if ((task.getDueDate().after(new Date())) && (task.isCompleted() == false)
						&& (task.isTaskExpiredHadBeenNotify() == false)) {
					task.handleExpiredTask();
				}
			}
		}

		synchronized (reminderList) {
			for (Reminder reminder : reminderList) {
				if (reminder.getDateOfReminding().after(new Date()) && (!reminder.isHadBeenSend())) {
					reminder.sendReminder();
				}
			}
		}
	}

	public LinkedList<Task> retrieveTasksByUser(String user) {
		synchronized (taskList) {
			LinkedList<Task> taskUserList = new LinkedList<Task>();

			for (Task task : taskList) {
				if (task.getTaskCreator().equals(user)) {
					taskUserList.add(task);
				}
			}

			return taskUserList;
		}
	}

	public LinkedList<Reminder> retrieveReminderByUser(String user) {
		synchronized (reminderList) {
			LinkedList<Reminder> reminderUserList = new LinkedList<Reminder>();

			for (Reminder reminder : reminderList) {
				if (reminder.getUser().equals(user)) {
					reminderUserList.add(reminder);
				}
			}

			return reminderUserList;
		}
	}

	public LinkedList<Poll> retrievePollsByUser(String user) {
		synchronized (pollList) {
			LinkedList<Poll> pollUserList = new LinkedList<Poll>();

			for (Poll poll : pollList) {
				if (poll.getPollCreator().equals(user)) {
					pollUserList.add(poll);
				}
			}

			return pollUserList;
		}
	}

	public void addTask(Task newTask) {
		synchronized (reminderList) {
			taskList.add(newTask);
		}
		dataXMLManager.addTask(newTask);
	}

	public void addReminder(Reminder newReminder) {
		synchronized (reminderList) {
			reminderList.add(newReminder);
		}
		dataXMLManager.addReminder(newReminder);	
	}

	public synchronized void addPoll(Poll newPoll) {
		synchronized (pollList) {
			pollList.add(newPoll);
		}
		dataXMLManager.addPoll(newPoll);
	}

	public LinkedList<Task> getTaskList() {
		synchronized (taskList) {
			return taskList;
		}
	}

	public void setTaskList(LinkedList<Task> taskList) {

		this.taskList = taskList;
	}

	public LinkedList<Reminder> getReminderList() {
		synchronized (reminderList) {
			return reminderList;
		}
	}

	public void setReminderList(LinkedList<Reminder> reminderList) {
		this.reminderList = reminderList;
	}

	public LinkedList<Poll> getPollList() {
		synchronized (pollList) {
			return pollList;
		}
	}

	public void setPollList(LinkedList<Poll> pollList) {
		this.pollList = pollList;
	}

	public DataScheduler getSeheduler() {
		return seheduler;
	}

	public void setSeheduler(DataScheduler seheduler) {
		this.seheduler = seheduler;
	}

	public DataXMLManager getDataXMLManager() {
		return dataXMLManager;
	}

	public void setDataXMLManager(DataXMLManager dataXMLManager) {
		this.dataXMLManager = dataXMLManager;
	}
}
