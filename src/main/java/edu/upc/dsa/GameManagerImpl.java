package edu.upc.dsa;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import edu.upc.dsa.exceptions.EmailAlreadyInUseException;
import edu.upc.dsa.exceptions.IncorrectPasswordException;
import edu.upc.dsa.exceptions.UserNotRegisteredException;
import edu.upc.dsa.models.Credentials;
import edu.upc.dsa.models.Item;
import edu.upc.dsa.models.User;
import org.apache.log4j.Logger;

public class GameManagerImpl implements GameManager {
    private static GameManager instance;
    final static Logger logger = Logger.getLogger(GameManagerImpl.class);
    private HashMap<String, User> UsersMap;
    protected List<User> users;
    protected List<Item> items;
    protected List<User> logged;
    public List<User> getUsers(){
        return users;
    }
    public GameManagerImpl() {
        this.users = new LinkedList<>();
        this.logged = new LinkedList<>();
        this.items = new LinkedList<>();
        UsersMap = new HashMap<String, User>();
    }
    public static GameManager getInstance() {
        if (instance==null) instance = new GameManagerImpl();
        return instance;
    }

    public int size() {
        int ret = this.users.size();
        logger.info("size " + ret);

        return ret;
    }

    @Override
    public User register(User user) throws EmailAlreadyInUseException {
        User user1 = UsersMap.get(user.getEmail());
        if (user1 == null)
        {
            this.users.add(user);
            this.UsersMap.put(user.getEmail(), user);
            logger.info("User registered");
            return user;
        } else
        {
            logger.info("This email is already being used");
            throw new EmailAlreadyInUseException();
        }
    }
    @Override
    public User login(Credentials credentials) throws UserNotRegisteredException, IncorrectPasswordException {
        User user = UsersMap.get(credentials.getEmail());
        if (user == null) {

            throw new UserNotRegisteredException();
        }
        if (!user.getPassword().equals(credentials.getPassword())) {

            throw new IncorrectPasswordException();
        }
        return user;
    }
    public List<Item> Shop ()
    {
        items.add (new Item("Potion","Recover 50 health points",15));
        items.add(new Item("Sword","Increase damage by 20 points",35));
        logger.info("Items added to the Shop");
        return items;
    }
    @Override
    public int UserNumber() {
        return this.users.size();
    }
    @Override
    public int ItemNumber(){return this.items.size();}
    @Override
    public int LoggedNumber(){return this.logged.size();}
}