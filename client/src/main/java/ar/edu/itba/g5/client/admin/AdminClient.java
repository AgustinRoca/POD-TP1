package ar.edu.itba.g5.client.admin;

import utils.CommandUtils;
import exceptions.ElectionFinishedException;
import exceptions.ElectionNotStartedException;
import models.ElectionStatus;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.AdminService;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.InvalidParameterException;
import java.util.Properties;

import static utils.CommandUtils.JAVA_OPT;
import static utils.CommandUtils.SERVER_ADDRESS_PARAMETER;

public class AdminClient {
    private static final Logger logger = LoggerFactory.getLogger(AdminClient.class);
    private static final String ACTION_PARAMETER = "action";

    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException,
            ParseException {
        Properties properties = parseCommandLine(args);

        AdminService adminService =
                (AdminService) Naming.lookup("//" + properties.getProperty(SERVER_ADDRESS_PARAMETER) + "/admin");

        String action = properties.getProperty(ACTION_PARAMETER);
        switch (action) {
            case "open": {
                try {
                    adminService.open();
                } catch (ElectionFinishedException e) {
                    System.err.println(e.getMessage() + " You can not open it again.");
                    System.exit(1);
                } catch (RemoteException e) {
                    System.err.println("An unknown remote error occurred");
                    System.exit(1);
                }

                System.out.println("Election Started");
                break;
            }
            case "close": {
                try {
                    adminService.close();
                } catch (ElectionNotStartedException e) {
                    System.err.println(e.getMessage() + "You can not close it before opening it.");
                    System.exit(1);
                } catch (RemoteException e) {
                    System.err.println("An unknown remote error occurred");
                    System.exit(1);
                }

                System.out.println("Election Closed");
                break;
            }
            case "state": {
                ElectionStatus status = adminService.getState();
                System.out.println(status.toString());
                break;
            }
            default:
                throw new InvalidParameterException("\"" + action + "\" is not a supported action");
        }
    }

    private static Properties parseCommandLine(String[] args) throws ParseException {
        Option actionOption = new Option(JAVA_OPT, "specifies the action to perform: open, state or close");
        actionOption.setArgName(ACTION_PARAMETER);
        actionOption.setRequired(true);

        return CommandUtils.parseCommandLine(args, CommandUtils.serverAddressOption, actionOption);
    }
}
