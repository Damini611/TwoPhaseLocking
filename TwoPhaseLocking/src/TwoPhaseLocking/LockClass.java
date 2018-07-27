package TwoPhaseLocking;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class LockClass {

	private char item_name;
	private lock_state itemLockstate;
	private ArrayList<Integer> tid_list = new ArrayList<Integer>();
	private Queue<Integer> waiting_transaction = new LinkedList<Integer>();
	public enum lock_state{Read_Locked, Write_Locked};
	
	public char getItem_name() {
		return item_name;
	}
	public void setItem_name(char item_name) {
		this.item_name = item_name;
	}
	public lock_state getItemLockstate() {
		return itemLockstate;
	}
	public void setItemLockstate(lock_state itemLockstate) {
		this.itemLockstate = itemLockstate;
	}
	public ArrayList<Integer> getTid_list() {
		return tid_list;
	}
	public void setTid_list(int tid) {
		tid_list.add(tid);
	}
	public void removeTid_list(int tid) {
		tid_list.remove(Integer.valueOf(tid));
	}
	public Queue<Integer> getWaiting_transaction() {
		return waiting_transaction;
	}
	public void setWaiting_transaction(int first_tid) {
		waiting_transaction.add(first_tid);
	}
	public void removeWaiting_transaction() {
		waiting_transaction.remove();
	}

}
