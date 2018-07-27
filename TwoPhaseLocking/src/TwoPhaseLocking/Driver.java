package TwoPhaseLocking;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import TwoPhaseLocking.LockClass.lock_state;
import TwoPhaseLocking.TransactionClass.transaction_state;

public class Driver {
	
	//Creating two HashMaps for Transaction Table and Lock Table
	
	static HashMap<Integer, TransactionClass> TransactionTable = new HashMap<>();
	static HashMap<Character, LockClass> LockTable = new HashMap<>();
	static ArrayList<String> Result = new ArrayList<String>();
	//Counter variable to keep track of the transaction time stamps
	static int timestamp = 0;                                            
	public static void main(String[] args) {
		
		
		 ArrayList<String> inputline = new ArrayList<String>();
	        try (BufferedReader br = new BufferedReader(new FileReader("/C:/Users/Damini Singh/eclipse-workspace/TwoPhaseLocking/src/TwoPhaseLocking/input.txt")))
	        {

	            String sCurrentLine;

	            while ((sCurrentLine = br.readLine()) != null) {
	            
	                inputline.add(sCurrentLine);
	            }

	        } catch (IOException e) {
	            e.printStackTrace();
	        } 
	        
     //Iterating through each element of the input data 
     //Depending on the operation type, Calling either of the functions:Begin, Read, Write, End
     for(int i = 0; i <inputline.size();i++)
     {
    	if(inputline.get(i).isEmpty())
    	{
    		continue;
    	}
    	String transaction = inputline.get(i);
    	
    	//Converting each transaction to a character array and 
    	//fetching operation, transaction id and data item from each transaction
    	char[] transaction_char = transaction.toCharArray();
     	char operation = transaction_char[0];
     	int tid = Character.getNumericValue(transaction_char[1]);
     	char data_item = 0;
     	
     	//If Input line encountered, transaction table, lock table and timestamp should be refreshed.
     	if (operation == 'I')
     	{	
     	
     		Result.add("" );
     		Result.add("");
     		Result.add("output");
 			timestamp = 0;
 			TransactionTable.clear();
 			LockTable.clear();
     	}
     	
     	//If Begin transaction is encountered, the Begin Method is called
     	else if (operation == 'b')
     	{	
     		Result.add("OPERATION: " + transaction);
 			timestamp++;
     		Begin(tid);
     	}
     	
     	//If the operation is either Read or Write, then data item is fetched from the input line
     	else if(operation == 'r')
     	{
     		Result.add("OPERATION: " + transaction);
 			data_item = transaction_char[4];
 			Read(tid, data_item,transaction);
     	}
     	
     	else if(operation == 'w')
     	{
     		Result.add("OPERATION: " + transaction);
     		data_item = transaction_char[4];
     		Write(tid, data_item,transaction);
     	}
     	
     	else if (operation == 'e')
     	{	
     		Result.add("OPERATION: " + transaction);
     		EndTransaction(tid,transaction);
     	}
     }
     
     //TO REMOVE BFEORE SUBMITTING
     //This is where Lock Table, Transaction Table, And the final system instructions will be printed.
     System.out.println(LockTable.keySet());
     System.out.println(TransactionTable.keySet());
   
     for(String str: Result) {
    	  System.out.println(str);
     }
     //TO REMOVE BEFORE SUBMITTING
     
	//Writing the elements of Result arraylist to an output text file
	 try {
		FileWriter writer = new FileWriter ("/C:/Users/Damini Singh/eclipse-workspace/TwoPhaseLocking/src/TwoPhaseLocking/out.txt");
		for(String str: Result) {
			if(str.isEmpty())
			{
				 writer.write("\n");
			}
			  writer.write("\n");
			  writer.write(str);
			}
			writer.close();
	 	} 
	 catch (IOException e) {
	 		e.printStackTrace();
	 		}					

	} //End of Main Function
	
