package com.panagis.chatroom.db;

import org.sqlite.SQLiteException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class DBService {

        static Connection db_con = null;
        static PreparedStatement db_prep = null;

        //Initialize the connection
        public static void makeDBConnection() throws ClassNotFoundException, SQLException {

            try{
                //check if Driver is OK
                Class.forName("org.sqlite.JDBC");
                System.out.println("\nCongrats Seems like Sqlite JDBC Driver is Installed !");
            }catch (ClassNotFoundException e){
                System.out.println("\nSorry We Couldn't Find your  Sqlite JDBC Driver !");
                e.printStackTrace();
                throw e;
            }


            try {
                //using DriverManager to Connect to a Specific Data Base
                db_con=DriverManager.getConnection("jdbc:sqlite:./users.db");
                if(db_con!=null){
                    System.out.println("Driver Manager Connection Successful\n");
                }else {
                    System.out.println("Connection to Data Base Failed (403)\n");
                }
            }catch (SQLException ex){
                System.out.println("Connection to Data Base Failed (404)\n");
                ex.printStackTrace();
                throw  ex;
            }
        }

        //Retrieve data from DB
        public static boolean getDataFromDB(String name, String password){

            try {
                //Make SQL statement
                String getSQL="SELECT * FROM users";
                String getSize="SELECT COUNT(*) FROM users";

                //Pass statement to the connected DBMS query
                db_prep=db_con.prepareStatement(getSize);

                //Execute query and get a table size
                ResultSet size=db_prep.executeQuery();

                if(size.next() && size.getInt("COUNT(*)")==0){
                    System.out.println("DB is empty");
                    return false;
                }


                //Pass statement to the connected DBMS query
                db_prep=db_con.prepareStatement(getSQL);

                //Execute query and get a java ResultSet
                ResultSet rs=db_prep.executeQuery();


                //Iterate the Set to get our data
                while (rs.next()) {
                    if(rs.getString("username").equals(name) && rs.getString("password").equals(password)){
                        return true;
                    }
                }
                return false;
            }catch (SQLException e){
                System.out.println(e.getMessage());
                return false;
            }

        }

        //Create new table
        public static boolean createCityTable(){
        /*if(!makeDBConnection()){
            return false;
        }*/
            try {
                String createSQL="CREATE TABLE CITY(Name VARCHAR2(50) NOT NULL, Museums NUMBER(5) NOT NULL, Sights NUMBER(5) NOT NULL, Bars NUMBER(5) NOT NULL, Forests NUMBER(5) NOT NULL, Food NUMBER(5) NOT NULL, Zoo NUMBER(5) NOT NULL, Seaside NUMBER(5) NOT NULL, Mountains NUMBER(5) NOT NULL, Lat NUMBER(6,3) NOT NULL, Lon NUMBER(6,3) NOT NULL);" ;
                db_prep=db_con.prepareStatement(createSQL);
                db_prep.executeUpdate();
            }catch (SQLException e){
                System.out.println("Table Creation Failed");
                e.printStackTrace();
                return false;
            }
            System.out.println("Table Created");
            return true;
        }

        //Insert Data to Table city into the Data Base
        public static boolean insertDataToDB(String username, String password){
            try {
                String insertSQL="INSERT INTO users VALUES("+ "'" +username+ "'" + "," + "'" + password+ "'"+ ");" ;
                db_prep=db_con.prepareStatement(insertSQL);
                db_prep.executeUpdate();
                System.out.println("User Insertion to DB Successful");
                return true;
            }catch (SQLException ex){
                System.out.println("User Insertion to DB Failed");
                System.out.println(ex.getMessage());
                return false;
            }
        }


}
