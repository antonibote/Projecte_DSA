package edu.upc.dsa;


import edu.upc.dsa.CRUD.DAO.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


import edu.upc.dsa.CRUD.Session;
import edu.upc.dsa.exceptions.*;
import edu.upc.dsa.models.*;
import io.swagger.models.auth.In;
import org.apache.log4j.Logger;

public class GameManagerImpl implements GameManager {
    private static GameManager instance;
    final static Logger logger = Logger.getLogger(GameManagerImpl.class);
    private HashMap<String, User> usersMap;
    protected List<User> users;
    protected List<Item> items;
    protected List<User> logged;
    private HashMap<String, User> UsersMap;
    protected List<FAQ> faqs;
    protected List<Message> messages;
    public List<User> getUsers(){
        return users;
    }

    public GameManagerImpl() {
        this.users = new LinkedList<>();
        this.logged = new LinkedList<>();
        this.items = new LinkedList<>();
        usersMap = new HashMap<>();
        UsersMap = new HashMap<String, User>();
        this.faqs = new ArrayList<>();
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
            String idUser = user.getIdUser();
            String name= user.getName();
            String surname= user.getSurname();
            String email= user.getEmail();
            String password= user.getPassword();
            IUserDAO userDAO = new UserDAOImpl();
            userDAO.addUser(idUser,name, surname,email,password );
            logger.info("User registered");
            return user;
        } else
        {
            logger.info("This email is already being used");
            throw new EmailAlreadyInUseException();
        }
    }
    @Override
    public String login(Credentials credentials) throws UserNotRegisteredException, IncorrectPasswordException {
        try {
            IUserDAO userDAO = new UserDAOImpl();
            HashMap<String, String> credentialsHash = new HashMap<>();
            credentialsHash.put("email", credentials.getEmail());
            credentialsHash.put("password", credentials.getPassword());
            User user1 = userDAO.getUserByEmail(credentials.getEmail());
            logger.info(user1.getEmail());
            logger.info(user1.getPassword());
            if (user1.getPassword().equals(credentials.getPassword())) {
                logger.info("Succesful login " + credentials.getEmail());
                logger.info("id: "+user1.getIdUser());
                return user1.getIdUser();
            } else if (user1.getEmail() == null) {
                logger.info("User not registered");
                throw new UserNotRegisteredException();
            }
        } catch (Exception e) {
            logger.info("Something went wrong");
        }

        logger.warn("Incorrect user name or password");
        throw new IncorrectPasswordException();
    }
    public List<Item> Shop ()
    {
        logger.info("Loading items...");
        IItemDAO itemDAO = new ItemDAOImpl();
        List<Item> daoItems = itemDAO.getItems();
        return daoItems;
    }
    public List<Inventory> Inventory ()
    {
        logger.info("Loading inventory...");
        IInventoryDAO inventoryDAO = new InventoryDAOImpl();
        List<Inventory> daoInventory = inventoryDAO.getInventoryitems();
        return daoInventory;
    }
    public List<FAQ> getFAQs(){
        List<FAQ> faqs = this.faqs;
        return faqs;
    }
    public FAQ addFAQ(FAQ faq){
        logger.info("Adding FAQs...");
        this.faqs.add(faq);
        return faq;
    }

    public List<Message> getMessages(){
        List<Message> message = this.messages;
        return message;
    }

    public Message addMessage(Message message){
        logger.info("Adding Messages...");
        this.messages.add(message);
        return message;
    }

    public int FAQsNumber(){
        return this.faqs.size();
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