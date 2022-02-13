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

    protected static final Client client = new Client();

    NotificationManagerCompat notificationManager;
    NotificationCompat.Builder builder;
    NotificationManager notificationChannelManager;
    CharSequence name;
    String description;

    public ServerClient(String channel_name, String channel_desc, AndroidLauncher androidLauncher, NotificationManager systemService) {
        name = channel_name;
        description = channel_desc;
        notificationManager = NotificationManagerCompat.from(androidLauncher);
        builder = new NotificationCompat.Builder(androidLauncher, "Emergency Notification");
        notificationChannelManager = systemService;
    }

    public static void init() {
        Kryo kryo = client.getKryo();
        kryo.register(ServerInteractions.Connected.class);
        kryo.register(ServerInteractions.Emergency.class);
    }

    public void run() {
        init();
        client.start();

        try {
            client.connect(10000, "server.anshroid.tech", 25565);
        } catch (IOException e) {
            run();
        }

        ServerInteractions.Connected connected = new ServerInteractions.Connected();
        client.sendTCP(connected);

        client.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                if (object instanceof ServerInteractions.Emergency) {
                    ServerInteractions.Emergency emergency = (ServerInteractions.Emergency) object;

                    sendNotification(emergency.room_name, emergency.room_id);
                }
            }

            protected void sendNotification(String room_name, int room_id) {
                createNotificationChannel();

                builder.setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle("Patient in need")
                    .setContentText("Room name: " + room_name + " : Room ID: " + room_id)
                    .setPriority(NotificationCompat.PRIORITY_MAX);
                notificationManager.notify(0, builder.build());
            }

            protected void createNotificationChannel() {
                // Create the NotificationChannel, but only on API 26+ because
                // the NotificationChannel class is new and not in the support library
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel("My Notification", name, importance);
                channel.setDescription(description);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                notificationChannelManager.createNotificationChannel(channel);
            }
        });

    }

}
