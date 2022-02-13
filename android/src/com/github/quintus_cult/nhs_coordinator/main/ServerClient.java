package com.github.quintus_cult.nhs_coordinator.main;

import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;

public class ServerClient implements Runnable {

    protected static final Client client = new Client();       // Starts a client

    NotificationManagerCompat notificationManager;             // Creates some variables needed for sending a notification
    NotificationCompat.Builder builder;
    NotificationManager notificationChannelManager;
    CharSequence name;
    String description;


    // This constructor assigns the below values provided by the AndroidLauncher class to the above variables
    public ServerClient(String channel_name, String channel_desc, AndroidLauncher androidLauncher, NotificationManager systemService) {
        name = channel_name;
        description = channel_desc;
        notificationManager = NotificationManagerCompat.from(androidLauncher);
        builder = new NotificationCompat.Builder(androidLauncher, "Emergency Notification");
        notificationChannelManager = systemService;
    }

    // The method to initialise the client and registers possible responses (both incoming and outgoing)
    public static void init() {
        Kryo kryo = client.getKryo();
        kryo.register(ServerInteractions.Connected.class);
        kryo.register(ServerInteractions.Emergency.class);
    }

    // Where the thread starts running from
    public void run() {
        init();               // Initialise the client
        client.start();       // Start the client

        try {
            client.connect(10000, "server.anshroid.tech", 25565); // Connect to the given ip on the given port, with a 10 second (1st value in milliseconds) timeout

        } catch (IOException e) {
            run();              // If it times out, restart the run method
        }

        ServerInteractions.Connected connected = new ServerInteractions.Connected(); // Create a message that notifies the connection
        client.sendTCP(connected);                                                   // Send the message

        /*
         *
         * The following code creates a listener for incoming messages, and if it is of the type
         * Emergency, it converts it into a readable form and pushes a notification containing
         * the information it held, provided that the current floor that the phone is on is the
         * same as the floor number in the emergency message
         *
         */

        client.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                if (object instanceof ServerInteractions.Emergency) {
                    ServerInteractions.Emergency emergency = (ServerInteractions.Emergency) object;

                    if (emergency.floorID == Interface.current_floor_no) {
                        sendNotification(emergency.roomName, emergency.roomID);
                    }
                }
            }

            // Calls the method to create the notification channel, then creates
            // the notification using the below presets and pushes it in the last line
            protected void sendNotification(String room_name, int room_id) {
                createNotificationChannel();

                builder.setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("Patient in need")
                    .setContentText("Room name: " + room_name + " : Room ID: " + room_id)
                    .setPriority(NotificationCompat.PRIORITY_MAX);

                notificationManager.notify(0, builder.build());
            }

            protected void createNotificationChannel() {
                // Sets the notification channel settings (necessary in the later versions of android)
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel("My Notification", name, importance);
                channel.setDescription(description);

                // Creates the notification channel
                notificationChannelManager.createNotificationChannel(channel);
            }
        });

    }

}