	//READ FUNCTION
	public static void Read(int tid, char dataitem, String transaction)
	{
		//If transaction_state == blocked :
		// add operations(input line) in transaction_table.waiting_operations and and set the status as Blocked in Transaction Table.
		
		if(TransactionTable.get(tid).gettState() == transaction_state.Blocked)
		{
			TransactionTable.get(tid).setWaiting_operations(transaction);
			LockTable.get(dataitem).setWaiting_transaction(tid);
			
		}
		else if(TransactionTable.get(tid).gettState() == transaction_state.Aborted)
		{
			Result.add("ACTION TAKEN: Transaction " + tid + " is already aborted so ignoring the action " + transaction);
		}
		//If the item does not exist in the Lock Table, Create a Lock class object and assign following properties to it
		else
		{
		if(!LockTable.containsKey(dataitem))
		{
		LockClass L1 = new LockClass();
		L1.setItem_name(dataitem);
		L1.setTid_list(tid);
		L1.setItemLockstate(lock_state.Read_Locked);
		
		//Adding LockClass object to LockTable
		LockTable.put(dataitem, L1);
		//Also add the Locked item list by current transaction(locked_item_list) in the Transaction class
		TransactionTable.get(tid).setLocked_item_list(dataitem);
		
		Result.add("ACTIONS TAKEN: " + dataitem + " is added to LockTable with Lock status " + LockTable.get(dataitem).getItemLockstate() + " Transaction ID: " + LockTable.get(dataitem).getTid_list() + " Added data item to locked item list in Transaction Table " + TransactionTable.get(tid).getLocked_item_list() );
		}
		//Else if the Item exists in the LockTable
		else
		{	
			if(LockTable.get(dataitem).getItemLockstate() == lock_state.Read_Locked)
			{
				//Add item to Transaction List(tid_list) in Lock Class
				LockTable.get(dataitem).setTid_list(tid);
				
				//Also add the Locked item list by current transaction(locked_item_list) in the Transaction Table
				TransactionTable.get(tid).getLocked_item_list().add(dataitem);
				Result.add("ACTIONS TAKEN: Transaction ID: " + tid + " is updated in the Lock Table. Updated Transaction id list in the Lock Table against " + dataitem + " is" + LockTable.get(dataitem).getTid_list());
				
			}
			else if (LockTable.get(dataitem).getItemLockstate() == lock_state.Write_Locked)
			{	
				if(LockTable.get(dataitem).getTid_list().get(0) == tid)
				{
					LockTable.get(dataitem).setItemLockstate(lock_state.Read_Locked);
					Result.add("ACTIONS TAKEN: Lock State is updated to : " + LockTable.get(dataitem).getItemLockstate() +  " Modified data item " + LockTable.get(dataitem).getItem_name() + "Transaction ID" + LockTable.get(dataitem).getTid_list());
				}
				else
				{   
					waitDie(tid,LockTable.get(dataitem).getTid_list().get(0) ,transaction);
				}
				
			}
		 }	
		} // End of Else
		
	} // End of Read Function
	
