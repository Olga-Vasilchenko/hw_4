package org.example.task;

import org.example.models.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class Program {

    private final static Random random = new Random();

    /*Создайте базу данных (например, SchoolDB).
    В этой базе данных создайте таблицу Courses с полями id (ключ), title, и duration.
    Настройте Hibernate для работы с вашей базой данных.
    Создайте Java-класс Course, соответствующий таблице Courses, с необходимыми аннотациями Hibernate.
    Используя Hibernate, напишите код для вставки, чтения, обновления и удаления данных в таблице Courses.
    Убедитесь, что каждая операция выполняется в отдельной транзакции.
    */
    
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/";
        String user = "root";
        String password = "olga123";

        try {
            // Подключение к базе данных
            Connection connection = DriverManager.getConnection(url, user, password);

            // Создание базы данных
            createDatabase(connection);
            System.out.println("Database created successfully");

            // Использование базы данных
            useDatabase(connection);
            System.out.println("Use database successfully");

            // Создание таблицы
            createTable(connection);
            System.out.println("Create table successfully");

            // Вставка данных
            int count = random.nextInt(5, 11);
            for (int i = 0; i < count; i++){
                insertData(connection, Course.create());
            }
            System.out.println("Insert data successfully");

            // Чтение данных
            Collection<Course> courses = readData(connection);
            for (var school : courses)
                System.out.println(school);
            System.out.println("Read data successfully");

            // Обновление данных
            for (var school: courses) {
                school.updateTitle();
                school.updateDuration();
                updateData(connection, school);
            }
            System.out.println("Update data successfully");

//            // Удаление данных
//            for (var school: courses)
//                deleteData(connection, school.getId());
//            System.out.println("Delete data successfully");

            // Закрытие соединения
            connection.close();
            System.out.println("Database connection close successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // region Вспомогательные методы
    private static void createDatabase(Connection connection) throws SQLException{
        String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS schoolDB;";
        PreparedStatement statement = connection.prepareStatement(createDatabaseSQL);
        statement.execute();
    }

    private static void useDatabase(Connection connection) throws SQLException {
        String useDatabaseSQL = "USE schoolDB;";
        try (PreparedStatement statement = connection.prepareStatement(useDatabaseSQL)) {
            statement.execute();
        }
    }
    private static void createTable(Connection connection) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS schools (ID INT AUTO_INCREMENT PRIMARY KEY, title VARCHAR(255), duration INT);";
        try (PreparedStatement statement = connection.prepareStatement(createTableSQL)) {
            statement.execute();
        }
    }

    // Добавление данных в таблицу
    private static void insertData(Connection connection, Course course) throws SQLException {
        String insertDataSQL = "INSERT INTO schools (title, duration) VALUES(?, ?);";
        try (PreparedStatement statement = connection.prepareStatement(insertDataSQL)){
            statement.setString(1, course.getTitle());
            statement.setInt(2, course.getDuration());
            statement.executeUpdate();
        }
    }

    //Чтение данных из таблицы
    private static Collection<Course> readData(Connection connection) throws SQLException {
        ArrayList<Course> schoolsList = new ArrayList<>();
        String readDataSQL = "SELECT * FROM schools;";
        try (PreparedStatement statement = connection.prepareStatement(readDataSQL)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                int duration = resultSet.getInt("duration");
                schoolsList.add(new Course(id, title, duration));
            }
            return schoolsList;
        }
    }

    // Обновление данных в таблице
    private static void updateData(Connection connection, Course course) throws SQLException {
        String updateDataSQL = "UPDATE schools SET title=?, duration=? WHERE id=?;";
        try (PreparedStatement statement = connection.prepareStatement(updateDataSQL)) {
            statement.setString(1, course.getTitle());
            statement.setInt(2, course.getDuration());
            statement.setInt(3, course.getId());
            statement.executeUpdate();
        }
    }

    // Удаление записи из таблицы
    private static void deleteData(Connection connection, int id) throws SQLException {
        String deleteDataSQL = "DELETE FROM schools WHERE id=?;";
        try (PreparedStatement statement = connection.prepareStatement(deleteDataSQL)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }
}