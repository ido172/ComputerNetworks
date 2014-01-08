import java.util.Date;
import java.util.LinkedList;

public class DataBase {

	private LinkedList<Task> taskList;
	private LinkedList<Reminder> reminderList;
	private LinkedList<Poll> pollList;
	private DataScheduler seheduler;
	private DataXMLManager dataXMLManager;
	private IDCounter iDCounter;

	public DataBase() {
		taskList = new LinkedList<Task>();
		reminderList = new LinkedList<Reminder>();
		pollList = new LinkedList<Poll>();
		dataXMLManager = new DataXMLManager();
		taskList = dataXMLManager.retrieveTasks();
		reminderList = dataXMLManager.retrieveReminders();
		pollList = dataXMLManager.retrievePolls();
		seheduler = new DataScheduler(this);
		seheduler.runDataScheduler();
		iDCounter = new IDCounter();
	}

	public void checkItemsAndHandleThem() {
		synchronized (taskList) {
			for (Task task : taskList) {
				if ((task.getDueDate().before(new Date()) || (task.getDueDate().equals(new Date())))
						&& !task.isCompleted() && !task.isTaskExpiredHadBeenNotify()) {

					task.handleExpiredTask();
					// Task temp = task;
					// // this.dataXMLManager.deleteTaskFromXML(task);
					// // this.getTaskList().remove(task);
					// temp.handleExpiredTask();
					// this.addTask(temp);
				}
			}
		}

		synchronized (reminderList) {
			for (Reminder reminder : reminderList) {
				if ((reminder.getDateOfReminding().before(new Date()) || (reminder.getDateOfReminding()
						.equals(new Date()))) && !reminder.isHadBeenSend()) {

					Reminder temp = new Reminder(reminder);
					reminderList.remove(reminder);

					System.out.println("fedsjv11111111111111");
					this.dataXMLManager.deleteReminderFromXML(reminder);
					System.out.println("fedsjv2222222222222");
					;
					temp.sendReminder();
					this.addReminder(temp);
					System.out.println("fedsjv");
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
		synchronized (taskList) {
			taskList.add(newTask);
		}
		dataXMLManager.addTask(newTask);
	}

	public void deleteTask(Task taskToDelete) {
		synchronized (taskList) {
			taskList.remove(taskToDelete);
		}
		dataXMLManager.deleteTaskFromXML(taskToDelete);
	}

	public void editTask(Task newTask, Task oldTask) {

		taskList.remove(oldTask);
		taskList.add(newTask);

		dataXMLManager.deleteTaskFromXML(oldTask);
		dataXMLManager.addTask(newTask);
	}

	public void closeTask(int id){
		Task completedTask = retriveTaskByID(id);
		dataXMLManager.deleteTaskFromXML(completedTask);
		completedTask.taskHadBeenCompleted();
		dataXMLManager.addTask(completedTask);
		
	}
	
	public void editTask(int oldFormatTaskId, Task newFormatTask) {

		Task oldTask = retriveTaskByID(oldFormatTaskId);
		taskList.remove(oldTask);
		taskList.add(newFormatTask);

		dataXMLManager.deleteTaskFromXML(oldTask);
		dataXMLManager.addTask(newFormatTask);
	}
	
	public void addReminder(Reminder newReminder) {
		synchronized (reminderList) {
			reminderList.add(newReminder);
		}
		dataXMLManager.addReminder(newReminder);
	}

	public void deleteReminder(Reminder reminderToDelete) {
		synchronized (reminderList) {
			reminderList.remove(reminderToDelete);
		}
		dataXMLManager.deleteReminderFromXML(reminderToDelete);
	}

	public void deleteReminder(int reminderToDeleteID) {
		
		Reminder reminderToDelete = null;
		
		synchronized (reminderList) {
			reminderToDelete = retriveReminderByID(reminderToDeleteID);
			reminderList.remove(reminderToDelete);
		}
		dataXMLManager.deleteReminderFromXML(reminderToDelete);
	}
	
	public void editReminder(Reminder newReminder, Reminder oldReminder) {

		reminderList.remove(oldReminder);
		reminderList.add(newReminder);

		dataXMLManager.deleteReminderFromXML(oldReminder);
		dataXMLManager.addReminder(newReminder);
	}

	public void editReminder(int oldFormatReminderId, Reminder newFormatReminder) {
		
		Reminder oldReminder = retriveReminderByID(oldFormatReminderId);
		reminderList.remove(oldReminder);
		reminderList.add(newFormatReminder);

		dataXMLManager.deleteReminderFromXML(oldReminder);
		dataXMLManager.addReminder(newFormatReminder);
	}

	public synchronized void addPoll(Poll newPoll) {
		synchronized (pollList) {
			pollList.add(newPoll);
		}
		dataXMLManager.addPoll(newPoll);
	}

	public void deletePoll(Poll pollToDelete) {
		synchronized (pollList) {
			pollList.remove(pollToDelete);
		}
		dataXMLManager.deletePollFromXML(pollToDelete);
	}

	public void editPoll(Poll newPoll, Poll oldPoll) {

		pollList.remove(oldPoll);
		pollList.add(newPoll);

		dataXMLManager.deletePollFromXML(oldPoll);
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
		return reminderList;
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

	public void setiDCounter(IDCounter iDCounter) {
		this.iDCounter = iDCounter;
	}

	public Poll retrivePollByID(int id) {
		Poll returndPoll = null;

		synchronized (pollList) {
			for (Poll _poll : pollList) {
				if (_poll.getId() == id) {
					returndPoll = _poll;
					break;
				}
			}
		}

		return returndPoll;
	}

	public Reminder retriveReminderByID(int id) {
		Reminder returndReminder = null;

		synchronized (reminderList) {
			for (Reminder _reminder : reminderList) {
				if (_reminder.getId() == id) {
					returndReminder = _reminder;
					break;
				}
			}
		}

		return returndReminder;
	}

	public Task retriveTaskByID(int id) {
		Task returndTask = null;

		synchronized (taskList) {
			for (Task _task : taskList) {
				if (_task.getId() == id) {
					returndTask = _task;
					break;
				}
			}
		}

		return returndTask;
	}

	public void participantHadAnswerPoll(int pollID, String pollParticipantName) {

		Poll poll = retrivePollByID(pollID);
		Poll oldPoll = poll.duplicatePoll();
		poll.participantHadAnswer(pollParticipantName);

		dataXMLManager.deletePollFromXML(oldPoll);
		dataXMLManager.addPoll(poll);
	}
	
	public int getNewID(){
		return iDCounter.getCounterAndIncreaseByOne();
	}
}
