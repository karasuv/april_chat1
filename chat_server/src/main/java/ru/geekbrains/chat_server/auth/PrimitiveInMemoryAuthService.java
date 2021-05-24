package ru.geekbrains.chat_server.auth;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrimitiveInMemoryAuthService implements AuthService {
    private Connection connection;
    private Statement statement;
    private List<User> users;

    public PrimitiveInMemoryAuthService() {
//
//        try {
//            connection = DriverManager.getConnection("jdbc:sqlite:users.db");
//            statement = connection.createStatement();
//            statement.execute("CREATE TABLE users_table (log_in   TEXT UNIQUE NOT NULL PRIMARY KEY, password TEXT NOT NULL, Nickname TEXT UNIQUE NOT NULL);");
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (statement != null) statement.close();
//            } catch (SQLException throwables) {
//                throwables.printStackTrace();
//            }
//
//            try {
//                if (connection != null) connection.close();
//            } catch (SQLException throwables) {
//                throwables.printStackTrace();
//            }
//        }


//        this.users = new ArrayList<>(
//                Arrays.asList(
//                        new User("user1", "log1", "pass"),
//                        new User("user2", "log2", "pass"),
//                        new User("user3", "log3", "pass")
//                )
//        );

    }


    @Override
    public void start() {
        System.out.println("Auth service started");
    }

    @Override
    public void stop() {
        System.out.println("Auth service stopped");
    }

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:users.db");
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from users_table ");
                while (rs.next()) {


                    if (rs.getString("log_in").equals(login) && rs.getString("password").equals(password))
                        return rs.getString("Nickname");
               }


            return null;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            try {
                if (connection != null) connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

//        for (User user : users) {
//            if (user.getLogin().equals(login) && user.getPassword().equals(password)) return user.getUsername();
//        }
return null;
    }


    @Override
    public String changeUsername(String oldName, String newName) {




        return null;
    }

    @Override
    public String changePassword(String username, String oldPassword, String newPassword) {
        return null;
    }
}
