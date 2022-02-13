package tech.anshroid.nhserver;

import java.io.IOException;
import java.util.ArrayList;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import com.esotericsoftware.kryonet.Server;

import tech.anshroid.nhserver.Message.*;

public class CentralServer {

    public static Server server = new Server();
    public static ArrayList<Connection> phones = new ArrayList<>();

    public static void init() {
        // Register server listening events
        Kryo kryo = server.getKryo();
        kryo.register(Emergency.class);
        kryo.register(Connected.class);
    }

    public static void main(String[] args) {
        // Start the Server
        server.start();

        System.out.println("Started");

        try {
            // Connect to the port
            server.bind(25565);

            // If an emergency is received, propagate it to all connected phones.
            server.addListener(new Listener() {

                public void received (Connection connection, Object object) {
                    if (object instanceof Emergency) {
                        for (Connection phone : phones) {
                            phone.sendTCP(object);
                        }

                    }

                }

            });

            // If a phone requests a connection, store it.
            server.addListener(new Listener() {

                public void received (Connection connection, Object object) {
                    if (object instanceof Connected) {
                        phones.add(connection);
                        System.out.println("Phone connected!");
                    }

                }

            });

        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

}