package tech.anshroid.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import tech.anshroid.client.Response.*;

public class ServerClient {

    public static Client client = new Client();
    private static Connection conn;

    public static void init() throws FileNotFoundException {
        // Register connection to main server
        Kryo kryo = client.getKryo();
        kryo.register(Emergency.class);

        // Connect to the SQL Server
        Scanner scanner = new Scanner(new File("mysqlpasswd.env"));
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/imgrec_one?" +
                                                "user=root&password=" + scanner.next());
        } catch (SQLException ex) {
            System.out.println("Cannot connect to MySQL server. Aborting...");
            System.exit(1);
        }
        
    }

    public static void main(String[] args) throws IOException, SQLException {
        // Initialise connections
        init();

        // Connect to server
        client.start();
        client.connect(10000, "server.anshroid.tech", 25565);

        while (true) {
            try {
                // Query the SQL database for all rooms
                Statement stmt = conn.createStatement();
                ResultSet res = stmt.executeQuery("SELECT * FROM rooms WHERE problem = 1");

                // Iterate over table of results and send messages to server
                while (!res.isAfterLast()) {
                    Emergency issue = new Emergency();
                    issue.roomId = res.getInt("id");
                    issue.roomName = res.getString("name");
                    client.sendTCP(issue);
                }

                // Wait 5 seconds before next run-through
                Thread.sleep(5000);

            } catch (InterruptedException e) {
                // Cleanup
                client.close();
                client.stop();
                conn.close();
                break; // Stop application if interrupted
            }

        }

    }

}