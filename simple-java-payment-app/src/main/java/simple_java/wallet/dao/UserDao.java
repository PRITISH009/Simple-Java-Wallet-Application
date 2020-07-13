package simple_java.wallet.dao;
import java.util.List;
import simple_java.wallet.bean.User;

public interface UserDao{
	void registerUser(User user);
	boolean validateUser(String username, String password);
	boolean checkIfUserExists(String username);
	boolean checkIfAccountExists(int accountNumber);
	int showBalance(int accountNo);
	void creditToOwnAccount(int accountNo, int amount);
	void sendMoney(int senderAccountNo, int recieverAccountNo, int amount);
	List<Integer>getAllUserAccounts();
	
}
