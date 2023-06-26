package edu.upc.dsa.services;


import edu.upc.dsa.CRUD.DAO.*;
import edu.upc.dsa.GameManager;
import edu.upc.dsa.GameManagerImpl;
import edu.upc.dsa.exceptions.*;
import edu.upc.dsa.models.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.models.auth.In;
import org.apache.log4j.Logger;

import javax.naming.InsufficientResourcesException;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Api(value = "/game", description = "Endpoint to Game Service")
@Path("/game")
public class GameService {
    final static Logger logger = Logger.getLogger(GameService.class);
    private GameManager manager;
    private IUserDAO usermanager;
    private IInventoryDAO inventorymanager;


    public GameService() throws EmailAlreadyInUseException {
        this.manager = GameManagerImpl.getInstance();
        this.usermanager = UserDAOImpl.getInstance();
        this.inventorymanager = InventoryDAOImpl.getInstance();
        if (manager.FAQsNumber() == 0) {
            this.manager.addFAQ(new FAQ("Can I update my credentials?", "Of course, in the UPDATE section of the menu."));
            this.manager.addFAQ(new FAQ("How much money do I get?", "After registering, each user receives 200 coins."));
        }
        if (manager.MessageNumber() == 0) {
            this.manager.addMessage(new Message("New items available in the store"));
            this.manager.addMessage(new Message("Team competition. The registration process will consist of ..."));
        }
    }

    @POST
    @ApiOperation(value = "User registration", notes = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "User successfully registered", response = User.class),
            @ApiResponse(code = 404, message = "This email address is already in use"),
    })
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(User user) throws EmailAlreadyInUseException {
        try {
            this.manager.register(new User(user.getIdUser(), user.getName(), user.getSurname(), user.getEmail(), user.getPassword()));
            return Response.status(201).entity(user).build();
        } catch (EmailAlreadyInUseException e) {
            e.printStackTrace();
            return Response.status(404).entity(user).build();

        }

    }

    @POST
    @ApiOperation(value = "User login", notes = "Authenticate an existing user")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "User successfully authenticated", response = idUser.class),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 401, message = "Incorrect password"),
    })
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(Credentials credentials) throws UserNotRegisteredException, IncorrectPasswordException {
        try {
            String id = this.manager.login(credentials);
            logger.info("idUser: " +id);
            idUser idUser = new idUser(id);
            return Response.status(201).entity(idUser).build();
        } catch (UserNotRegisteredException e) {
            return Response.status(404).build();
        } catch (IncorrectPasswordException e) {
            return Response.status(401).build();
        }
    }

    @GET
    @ApiOperation(value = "View the items from the shop", notes = "View items")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = Item.class, responseContainer = "List"),
    })
    @Path("/shop")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getShop() {

        List<Item> items = this.manager.Shop();
        GenericEntity<List<Item>> entity = new GenericEntity<List<Item>>(items) {
        };
        return Response.status(201).entity(entity).build();
    }

    @PUT
    @ApiOperation(value = "Buy an item from the shop", notes = "Buy items")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful"),
            @ApiResponse(code = 409, message = "Item already purchased"),
            @ApiResponse(code = 401, message = "Item does not exist"),
            @ApiResponse(code = 403, message = "Insufficient money")
    })
    @Path("/buyItems/{idItem}/{name}/{idUser}")
    public Response buyItems(@PathParam("idItem") String idItem, @PathParam("name") String name, @PathParam("idUser") String idUser) {
        try {
            this.usermanager.buyItem(idItem, name, idUser);
            return Response.status(201).build();
        } catch (InsufficientMoneyException e) {
            return Response.status(403).build();
        } catch (NonExistentItemException e) {
            return Response.status(401).build();
        } catch (SQLException e) {
            return Response.status(409).build();
        }
    }

    @GET
    @ApiOperation(value = "View items in the user's Inventory", notes = "View Inventory")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful"),
            @ApiResponse(code = 401, message = "User has an empty inventory"),
            @ApiResponse(code = 500, message = "SQL Exception")
    })
    @Path("/inventory/{idUser}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInventory(@PathParam("idUser") String idUser) {

        try {
            List<Inventory> inventory = this.inventorymanager.getInventory(idUser);
            GenericEntity<List<Inventory>> entity = new GenericEntity<List<Inventory>>(inventory) {
            };
            return Response.status(201).entity(entity).build();
        } catch (SQLException e) {
            return Response.status(500).build();
        } catch (NotInInventoryException e) {
            return Response.status(401).build();
        } catch (NonExistentItemException e) {
            throw new RuntimeException(e);
        }

    }

    @PUT
    @ApiOperation(value = "update a User", notes = "Updating a User")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful"),
            @ApiResponse(code = 401, message = "User does not exist")
    })
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(UpdateInfo info) {
        try {
            this.usermanager.updateUser(info);
            return Response.status(201).entity(info).build();
        } catch (SQLException e) {
            return Response.status(401).build();
        }
    }

    @GET
    @ApiOperation(value = "Obtain the FAQs", notes = "View FAQs")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = FAQ.class, responseContainer = "List"),
    })
    @Path("/faqs")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFAQs() {
        List<FAQ> faqs = this.manager.getFAQs();
        GenericEntity<List<FAQ>> entity = new GenericEntity<List<FAQ>>(faqs) { };
        return Response.status(201).entity(entity).build();
    }

    @POST
    @ApiOperation(value = "Report an abuse", notes = "Add a new abuse report")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful"),
            @ApiResponse(code = 401, message = "Error")

    })
    @Path("/report/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReport(Report report) {
        try {
            this.usermanager.addReport(report);
            return Response.status(201).build();
        } catch (SQLException e) {
            return Response.status(401).build();
        }
    }

    @GET
    @ApiOperation(value = "Get user by ID", notes = "Get user")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = User.class),
            @ApiResponse(code = 404, message = "Something went wrong")
    })
    @Path("/user/{idUser}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("idUser") String idUser) {
        try {
            User user = this.usermanager.getUser(idUser);
            return Response.status(201).entity(user).build();
        } catch (Exception E) {
            return Response.status(404).build();
        }
    }
    @GET
    @ApiOperation(value = "View the user's badges", notes = "View Badges")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful"),
            @ApiResponse(code = 401, message = "The user has no badges"),
            @ApiResponse(code = 500, message = "SQL Exception")
    })
    @Path("/badges/{idUser}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBadges(@PathParam("idUser") String idUser) {

        try {
            List<Insignias> badges = this.usermanager.getInsignias(idUser);
            GenericEntity<List<Insignias>> entity = new GenericEntity<List<Insignias>>(badges) {};
            return Response.status(201).entity(entity).build();
        } catch (SQLException e) {
            return Response.status(500).build();
        } catch (NotInInventoryException e) {
            return Response.status(401).build();
        } catch (NonExistentItemException e) {
            throw new RuntimeException(e);
        }

    }
    @GET
    @ApiOperation(value = "Obtain the messages", notes = "View messages")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful", response = Message.class, responseContainer = "List")
    })
    @Path("/messages")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMessages() {
        List<Message> messages = this.manager.getMessages();
        GenericEntity<List<Message>> entity = new GenericEntity<List<Message>>(messages) {};
        return Response.status(200).entity(entity).build();
    }
    @POST
    @ApiOperation(value = "add a question", notes = "Do you want to add a question?")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful")

    })
    @Path("/user/question")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response newQuestion(Question question) throws SQLException {
        this.manager.addQuestion(question);
        return Response.status(201).entity(question).build();
    }
}