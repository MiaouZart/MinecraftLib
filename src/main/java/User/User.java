package User;

public class User {
    private String m_userName;
    private String m_uuid;
    private String m_clientID;
    private String m_userType;
    private String m_accessToken;

    public User(String Name){
        m_userName = Name;
        m_accessToken = "0";
        m_uuid = "offline";
        m_userType = "legacy";
    }

    public String getUserType() {
        return m_userType;
    }

    public String getUUID() {
        return m_uuid;
    }

    public String getAccessToken() {
        return m_accessToken;
    }

    public String getUserName() {
        return m_userName;
    }
}
