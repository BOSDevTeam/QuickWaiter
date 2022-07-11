package data;

/**
 * Created by NweYiAung on 14-02-2017.
 */
public class IPSettingData {

    String ipAddress,databaseName,serverUser,serverPassword;

    public String getIpAddress() {
        return ipAddress;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getServerUser() {
        return serverUser;
    }

    public String getServerPassword() {
        return serverPassword;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public void setServerUser(String serverUser) {
        this.serverUser = serverUser;
    }

    public void setServerPassword(String serverPassword) {
        this.serverPassword = serverPassword;
    }
}
