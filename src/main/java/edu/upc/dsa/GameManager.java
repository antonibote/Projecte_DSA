
package edu.upc.dsa;

import edu.upc.dsa.exceptions.*;
import edu.upc.dsa.models.*;

import java.sql.SQLException;
import java.util.List;

public interface GameManager {
    int size();
    User register(User user) throws EmailAlreadyInUseException;
    //User login(String email, String password) throws UserNotRegisteredException, IncorrectPasswordException;
    String login(Credentials credentials) throws UserNotRegisteredException, IncorrectPasswordException;
    List<User> getUsers();
    int UserNumber();
    int ItemNumber();
    int LoggedNumber();
    int FAQsNumber();
    List<Item> Shop();
    List<Inventory> Inventory();
    List<FAQ> getFAQs();
    FAQ addFAQ(FAQ faq);
    List<Message> getMessages();
    Message addMessage(Message message);
}
