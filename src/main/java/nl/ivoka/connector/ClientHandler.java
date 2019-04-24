package nl.ivoka.connector;

import nl.ivoka.MCWrapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

class ClientHandler implements Runnable {
    private String name;
    private Socket socket;
    private Scanner in;
    private PrintWriter out;

    private MCWrapper plugin;

    /**
     * Constructs a handler thread, squirreling away the socket. All the interesting
     * work is done in the run method. Remember the constructor is called from the
     * server's main method, so this has to be as short as possible.
     */
    ClientHandler(Socket socket, MCWrapper plugin) { this.socket = socket; this.plugin = plugin; }

    /**
     * Services this thread's client by repeatedly requesting a screen name until a
     * unique one has been submitted, then acknowledges the name and registers the
     * name with socket for the client in a global map, then repeatedly gets inputs and
     * broadcasts them.
     */
    @Override
    public void run() {
        System.out.println("Connected: " + socket);
        try {
            Server.addNamelessClient(this);

            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);

            // Keep requesting a name until we get a unique one.
            out.println("SUBMITNAME");
            while (in.hasNextLine()) {
                name = in.nextLine();

                if (name == null || name.length() < 1 || !name.startsWith("NAME "))
                    return;

                name = name.substring(5);

                if (Server.addClient(name, this)) {
                    Server.removeNamelessClient(this);
                    break;
                }
                out.println("SUBMITNAME");
            }

            // Tell client that name is accepted and send welcome message
            out.println("NAMEACCEPTED " + name);
            out.println("WELCOME " + Server.getWelcomeMessage());

            // Accept messages from this client and process them.
            while (in.hasNextLine()) {
                String input = in.nextLine();
                if (input.startsWith("QUIT")) {
                    out.println("CLOSE 2 Client closed connection.");
                    return;
                }

                processInput(input);
            }
        } catch (Exception e) {
            System.out.println("Error message: "+e.getMessage()+"\n"+
                    "Error stacktrace: "+e.getStackTrace()+"\n"+
                    "Socket: "+socket);
        } finally {
            if (name != null)
                Server.removeClient(name, this);
            System.out.println("Disconnected: "+socket);

            try { socket.close(); } catch (IOException e) {}
        }
    }

    private void processInput(String input) {
        String[] splittedInput = input.split("\\s+");
        String command = splittedInput[0];
        String args="";

        if (splittedInput.length>1)
            args = input.substring(command.length()+1);

        if (command.equals("GETCONNECTIONS"))
            out.println("CONNECTIONS: "+Server.getClients().size());
        else if (command.equals("BROADCAST"))
            Server.broadcast(args, this);
        else if (command.equals("GETPLAYERS"))
            out.println(plugin.getPlayers());
        else if (command.equals("GETALLPLAYERS"))
            out.println(plugin.getAllPlayers());
        else if (command.equals("GETSTATS")) {
             out.println(plugin.getStats(args.split("\\s+")[0]));
        }
    }

    public void println(String message) { out.println(message); }

    public void close() { close(1); }
    public void close(int code) {
        out.println("CLOSE "+code+" socket server shutdown");

        try { socket.close(); }
        catch (IOException e) { System.out.println("Error message: "+e.getMessage()+"\n"+
                "Error stacktrace: "+e.getStackTrace()); }
    }
}