	//WRITE FUNCTION
	public static void Write(int tid, char dataitem, String transaction)
	{
		//If transaction_state == blocked :
		// add operations(input line) in transaction_table.waiting_operations and and set the status as Blocked in Transaction Table.
		
		if(TransactionTable.get(tid).gettState() == transaction_state.Blocked)
		{
			TransactionTable.get(tid).setWaiting_operations(transaction);
			LockTable.get(dataitem).setWaiting_transaction(tid);
			
		}
		else if(TransactionTable.get(tid).gettState() == transaction_state.Aborted)
		{
			Result.add("ACTION TAKEN: Transaction " + tid + " is already aborted so ignoring the action " + transaction);
		}
		else
		{
		//If the item does not exist in the Lock Table, Create a Lock class object and assign following properties to it
		if(!LockTable.containsKey(dataitem))
		{
        LockClass L1 = new LockClass();
		L1.setItem_name(dataitem);
		L1.setTid_list(tid);
		L1.setItemLockstate(lock_state.Write_Locked);
		
		//Adding LockClass object to LockTable
		LockTable.put(dataitem, L1);
		//Also add the Locked item list by current transaction(locked_item_list) in the Transaction class
		TransactionTable.get(tid).setLocked_item_list(dataitem);
		
		Result.add("ACTIONS TAKEN: " + dataitem + " is added to LockTablewith Lock status " + LockTable.get(dataitem).getItemLockstate() + " Transaction ID: " + LockTable.get(dataitem).getTid_list() + " Added data item to locked item list in Transaction Table " + TransactionTable.get(tid).getLocked_item_list());
		}
		
		else if(LockTable.containsKey(dataitem))
		{
		//If the item already exists in the Lock Table and holds write Lock, Call Wait Die
		if(LockTable.get(dataitem).getItemLockstate() == lock_state.Write_Locked)
		{
			waitDie(tid, LockTable.get(dataitem).getTid_list().get(0), transaction);
		}
		
		else if (LockTable.get(dataitem).getItemLockstate() == lock_state.Read_Locked)
		{
			//When the already existing element in the Lock  table contains Read Lock
			//And the Transaction Id List in the Lock Table contains the same transactions as the requesting transaction
			
			if(LockTable.get(dataitem).getTid_list().size() == 1 & LockTable.get(dataitem).getTid_list().get(0) == tid) 
			{
				//Read Lock is upgraded to Write lock
				LockTable.get(dataitem).setItemLockstate(lock_state.Write_Locked);
				Result.add("ACTIONS TAKEN: Read Lock is upgraded to Write Lock in the Lock table for data item " + dataitem + " Transaction Id is " + LockTable.get(dataitem).getTid_list());
			}

			else
			{
				//If there are multiple elements holding Read Lock on same item
				//Requesting write Lock transaction should check with all the existing transactions
				//check timestamp,  then call wait die accordingly
				Result.add("Transaction " + tid + " is requesting write lock which is Read Locked by multiple items " + LockTable.get(dataitem).getTid_list());
				int checktimstamp = 0;
				int write_timestamp = TransactionTable.get(tid).getTransaction_timestamp();
				int checkid = 0;
				for(int read_transaction :LockTable.get(dataitem).getTid_list() )
				{	
					if(tid == read_transaction)
					{
						continue;
					}
					else
					{
					checkid = read_transaction;
					int read_timestamp = TransactionTable.get(checkid).getTransaction_timestamp();
					if(write_timestamp > read_timestamp)
					{
						waitDie(tid, read_transaction, transaction);
						checktimstamp++;
						break;
					}
					}
				}
				//If the requesting write transaction has not been aborted, then it will be blocked for data item
				if(checktimstamp == 0)
				{
					waitDie(tid, checkid, transaction);
				}
			}
		}
			
		}// End of Module when element is in the Lock Table	
	  }//end of else
	} // End  of Write Method
	
	//A transaction object is created and status for the Transaction is set to Active
	public static void Begin(int tid)
	
	{	
		//Creating a Transaction Class object with below properties
		TransactionClass T1 = new  TransactionClass();
		T1.setTransaction_id(tid);
		T1.setTransaction_timestamp(timestamp);
		T1.settState(transaction_state.Active);
		
		//Adding TransactionClass Object to Transaction Table
		TransactionTable.put(tid, T1);
		Result.add("ACTIONS TAKEN: Tansaction " + TransactionTable.get(tid).getTransaction_id() + " is added to the transaction table with status " + TransactionTable.get(tid).gettState());
		
	}
	
