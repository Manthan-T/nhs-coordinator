package com.github.quintus_cult.nhs_coordinator.main;

public class ServerInteractions {

    // This class contains the possible responses (outgoing and incoming)

    public static class Connected {
    }

    public static class Emergency {
        String roomName;
        int roomID;
        int floorID;
    }

}
