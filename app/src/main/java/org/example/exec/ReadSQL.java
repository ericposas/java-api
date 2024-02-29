package org.example.exec;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ReadSQL {

    public static void readFile(String username, String password, String host, String sqlFilepath) {
        try {
            String line;
            String cmd = "psql " + host.split("//")[0].split("jdbc:")[1] + "//" + username + ":" + password + "@"
                    + host.split("//")[1] + " -f "
                    + sqlFilepath;
            Process process = Runtime.getRuntime().exec(cmd);
            System.out.println(cmd);
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println("Generated tables");
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
