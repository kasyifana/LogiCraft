package Helpers;

public class UserSession {
    private static String currentUserID;

    public static String getCurrentUserID() {
        return currentUserID;
    }
    
    public static void setCurrentUserID(String id) {
        currentUserID = id;
    }
}
