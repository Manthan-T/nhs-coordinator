package tech.anshroid.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import tech.anshroid.client.Response.*;

public class ServerClient {

    public static Client client = new Client();
    private static Connection conn;
    private static int floorNo;
    private static Map<Integer, Integer> rooms = new HashMap<>();

    public static void init() throws IOException {
        // Load floor no. from config
        try {
            Scanner scanner = new Scanner(new File(System.getProperty("user.dir") + "\\config.ini"));
            floorNo = Integer.parseInt(scanner.nextLine().split(": ")[1]);

        } catch (FileNotFoundException e) {
            // If the file does not exist, create it
            FileOutputStream fos = new FileOutputStream("config.ini");
            fos.write("floorNo: 1".getBytes());
            fos.flush();
            fos.close();

            floorNo = 1;
        }


        // Register connection to main server
        Kryo kryo = client.getKryo();
        kryo.register(Emergency.class);

        // Connect to the SQL Server
        Scanner scanner = new Scanner(new File(System.getProperty("user.dir") + "\\mysqlpasswd.env"));
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
                ResultSet res = stmt.executeQuery("SELECT * FROM rooms");

                // Iterate over table of results and send messages to server
                while (!res.isAfterLast()) {
                    if (!rooms.containsKey(res.getInt("id"))) {
                        rooms.put(res.getInt("id"), 0);
                    }

                    if (rooms.get(res.getInt("id")) != res.getInt("problem")) {
                        rooms.replace(res.getInt("id"), res.getInt("problem"));
                        Emergency issue = new Emergency();
                        issue.floorId = floorNo;
                        issue.roomId = res.getInt("id");
                        issue.roomName = res.getString("name");
                        client.sendTCP(issue);
                    }
                    res.next();
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