	public static void Unlock(int transactionid, String  transaction, String state)
	{
		//Get Locked Item List for the Transaction Id from the Transaction table
		ArrayList<Character> lockeditemlist = new ArrayList<Character>();
		lockeditemlist.addAll(TransactionTable.get(transactionid).getLocked_item_list());
		
		//Iterate through each locked item list and fetch waiting transactions from Lock Table
		// From waiting transaction, fetch first waiting transaction.
		//If the first transaction has a read operation associated, execute all read operations.
		//If the first transaction has a write operation
		for(char item: lockeditemlist)
		{
			ArrayList<Integer> waiting_transaction = new ArrayList<Integer>();
			waiting_transaction.addAll(LockTable.get(item).getWaiting_transaction());

			//If there are no waiting operations waiting for the item to be locked
			if(waiting_transaction.size() == 0)
			{
				if(LockTable.get(item).getTid_list().size() == 1)
				{
					//Deleting the data entry from lock table since there are no waiting transactions
					//And the only transaction holding the item has been Aborted/Committed
					LockTable.remove(item);
					Result.add("Entry for " + item + " is released from Lock Table against the transaction " +transactionid + " since it has been " + state);
				}
				
				else
				{	//In case of shared lock , there can  be multiple Transactions holding the item
					//Removing the Aborted/committed transaction id from Lock Table against the data item
					
					LockTable.get(item).removeTid_list(transactionid);
					Result.add("Entry for " + item + " is released from Lock Table against the transaction " +transactionid + " since it has been " + state );

				}
			}
			else
			{	
				//Get the first waiting transaction
				int tid = LockTable.get(item).getWaiting_transaction().peek();
				//PROCESSING THE OPERATIONS OF THE waiting_transaction
				//Get all waiting operations against each waiting transaction			
				Queue<String> waiting_operations = new LinkedList<String>();
				waiting_operations.addAll(TransactionTable.get(tid).getWaiting_operations());
				
				//Removing the first operation from the waiting operation list for transaction
				String operation = waiting_operations.remove();
				char[] transaction_char1 = operation.toCharArray();
		     	char operationvalue = transaction_char1[0];
		     	
		     	if(operationvalue == 'w')
		     	{
		     		if(LockTable.get(item).getItemLockstate() == lock_state.Write_Locked)
		     		{ 		
		     			//Removing the Aborted/committed transaction id from Lock Table against the data item
		     			//transactionid is the committed/aborted transaction
		     			/*1.Unlocked T should be removed from Lock Table
		     			2.Waiting T should be added in Tid_list in Lock Table
		     			3. First waiting T should be removed from Lock Table
		     			4. Transaction State should be set as Active
		     			5. Locked item list should be updated in Transaction Table*/
						LockTable.get(item).removeTid_list(transactionid);				
		     			//Adding the waiting transaction to the tid_list in the Lock Table
		     			LockTable.get(item).setTid_list(tid);
		     			LockTable.get(item).removeWaiting_transaction();
		     			TransactionTable.get(tid).setLocked_item_list(item);
		     			//Setting the status of tid as Active in the Transaction Table
		     			TransactionTable.get(tid).settState(transaction_state.Active);
		     			
		     			Result.add("ACTION TAKEN:Transaction " + transactionid + " releases write lock on " + item + " And Transaction " + tid + " executes " + operation);
		     			//Checking the next operation in the waitingoperation list
		     			String next_operation = waiting_operations.poll();
		     			transaction_char1 = next_operation.toCharArray();
				     	operationvalue = transaction_char1[0];
				     	
				     	if(operationvalue == 'e' )
				     	{
				     		EndTransaction(tid, next_operation);	
				     	} 	
		     		}

		     		//Existing Lock State was Read Locked(Shared Locked)
		     		else
		     		{
		     			if(LockTable.get(item).getItemLockstate() == lock_state.Read_Locked);
		     			//Removing the Aborted/committed transaction id from Lock Table against the data item
						LockTable.get(item).removeTid_list(transactionid);
	     				//In this case we have to check if there are more transactions sharing the read lock
		     			int transactionlistsize = LockTable.get(item).getTid_list().size();
		     			if(transactionlistsize == 0 || (transactionlistsize ==1 && LockTable.get(item).getTid_list().get(0) == tid ) )
			     			{
		     				/*1.Unlocked T should be removed from Lock Table
			     			2.Waiting T should be added in Tid_list in Lock Table
			     			3. First waiting T should be removed from Lock Table
			     			4. Transaction State should be set as Active
			     			5. Locked item list should be updated in Transaction Table*/
			     			LockTable.get(item).setTid_list(tid);
			     			LockTable.get(item).setItemLockstate(lock_state.Write_Locked);	
			     			LockTable.get(item).removeWaiting_transaction();
			     			//Setting the status of tid as Active in the Transaction Table
			     			TransactionTable.get(tid).settState(transaction_state.Active);
			     			Result.add("ACTION TAKEN:Transaction " + transactionid + " releases read lock on " + item + " And Transaction " + tid + " executes " + operation);
			     			
			     			String next_operation = waiting_operations.poll();
			     			transaction_char1 = next_operation.toCharArray();
					     	operationvalue = transaction_char1[0];
					     	
					     	if(operationvalue == 'e' )
					     	{
					     		EndTransaction(tid, next_operation);	
					     	} 
			     			}
		     			else if(transactionlistsize > 1)
			     			{
								//If there are multiple elements holding Read Lock on same item
								//Requesting write Lock transaction should check with all the existing transactions
								int checktimstamp = 0;
								int write_timestamp = TransactionTable.get(tid).getTransaction_timestamp();
								for(int read_transaction : LockTable.get(item).getTid_list())
								{
									int read_timestamp = TransactionTable.get(read_transaction).getTransaction_timestamp();
									if(write_timestamp > read_timestamp)
									{
										waitDie(tid, read_transaction, transaction);
										checktimstamp++;
										break;
									}
								
								//If the requesting write transaction has not been aborted, then it will be blocked for data item
									if(checktimstamp == 0)
									{
										waitDie(tid, LockTable.get(item).getTid_list().get(0), transaction);
									}
								}
			     			}
		     		}
		     	} // End of checking waiting opeartion is Write
		     	
		     	//NEED TO CHECK THIS
		     	else if(operationvalue == 'r')
		     	{
		     		if(LockTable.get(item).getItemLockstate() == lock_state.Write_Locked)
		     		{ 		
		     			//Removing the Aborted/committed transaction id from Lock Table against the data item
		     			//transactionid is the committed/aborted transaction
						LockTable.get(item).removeTid_list(transactionid);
						
		     			//Adding the waiting transaction to the tid_list in the Lock Table
		     			LockTable.get(item).setTid_list(tid);
		     			//Changing the lock status in Lock Table to Write Locked
		     			LockTable.get(item).setItemLockstate(lock_state.Read_Locked);
		     			LockTable.get(item).removeWaiting_transaction();
		     			TransactionTable.get(tid).setLocked_item_list(item);		     	
		     			TransactionTable.get(tid).settState(transaction_state.Active);
		     			
		     			Result.add("ACTION TAKEN:Transaction " + transactionid + " releases write lock on " + item + " And Transaction " + tid + " executes " + operation);
		     			//Checking the next operation in the waitingoperation list
		     			String next_operation = waiting_operations.poll();
		     			transaction_char1 = next_operation.toCharArray();
				     	operationvalue = transaction_char1[0];
				     	
				     	if(operationvalue == 'e' )
				     	{
				     		EndTransaction(tid, next_operation);	
				     	} 	
				     	
				     	//Since the opeartion is read , we have to go through each of the remaining waiting transactions
				     	for(int waiting_transaction1 : LockTable.get(item).getWaiting_transaction())
				     	{
				     		//PROCESSING THE OPERATIONS OF THE waiting_transaction
							//Get all waiting operations against each waiting transaction			
							Queue<String> waiting_operations1 = new LinkedList<String>();
							waiting_operations1.addAll(TransactionTable.get(waiting_transaction1).getWaiting_operations());
							
							//Removing the first operation from the waiting operation list for transaction
							String operation1 = waiting_operations1.remove();
							char[] transaction_char11 = operation1.toCharArray();
					     	char operationvalue1 = transaction_char11[0];
					     	if(operationvalue1 =='r')
					     	{
					     		//Removing the waiting_transaction from waiting Transaction list from Lock Table
								LockTable.get(item).removeWaiting_transaction();
								LockTable.get(item).setTid_list(waiting_transaction1);
								TransactionTable.get(waiting_transaction1).settState(transaction_state.Active);
								
								
								//Checking the next operation in the waitingoperation list
				     			String next_operation1 = waiting_operations1.poll();
				     			transaction_char1 = next_operation1.toCharArray();
						     	operationvalue = transaction_char1[0];
						     	
						     	if(operationvalue == 'e' )
						     	{
						     		EndTransaction(tid, next_operation1);	
						     	} 	
					     	}
				     	}
				     	
		     		}
		     	} // End of checking waiting operation is read
		     	
			}
			
		}//End of iterating over each locked item list
	}
	
