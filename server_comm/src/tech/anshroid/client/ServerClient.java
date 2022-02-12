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
        Kryo kryo = client.getKryo();
        kryo.register(Emergency.class);

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
        init();
        client.start();

        client.connect(10000, "server.anshroid.tech", 25565);

        while (true) {
            try {
                Statement stmt = conn.createStatement();
                ResultSet res = stmt.executeQuery("SELECT * FROM ROOMS");

                Map<Integer, String> emergencies = new HashMap<>();
                while (!res.isAfterLast()) {
                    if (res.getBoolean("problem")) {
                        Integer roomId = res.getInt("id");
                        String roomName = res.getString("name");
                        emergencies.put(roomId, roomName);
                    }
                }

                for (Map.Entry<Integer, String> room : emergencies.entrySet()) {
                    Emergency issue = new Emergency();
                    issue.roomId = room.getKey();
                    issue.roomName = room.getValue();
                    client.sendTCP(issue);
                }

                Thread.sleep(5000);
            } catch (InterruptedException e) {
                break;
            }
        }

    }

}