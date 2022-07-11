package common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import com.bosictsolution.quickwaiter.StartActivity;

/**
 * Created by NweYiAung on 14-02-2017.
 */
public class ServerConnection {
    String classs="net.sourceforge.jtds.jdbc.Driver";

    @SuppressLint("NewApi")
    public Connection CONN(){
        String ip_user_pass= StartActivity.getIPSetting();
        String arr[]=ip_user_pass.split(",");
        String ip=arr[0];
        String user=arr[1];
        String password=arr[2];
        String dbname=arr[3];
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn=null;
        String ConnURL=null;
        try{
            Class.forName(classs);
            ConnURL="jdbc:jtds:sqlserver://" + ip + ";"
                    + "databaseName=" + dbname + ";user=" + user + ";password="
                    + password + ";";
            conn=DriverManager.getConnection(ConnURL);

        }catch(SQLException se){
            Log.e("ERRO", se.getMessage());
        }catch(ClassNotFoundException e){
            Log.e("ERRO", e.getMessage());
        }catch(Exception e){
            Log.e("ERRO", e.getMessage());
        }
        return conn;
    }
}
