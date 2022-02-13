package com.github.quintus_cult.nhs_coordinator.server;

public class Message {

    public static class Emergency {
        String roomName;
        int roomId;
        int floorId;
    }

    public static class Connected { }

}
