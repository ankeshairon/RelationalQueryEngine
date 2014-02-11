package edu.buffalo.cse562.invoker;

import java.io.*;

public class RelationalQueryEngine {


    private String dataFolderName;
    private String sqlQueryFileName;

    public RelationalQueryEngine(String dataFolderName, String sqlQueryFileName) {
        this.dataFolderName = dataFolderName;
        this.sqlQueryFileName = sqlQueryFileName;
    }

    public void invoke() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(sqlQueryFileName)));
            String query;

            while ((query = bufferedReader.readLine()) != null){
//                processQuery(query);
            }

        } catch (FileNotFoundException e) {
            System.out.println("Unable to find file : " + sqlQueryFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