	public static void waitDie(int first_tid,int existingtid,String transaction)
	{	
		Result.add("Wait die is called between "+ first_tid + " and " + existingtid);
		int timestamp1 = TransactionTable.get(first_tid).getTransaction_timestamp();
		int timestamp2 = TransactionTable.get(existingtid).getTransaction_timestamp();
		//ArrayList<Character> lockeditemlist = new ArrayList<Character>();
		char[] transaction_char = transaction.toCharArray();
     	char dataitem = transaction_char[4];

		//If Transaction1 is older it waits
		if(timestamp1 < timestamp2)
		{
			TransactionTable.get(first_tid).settState(transaction_state.Blocked);
			TransactionTable.get(first_tid).setWaiting_operations(transaction);
			//Add waiting transaction against the data item in Lock Table
			LockTable.get(dataitem).setWaiting_transaction(first_tid);;
			Result.add("ACTIONS TAKEN: Transaction ID "+ first_tid + " is blocked and status is set to Blocked. Added operation to waiting operations in Transaction Table " + transaction + " for transaction id " + first_tid);
			Result.add("Waiting Transaction " + LockTable.get(dataitem).getWaiting_transaction() +  " has been added in the Lock Table against " + dataitem);
		}
		//If transaction1 is younger, it gets aborted
		else
		{	
			String state = "Aborted";
			//Transaction status is set as Aborted in the Transaction Table
			TransactionTable.get(first_tid).settState(transaction_state.Aborted);
			//All  the items currently locked by Transaction should be unlocked 
			Result.add("Transaction ID " + first_tid + " will be  aborted since its timestamp is more and subsequent actions will be ognored");
			Unlock(first_tid, transaction,state);	
		}
	}
	
	public static void EndTransaction(int tid, String transaction)
	{   
		String state = "Committed";

		if(TransactionTable.get(tid).gettState() == transaction_state.Aborted)
		{
			Result.add("ACTIONS TAKEN: Transaction " + tid + " is already aborted so ignoring the action " + transaction);
		}
		else if(TransactionTable.get(tid).gettState() == transaction_state.Blocked)
		{
			TransactionTable.get(tid).setWaiting_operations(transaction);
			Result.add("ACTIONS TAKEN: Transaction " + tid + " is already blocked, so adding the operation in the waiting operation list in transaction table for transaction " + tid + " Waiting Opeartions " + TransactionTable.get(tid).getWaiting_operations());
		}
		else
		{
		TransactionTable.get(tid).settState(transaction_state.Commited);
		Result.add("ACTIONS TAKEN : operation " + transaction + " is executed. Transaction " + tid + " has been  committed and the status in transaction table has been has been updated to committed" );
		Unlock(tid, transaction,state);
		}
	}
	
} // End of Main Class
