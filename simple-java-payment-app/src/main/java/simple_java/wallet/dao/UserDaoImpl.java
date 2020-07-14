package simple_java.wallet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import simple_java.wallet.bean.User;
import simple_java.wallet.service.UserServiceImpl;
import simple_java.wallet.ui.App;

public class UserDaoImpl implements UserDao{
	
	Connection connection = App.getConnection();
	@Override
	public void registerUser(User user) {
		
		String insertUser = "insert into userdetails values(?,?)";
		String insertUserAccount = "insert into useraccounts values(?, ?, ?)";
		
		try {
			//Inserting UserId and Password
			PreparedStatement pr = connection.prepareStatement(insertUser);
			pr.setNString(1, user.getUserID());
			pr.setNString(2,user.getPassword());
			pr.execute();
			
			//Setting up Account related to the given User object
			pr = connection.prepareStatement(insertUserAccount);
			pr.setInt(1, user.getAccountNo()); // Setting User Account No.
			pr.setString(2, user.getUserID()); // Setting User Id the account is related to
			pr.setInt(3, 1000); // Setting a default balance for now.
			pr.execute();
			
			
		} catch (SQLException e) {
			System.out.println("Error... in registerUser.. Coudn't register User or User Account");
			e.printStackTrace();
		}
	}

	@Override
	public boolean validateUser(String username, String password){
		//SQL Query that returns 1 if a user with the given userID and password EXISTS else 0
		String validateQuery = "select exists(select UserID from userdetails where UserID = ? and Password = ?)";
		
		try {
			
			PreparedStatement pr = connection.prepareStatement(validateQuery);
			pr.setString(1,  username);
			pr.setString(2, password);
			ResultSet result = pr.executeQuery();
			
			result.next();
			return (result.getString(1).contentEquals("1") ? true : false);
			
		} catch (SQLException e) {
			System.out.println("Problem in validating User... in validateUser");
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean checkIfUserExists(String username){
		
		String userExistsQuery = "select exists(select * from userdetails where UserID = ?)";
		
		try {
			
			PreparedStatement pr = connection.prepareStatement(userExistsQuery);
			pr.setString(1, username);
			ResultSet result = pr.executeQuery();
			
			result.next();
			System.out.println("Checking if User exists");
			System.out.println("Result  - " + (result.getString(1).contentEquals("1") ? true : false));
			return result.getString(1).contentEquals("1") ? true : false;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public boolean checkIfAccountExists(int accountNumber) {
		String checkAccountExistsQuery = "select exists(select * from useraccounts where AccountNo = ?)";
		
		try {
			PreparedStatement pr = connection.prepareStatement(checkAccountExistsQuery);
			pr.setInt(1,  accountNumber);
			ResultSet result = pr.executeQuery();
			result.next();
			
			return result.getString(1).contentEquals("1") ? true : false;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public int showBalance(int accountNo) {
		String checkBalanceQuery = "Select AccountBalance from useraccounts where AccountNo = ?";
		try {
			PreparedStatement pr = connection.prepareStatement(checkBalanceQuery);
			pr.setInt(1, accountNo);
			
			ResultSet result = pr.executeQuery();
			result.next();
			return result.getInt(1);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public void sendMoney(int senderAccountNo, int recieverAccountNo, int amount) {
		String debitMoneyFromSenderQuery = "update useraccounts "
				+ "set AccountBalance = AccountBalance - ? "
				+ "where AccountNo = ? ;";
		
		String creditMoneyToRecieverQuery = "update useraccounts  "
				+ "set AccountBalance = AccountBalance + ? "
				+ "where AccountNo = ?;";
		
		String logTransactionQuery = "insert into transactionlogs(SenderAcc, RecieverAcc, Amount) values(?,?,?);";
		
		// To Debit Money from Sender
		try {
			PreparedStatement prDebit = connection.prepareStatement(debitMoneyFromSenderQuery);
			prDebit.setInt(1, amount);
			prDebit.setInt(2,  senderAccountNo);
			prDebit.executeUpdate();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// For Credit Money to Receiver
		try {
			PreparedStatement prCredit = connection.prepareStatement(creditMoneyToRecieverQuery);
			prCredit.setInt(1, amount);
			prCredit.setInt(2, recieverAccountNo);
			prCredit.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			PreparedStatement log = connection.prepareStatement(logTransactionQuery);
			log.setInt(1, senderAccountNo);
			log.setInt(2, recieverAccountNo);
			log.setInt(3, amount);
			log.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public List<Integer> getAllUserAccounts() {
		String getAccountsQuery = "Select * from useraccounts where UserID = ?";
		List<Integer>accounts = new ArrayList<Integer>();
		try {
			PreparedStatement pr = connection.prepareStatement(getAccountsQuery);
			pr.setString(1, UserServiceImpl.getUsername());
			ResultSet result = pr.executeQuery();
			
			while(result.next()) {
				System.out.println(result.getInt(1));
				accounts.add(result.getInt(1));
			}
		} catch (SQLException e) {
			System.out.println("Problem in getting all User accounts");
			e.printStackTrace();
		}
		return accounts;
	}

	@Override
	public void creditToOwnAccount(int accountNo, int amount) {
		String addMoneyToAccountQuery = "update useraccounts set AccountBalance = AccountBalance + ? where AccountNo = ?";
		String creditToOwnAccountLogQuery = "insert into transactionlogs(SenderAcc, RecieverAcc, Amount) values(?,?,?)";
		try {
			PreparedStatement pr = connection.prepareStatement(addMoneyToAccountQuery);
			pr.setInt(1, amount);
			pr.setInt(2, accountNo);
			pr.executeUpdate();
			pr.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			PreparedStatement log = connection.prepareStatement(creditToOwnAccountLogQuery);
			log.setInt(1,accountNo);
			log.setInt(2, accountNo);
			log.setInt(3, amount);
			log.executeUpdate();
			log.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
