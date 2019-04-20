/* http://cs.lmu.edu/~ray/notes/javanetexamples/ */
package nl.ivoka.connector;

import nl.ivoka.MCWrapper;
import nl.ivoka.task.TaskScheduler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * A server program which accepts requests from clients to get statistic data from MC server. When
 * a client connects, a new thread is started to handle it. Receiving client data,
 * processing it, and sending the response back is all done on the thread, allowing
 * much greater throughput because more clients can be handled concurrently.
 */

/**
 * For this project I created a new application-level protocol called MCWP (MineCraft Wrapper Protocol)
 * is entirely plain text. The messages of MCWP are:
 *
 * ClientHandler -> Server
 *     NAME <text>                  *response*
 *     GETCONNECTIONS               *response*
 *     BROADCAST <text>             *NO response*
 *
 *     GETPLAYERS                   *response*
 *     GETALLPLAYERS                *response*
 *     GETSTATS <playerUUID>        *response*
 *
 * Server -> ClientHandler
 *     SUBMITNAME
 *     NAMEACCEPTED <text>
 *     WELCOME <text>
 *     CONNECTIONS <n>
 *     MESSAGE <text>
 *     CLOSE <n> [<close message>]
 *     ERROR <n> [<error message>]
 *
 *     PLAYERS <n> [<player1Name>:<player1UUID> <player2>:<player2UUID> ...]
 *     ALLPLAYERS <n> [<player1Name>:<player1UUID> <player2>:<player2UUID> ...]
 *     STATS <playerUUID> kills:<n> deaths:<n> logins:<n> teleports:<n>
 *
 *     EVENT <event>
 *
 * CLOSE <n> -> <n> equals 0 (other reason) or 1 (server stopped)
 * ERROR <n> -> <n> equals:
 *      0 - error
 *      1 - wrong use of command
 *      2 - no such player found
 *
 * EVENT
 *     SERVER
 *         STATUS
 *             DONE
 *             STOPPING
 *         COMMAND <command>
 *         SAVING
 *     PLAYER
 *         JOIN <playerUUID>
 *         QUIT <playerUUID>
 *         CHAT <playerUUID> <chat>
 *         TELEPORT <playerUUID> <x> <y> <z> <yaw> <pitch>
 *         COMMAND <playerUUID> <command>
 *
 */

/**
 * When a client connects the server requests a screen
 * name by sending the client the text "SUBMITNAME", and keeps requesting a name until
 * a unique one is received. After a client submits a unique name, the server acknowledges
 * with "NAMEACCEPTED". Then all messages from that client will be broadcast to all other
 * clients that have submitted a unique screen name. The broadcast messages are prefixed
 * with "MESSAGE".
 *
 * This is just a teaching example so it can be enhanced in many ways, e.g., better
 * logging. Another is to accept a lot of fun commands, like Slack.
 */

// TODO make this secure (by username and password)
public class Server implements TaskScheduler<MCWrapper> {
    // All client names with sockets, so we can check for duplicates upon registration and access the Sockets.
    private static Map<String, ClientHandler> clients = new HashMap<>();
    private static List<ClientHandler> namelessClients = new ArrayList<>();

    private boolean stop=false;
    private static String welcomeMessage="Welcome to the MCWrapper-core socket. You may only connect to this if you have permission from the server owner!"; // TODO make this message configurable in config.yml

    private MCWrapper plugin;

    /**
     * Runs the server. When a client connects, the server spawns a new thread to do
     * the servicing and immediately returns to listening. The application limits the
     * number of threads via a thread pool (otherwise millions of clients could cause
     * the server to run out of resources by allocating too many threads).
     */
    public Server(MCWrapper plugin) {
        this.plugin = plugin;

        runAsync(() -> {
            try (ServerSocket listener = new ServerSocket(59898, 1, InetAddress.getLocalHost())) { // new ServerSocket(0,1,InetAddress.getByName(ipAddress)); // random port and ipAddress
                System.out.println("The connector server is running on "+InetAddress.getLocalHost().toString()+":59898 ...");
                ExecutorService pool = Executors.newFixedThreadPool(20);
                while (!stop)
                    pool.execute(new ClientHandler(listener.accept(), plugin));
                fireEvent("SERVER STATUS STOPPING", true);

                for (ClientHandler clientHandler : clients.values())
                    clientHandler.close();
                for (ClientHandler clientHandler : namelessClients)
                    clientHandler.close();

                pool.shutdown();
            } catch (IOException e) {
                System.out.println("Error message: "+e.getMessage()+"\n"+
                        "Error stacktrace: "+e.getStackTrace());
            }
        });
    }

    public void stopServer() {
        stop=true;

        try (Socket socket = new Socket(InetAddress.getLocalHost(), 59898)) {
            System.out.println("Creating closing socket...");
        } catch (IOException e) {
            System.out.println("Error message: "+e.getMessage()+"\n"+
                    "Error stacktrace: "+e.getStackTrace());
        }
    }

    public static void addNamelessClient(ClientHandler clientHandler) {
        synchronized (namelessClients) {
            namelessClients.add(clientHandler);
        }
    }

    public static boolean addClient(String name, ClientHandler clientHandler) {
        synchronized (clients) {
            if (clients.containsKey(name))
                return false;
            else
                clients.put(name, clientHandler);
            return true;
        }
    }

    public static void removeNamelessClient(ClientHandler clientHandler) {
        synchronized (namelessClients) {
            namelessClients.remove(clientHandler);
        }
    }

    public static boolean removeClient(String name) { return removeClient(name, null); }
    public static boolean removeClient(String name, ClientHandler clientHandler) {
        synchronized (clients) {
            if (!clients.containsKey(name))
                return false;
            else if (clientHandler == null)
                clients.remove(name);
            else
                clients.remove(name, clientHandler);
            return true;
        }
    }

    // region Commands
    public static void broadcast(String message) { broadcast(message, null); }
    public static void broadcast(String message, ClientHandler clientHandler) {
        for (ClientHandler client : clients.values()) {
            if (client != clientHandler)
                client.println("MESSAGE "+message);
        }
    }
    public void fireEvent(String event) { fireEvent(event, false); }
    public void fireEvent(String event, boolean override) {
        if (!stop || override) {
            for (ClientHandler client : clients.values())
                client.println("EVENT " + event);
        }
    }
    // endregion

    // region Getters
    public static Map<String, ClientHandler> getClients() { return clients; }
    public static String getWelcomeMessage() { return welcomeMessage; }
    // endregion

    @Override
    public MCWrapper getPlugin() { return plugin; }
}
