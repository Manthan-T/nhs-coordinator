package com.github.quintus_cult.nhs_coordinator.server;

import java.io.IOException;
import java.util.ArrayList;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import com.esotericsoftware.kryonet.Server;

public class CentralServer {

    public static Server server = new Server();
    public static ArrayList<Connection> phones = new ArrayList<>();

    public static void init() {
        // Register server listening events
        Kryo kryo = server.getKryo();
        kryo.register(Message.Emergency.class);
        kryo.register(Message.Connected.class);
    }

    public static void start() {
        // Start the Server
        server.start();

        System.out.println("Started");

        try {
            // Connect to the port
            server.bind(25565);

            // If an emergency is received, propagate it to all connected phones.
            server.addListener(new Listener() {

                public void received (Connection connection, Object object) {
                    if (object instanceof Message.Emergency) {
                        for (Connection phone : phones) {
                            phone.sendTCP((Message.Emergency) object);
                        }

                    }

                }

            });

            // If a phone requests a connection, store it.
            server.addListener(new Listener() {

                public void received (Connection connection, Object object) {
                    if (object instanceof Message.Connected) {
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