package ar.edu.itba.g5.server;

import ar.edu.itba.g5.server.services.AdminServiceImpl;
import ar.edu.itba.g5.server.services.FiscalServiceImpl;
import ar.edu.itba.g5.server.services.VoteServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.FiscalService;
import service.QueryService;
import service.VoteService;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private static Registry registry;

    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        logger.info("Elections Server Starting ...");

        Server.registry = LocateRegistry.createRegistry(1099); // TODO

        AdminServiceImpl adminService = new AdminServiceImpl();
        registry.bind("admin", adminService);

        VoteServiceImpl voteService = new VoteServiceImpl(adminService);
        registry.bind("vote", (VoteService) voteService);
        registry.bind("query", (QueryService) voteService);

        FiscalService fiscalService = new FiscalServiceImpl(adminService);
        registry.bind("fiscal", fiscalService);
    }
}
