package com.example.demoexamnewfour;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkshopDAO {
    private DBConnection dbConnection = new DBConnection();

    // Создание цеха
    public void create(Workshop workshop) throws SQLException {
        String sql = "INSERT INTO workshops (workshop_name, workshop_type, workers_count) VALUES (?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, workshop.getWorkshopName());
            stmt.setString(2, workshop.getWorkshopType());
            stmt.setInt(3, workshop.getWorkersCount());
            stmt.executeUpdate();
            System.out.println("Новый цех успешно добавлен!");
        } catch (SQLException e) {
            throw new SQLException("При создании цеха произошла ошибка: " + e.getMessage());
        }
    }

    // Получение всех цехов
    public List<Workshop> getAllWorkshops() throws SQLException {
        List<Workshop> workshops = new ArrayList<>();
        String sql = "SELECT * FROM workshops ORDER BY workshop_name";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Workshop workshop = new Workshop(
                    rs.getInt("workshop_id"),
                    rs.getString("workshop_name"),
                    rs.getString("workshop_type"),
                    rs.getInt("workers_count")
                );
                workshops.add(workshop);
            }
        } catch (SQLException e) {
            throw new SQLException("При получении списка цехов произошла ошибка: " + e.getMessage());
        }
        
        return workshops;
    }

    // Получение цеха по ID
    public Workshop getWorkshop(int workshopId) throws SQLException {
        String sql = "SELECT * FROM workshops WHERE workshop_id = ?";
        Workshop workshop = null;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, workshopId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                workshop = new Workshop(
                    rs.getInt("workshop_id"),
                    rs.getString("workshop_name"),
                    rs.getString("workshop_type"),
                    rs.getInt("workers_count")
                );
            }
        } catch (SQLException e) {
            throw new SQLException("Ошибка при получении цеха: " + e.getMessage());
        }

        return workshop;
    }

    // Получение цехов по типу
    public List<Workshop> getWorkshopsByType(String workshopType) throws SQLException {
        List<Workshop> workshops = new ArrayList<>();
        String sql = "SELECT * FROM workshops WHERE workshop_type = ? ORDER BY workshop_name";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, workshopType);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Workshop workshop = new Workshop(
                    rs.getInt("workshop_id"),
                    rs.getString("workshop_name"),
                    rs.getString("workshop_type"),
                    rs.getInt("workers_count")
                );
                workshops.add(workshop);
            }
        } catch (SQLException e) {
            throw new SQLException("При получении цехов по типу произошла ошибка: " + e.getMessage());
        }
        
        return workshops;
    }

    // Получение цехов с количеством рабочих больше указанного
    public List<Workshop> getWorkshopsWithMinWorkers(int minWorkers) throws SQLException {
        List<Workshop> workshops = new ArrayList<>();
        String sql = "SELECT * FROM workshops WHERE workers_count >= ? ORDER BY workers_count DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, minWorkers);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Workshop workshop = new Workshop(
                    rs.getInt("workshop_id"),
                    rs.getString("workshop_name"),
                    rs.getString("workshop_type"),
                    rs.getInt("workers_count")
                );
                workshops.add(workshop);
            }
        } catch (SQLException e) {
            throw new SQLException("При получении цехов с минимальным количеством рабочих произошла ошибка: " + e.getMessage());
        }
        
        return workshops;
    }

    // Получение статистики по цехам
    public List<Workshop> getWorkshopsWithStatistics() throws SQLException {
        List<Workshop> workshops = new ArrayList<>();
        String sql = "SELECT w.*, " +
                    "COUNT(pp.process_id) as process_count, " +
                    "COALESCE(SUM(pp.production_time_hours), 0) as total_production_time " +
                    "FROM workshops w " +
                    "LEFT JOIN production_processes pp ON w.workshop_id = pp.workshop_id " +
                    "GROUP BY w.workshop_id, w.workshop_name, w.workshop_type, w.workers_count " +
                    "ORDER BY w.workshop_name";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Workshop workshop = new Workshop(
                    rs.getInt("workshop_id"),
                    rs.getString("workshop_name"),
                    rs.getString("workshop_type"),
                    rs.getInt("workers_count")
                );
                workshops.add(workshop);
            }
        } catch (SQLException e) {
            throw new SQLException("При получении статистики по цехам произошла ошибка: " + e.getMessage());
        }
        
        return workshops;
    }

    // Обновление цеха
    public void update(int workshopId, String workshopName, String workshopType, int workersCount) throws SQLException {
        String sql = "UPDATE workshops SET workshop_name = ?, workshop_type = ?, workers_count = ? WHERE workshop_id = ?";

        try (Connection conn = dbConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, workshopName);
            stmt.setString(2, workshopType);
            stmt.setInt(3, workersCount);
            stmt.setInt(4, workshopId);
            stmt.executeUpdate();
            System.out.println("Цех успешно обновлен!");
        } catch (SQLException e) {
            throw new SQLException("При обновлении цеха произошла ошибка: " + e.getMessage());
        }
    }

    // Удаление цеха
    public void delete(int workshopId) throws SQLException {
        String sql = "DELETE FROM workshops WHERE workshop_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, workshopId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Цех с ID " + workshopId + " успешно удален.");
            } else {
                System.out.println("Цех с ID " + workshopId + " не найден.");
            }
        } catch (SQLException e) {
            throw new SQLException("При удалении цеха произошла ошибка: " + e.getMessage());
        }
    }

    // Получение общего количества рабочих во всех цехах
    public int getTotalWorkersCount() throws SQLException {
        String sql = "SELECT SUM(workers_count) as total_workers FROM workshops";
        int totalWorkers = 0;
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                totalWorkers = rs.getInt("total_workers");
            }
        } catch (SQLException e) {
            throw new SQLException("При получении общего количества рабочих произошла ошибка: " + e.getMessage());
        }
        
        return totalWorkers;
    }

    // Получение количества цехов по типу
    public int getWorkshopsCountByType(String workshopType) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM workshops WHERE workshop_type = ?";
        int count = 0;
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, workshopType);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                count = rs.getInt("count");
            }
        } catch (SQLException e) {
            throw new SQLException("При получении количества цехов по типу произошла ошибка: " + e.getMessage());
        }
        
        return count;
    }
}

