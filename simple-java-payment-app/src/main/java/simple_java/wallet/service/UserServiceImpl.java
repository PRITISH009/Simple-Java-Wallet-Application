package simple_java.wallet.service;
import java.util.List;

import simple_java.wallet.bean.User;
import simple_java.wallet.dao.UserDaoImpl;
import simple_java.wallet.ui.App;

public class UserServiceImpl implements UserService{
	
	UserDaoImpl dao = new UserDaoImpl();
	private static String username;

	public static String getUsername() {
		return username;
	}

	private static void setUsername(String username) {
		UserServiceImpl.username = username;
	}
	
	private int getAmount(int accountNo) {
		boolean amountCheckRequired = true;
		int amount;
		int accountBalance = dao.showBalance(accountNo);
		do {
			System.out.println("Please Enter the amount you want to Transfer..");
			amount = Integer.parseInt(App.getSc().nextLine());
			
			if(amount < 0) {
				System.out.println("Invalid Amount.. Please try again");
			}else if(amount > accountBalance) {
				System.out.println("Insufficient balance... Please add a suitable amount");
			}else {
				amountCheckRequired = false;
			}
		}while(amountCheckRequired);
		
		return amount;
	}
	
	private int getAccountNumber() {
		
		List<Integer>accounts = dao.getAllUserAccounts();
		int accountNo;
		
		if(accounts.size() > 1) {
			int choice;
			boolean accountCheckRequired = true;
			do {
				
				System.out.println("Please select an account");
				int index = 1;
				
				for(int account : accounts) {
					System.out.println("Press " + index + " to select account number " + account);
					index++;
				}
				
				choice= Integer.parseInt(App.getSc().nextLine());

				if(choice < 1 || choice >accounts.size()) {
					System.out.println("Invalid Input.. Please select from the given Menu\n");
				}else {
					accountCheckRequired = false;
				}
				
			}while(accountCheckRequired);	
			accountNo = accounts.get(choice-1);
		
		}else {
			accountNo = accounts.get(0);
		}
		return accountNo;
	}
	
	//All User Services are below
	@Override
	public void App(){
		// This method interacts with the user.. 
		int choice;
		
		//Show Menu
		do {
			System.out.println("Press 1 or 2 to Register/Login respectively");
			choice = Integer.parseInt(App.getSc().nextLine());
			
			switch(choice)
			{
				case 1:
					registerUser();
					break;
					
				case 2:
					login();
					break;
				
				default:
					continue;	
			}
			
		}while(choice != 1 && choice != 2);
		
	}

	@Override
	public void registerUser() {
		// Register User by asking for User details
		User newUser = new User();
		boolean uniqueUserIdCheckRequired = true;
		String username;
		int accountNumber;
		System.out.println("Please Enter your following details");
		
		do {
			
			System.out.println("A unique UserID");
			username = App.getSc().nextLine();
			
			if(!dao.checkIfUserExists(username)) {
				newUser.setUserID(username);
				uniqueUserIdCheckRequired = false;
			}else {
				System.out.println("Please Try a different UserID as this one already exists");
			}
		
			}while(uniqueUserIdCheckRequired);
		
		String password1;
		String password2;
		boolean checkRequired=true;
		do {
			System.out.println("Please Enter your Password");
			password1 = App.getSc().nextLine();
			
			System.out.println("Please Re-Enter your Password");
			password2 = App.getSc().nextLine();
			
			if(password1.equals(password2)) {
				newUser.setPassword(password1);
				checkRequired = false;
			}
			else {
				System.out.println("Passwords don't match... Please enter the same passwords.\n");
			}
			
		}while(checkRequired);
		
		boolean uniqueAccountCheckRequired = true;
		
		do {
			
			System.out.println("Enter your Account Number");
			accountNumber = Integer.parseInt(App.getSc().nextLine());
			
			if(!dao.checkIfAccountExists(accountNumber)) {
				uniqueAccountCheckRequired = false;
				newUser.setAccountNo(accountNumber);
			}
			else {
				System.out.println("This Account is already registered.. Please Enter a different account");
			}
			
		}while(uniqueAccountCheckRequired);
		
		System.out.println("Registering User.... Please Wait");
		dao.registerUser(newUser);
		
		login();
	}

	@Override
	public void login() {
		// Validate User based on User name and Password entered..
		
		String username;
		String password;
		Boolean checkRequired = true;
		
		do {
			//Ask UserID and Password
			System.out.println("Please Enter your UserID ");
			username = App.getSc().nextLine();
			
			System.out.println("Please Enter your Password");
			password = App.getSc().nextLine();
			
			//ValidateUser
			if(dao.validateUser(username, password)) {
				checkRequired = false;
				setUsername(username);
			}else {
				System.out.println("Either Your User ID or Password was incorrect.. Please re-enter you credentials");
			}
			checkRequired = false;
			
		}while(checkRequired);
		
		showServices();
	}

	@Override
	public void showServices() {
		
		System.out.println("Welcome to the payment Applicaion.....");
		int choice;
		
		do {
			System.out.println("Press 1 if you want to view your balance");
			System.out.println("Press 2 if you want to add money to your account");
			System.out.println("Press 3 if you want to send money to a different account");
			System.out.println("Press 0 if you want If you want to exit");
			choice = Integer.parseInt(App.getSc().nextLine());
			
			switch (choice) {
				case 0:
					System.out.println("Thank You for using the Application.. Hope you have a great day ahead");
					break;
				
				case 1:
					System.out.println("Show Balance");
					showBalance();
					break;
					
				case 2:
					System.out.println("Add money to Bank Account");
					creditToAccount();
					break;
				
				case 3:
					System.out.println("Send Money to a different registered user");
					sendMoney();
					break;
				
				default:
					break;
			}
			
		}while(choice != 0);
		
	}

	@Override
	public void showBalance() {
		int accountNo = getAccountNumber();
		System.out.println("Your current balance for Account No " + accountNo + " is  - " + dao.showBalance(accountNo));
	}

	@Override
	public void creditToAccount() {
		int accountNo = getAccountNumber();
		System.out.println("Please Enter the amount you want to credit to your account");
		int amount = Integer.parseInt(App.getSc().nextLine());
		dao.creditToOwnAccount(accountNo, amount);
	}

	@Override
	public void sendMoney() {
		int senderAccountNo = getAccountNumber();
		int amount;
		
		int recieverAccountNo;
		boolean recieverAccountCheckRequired = true;
		do {
			System.out.println("Enter the Account Number you want to Transfer Money to");
			recieverAccountNo = Integer.parseInt(App.getSc().nextLine());
			
			if(dao.checkIfAccountExists(recieverAccountNo)) {
				//pay to the account.. Check the amount that is being requested to transfer...
				amount = getAmount(recieverAccountNo);
				dao.sendMoney(senderAccountNo, recieverAccountNo, amount);
				recieverAccountCheckRequired = false;
			}else {
				System.out.println("The Account you are trying to Transfer money to doesn't Exists... Please Enter a correct Account Number");
			}
		}while(recieverAccountCheckRequired);
	}

}
