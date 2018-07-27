package TwoPhaseLocking;

import java.util.*;


public class TransactionClass {
	
	private int transaction_id;
	private int transaction_timestamp;
	private transaction_state tstate ;
	private ArrayList<Character> locked_item_list = new ArrayList<Character>();
	private Queue<String> waiting_operations = new LinkedList<String>();
	
	public enum transaction_state{Active, Blocked, Aborted, Commited};
	
	public int getTransaction_id() {
		return transaction_id;
	}
	public void setTransaction_id(int tid) {
		this.transaction_id = tid;
	}
	public int getTransaction_timestamp() {
		return transaction_timestamp;
	}
	public void setTransaction_timestamp(int timestamp) {
		this.transaction_timestamp = timestamp;
	}
	public void settState(transaction_state state)
	{
		this.tstate = state;
	}
	
	public transaction_state gettState()
	{
		return this.tstate;
	}
	
	public ArrayList<Character> getLocked_item_list() {
		return locked_item_list;
	}
	public void setLocked_item_list(char locked_item) {
		locked_item_list.add(locked_item);
	}
	public Queue<String> getWaiting_operations() {
		return waiting_operations;
	}
	public void setWaiting_operations(String transaction) {
		waiting_operations.add(transaction);
	}
	

}
