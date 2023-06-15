package edu.upc.dsa.CRUD.DAO;


import edu.upc.dsa.CRUD.FactorySession;
import edu.upc.dsa.CRUD.Session;
import edu.upc.dsa.exceptions.InsufficientMoneyException;
import edu.upc.dsa.exceptions.NonExistentItemException;
import edu.upc.dsa.exceptions.NotInInventoryException;
import edu.upc.dsa.exceptions.UserNotRegisteredException;
import edu.upc.dsa.models.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

public class UserDAOImpl implements IUserDAO {
    protected List<Report> reports;
    final static Logger logger = Logger.getLogger(UserDAOImpl.class);
    private static UserDAOImpl instance;
    public static UserDAOImpl getInstance() {
        if (instance==null) instance = new UserDAOImpl();
        return instance;
    }
    public int addUser(String idUser, String name, String surname, String email, String password) {
        Session session = null;
        int employeeID = 0;
        try {
            session = FactorySession.openSession();
            User user = new User(idUser,name, surname, email, password);
            session.save(user);
        }
        catch (Exception e) {
            // LOG
        }
        finally {
            session.close();
        }

        return employeeID;
    }


    public User getUser(String idUser) {
        Session session = null;
        User user = null;
        try {
            session = FactorySession.openSession();
            user = (User)session.get(User.class, "idUser", idUser);
        }
        catch (Exception e) {
            e.printStackTrace();
            // LOG
        }
        finally {
            session.close();
        }
        logger.info("id: "+ user.getIdUser());
        return user;
    }
    public User getUserByEmail(String email) {
        Session session = null;
        User user = null;
        try {
            session = FactorySession.openSession();
            user = (User)session.get(User.class, "email", email);
        }
        catch (Exception e) {
            e.printStackTrace();
            // LOG
        }
        finally {
            session.close();
        }

        return user;
    }

    public void deleteEmployee(String employeeID) {
        User employee = this.getUser(employeeID);
        Session session = null;
        try {
            session = FactorySession.openSession();
            session.delete(User.class);
        }
        catch (Exception e) {
            // LOG
        }
        finally {
            session.close();
        }

    }
    public List<Item> getItems() {
        Session session = null;
        List<Item> items=null;
        try {
            session = FactorySession.openSession();
            items = session.findAll(User.class);
        }
        catch (Exception e) {
            // LOG
        }
        finally {
            session.close();
        }
        return items;
    }

    public List<User> getEmployees() {

        Session session = null;
        List<User> employeeList=null;
        try {
            session = FactorySession.openSession();
            employeeList = session.findAll(User.class);
        }
        catch (Exception e) {
            // LOG
        }
        finally {
            session.close();
        }
        return employeeList;
    }
    public List<User> getEmployeeByDept(int deptID) {

        Session session = null;
        List<User> employeeList=null;
        try {
            session = FactorySession.openSession();

            HashMap<String, Integer> params = new HashMap<String, Integer>();
            params.put("deptID", deptID);

            employeeList = session.findAll(User.class, params);
        }
        catch (Exception e) {
            // LOG
        }
        finally {
            session.close();
        }
        return employeeList;
    }
    public void buyItem(String idItem, String name, String idUser) throws InsufficientMoneyException, NonExistentItemException,  SQLException {

        logger.info("Buying item "+ idItem + " for User " +idUser);
        Session session = null;
        IItemDAO itemDAO = new ItemDAOImpl();
        Item item = itemDAO.getItem(idItem);
        User user = getUser(idUser);
        logger.info(item.getPrice());
        try {
            session = FactorySession.openSession();
            user.purchaseItem(item);
            logger.info("Item purchased");
            session.update(user);
            Inventory inventory = new Inventory(idItem, name, idUser);
            session.save(inventory);
            logger.info("Purchase saved, item: " + inventory.getName());

        } catch (InsufficientMoneyException e) {
            logger.warn("Not enough money exception");
            throw new InsufficientMoneyException();
        } catch(SQLException e){
            logger.warn("Object already purchased");
            throw new SQLException();
        }
        finally {

            session.close();
        }
    }
    public void updateUser(UpdateInfo info) throws SQLException {
        Session session = null;
        User user;
        try{
            session = FactorySession.openSession();
            user = (User) session.get(User.class, "idUser", (info.getIdUser()));
            session.update(updateInfo(user,info));
        }catch (SQLException e){
            throw new SQLException();
        } finally {
            session.close();
        }
    }
    private User updateInfo(User user, UpdateInfo info) {
        user.setName(info.getName());
        user.setSurname(info.getSurname());
        user.setEmail(info.getEmail());
        user.setPassword(info.getPassword());
        return user;
    }
    public void addReport(Report report) throws SQLException {
        Session session = null;
        try {
            session = FactorySession.openSession();
            String date = report.getDate();
            String informer = report.getInformer();
            String message = report.getMessage();
            logger.info("Sending report...");
            logger.info("Report = Date: "+date+", Informer: "+informer+", Message: "+message+".");
            session.save(report);
            reports.add(report);
            //return report;
        } catch(SQLException e){
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
    public List<Report> getReports() throws SQLException{
        Session session = null;
        try {
            session = FactorySession.openSession();
            List<Report> reportList = new ArrayList<Report>();
            reportList = session.findAll(Report.class);
            return reportList;
        } catch (Exception e){
            throw new SQLException();
        }finally {
            session.close();
        }
    }
    public List<Insignias>  getInsignias (String idUser) throws NonExistentItemException, SQLException, NotInInventoryException {
        Session session = null;
        HashMap<String, String> user = new HashMap<>();
        user.put("idUser", idUser);
        List<Insignias> badges = new ArrayList<>();
        try {
            session = FactorySession.openSession();
            List<Object> inPossession = session.findAll(Insignias.class, user);
            if (inPossession.size()!=0) {
                logger.info("The user with id: "+idUser+" has badges");
                for (Object object : inPossession) {
                    Insignias insignias = (Insignias) object;
                    try {
                        badges.add(insignias);
                        logger.info("Badges: "+insignias.getName());
                    } catch (Exception e) {
                        logger.info("The user with id: " +idUser+" has no badges");
                        throw new NonExistentItemException();
                    }
                }
                return badges;
            }
            throw new NotInInventoryException();
        }catch (Exception e){
        } finally {
            session.close();
        }
        return badges;
    }

}