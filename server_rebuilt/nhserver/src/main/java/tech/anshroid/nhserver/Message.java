package tech.anshroid.nhserver;

public class Message {

    public static class Emergency {
        String roomName;
        int roomId;
        int floorId;
    }

    public static class Connected { }

}
