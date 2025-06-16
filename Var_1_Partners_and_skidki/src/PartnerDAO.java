package com.example.demoexam;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartnerDAO {
    private DBConnection dbConnection = new DBConnection();

    //  Создание партнера
    public void create(Partner newPartner) throws SQLException {

        String sql = "INSERT INTO partners (partner_type, partner_name, director," +
                " email, phone, legal_address, inn)" +
                " VALUES ( ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPartner.getPartner_type());
            stmt.setString(2, newPartner.getPartner_name());
            stmt.setString(3, newPartner.getDirector());
            stmt.setString(4, newPartner.getEmail());
            stmt.setString(5, newPartner.getPhone());
            stmt.setString(6, newPartner.getLegal_address());
            stmt.setString(7, newPartner.getInn());
            stmt.executeUpdate();
            System.out.println("Новый партнер успешно добавлен!");
        } catch (SQLException e) {
            throw new SQLException(" !!! При создании партнера произошла ошибка: " + e.getMessage() + " !!! ");
        }
    }

    //    Изменение полей партнера
    public void update(int partner_id ,String partner_type, String partner_name,
                       String director, String email,
                       String phone, String legal_address, String inn) throws SQLException {

        String sql = "UPDATE partners SET partner_type = ?," +
                " partner_name = ?, director = ?, email = ?, phone = ?," +
                " legal_address = ?, inn = ? WHERE partner_id = ?";

        try (Connection conn = dbConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, partner_type);
            stmt.setString(2, partner_name);
            stmt.setString(3, director);
            stmt.setString(4, email);
            stmt.setString(5, phone);
            stmt.setString(6, legal_address);
            stmt.setString(7, inn);
            stmt.setInt(8, partner_id);
            stmt.executeUpdate();
            System.out.println("Изменения полей партнера было успешным!");
        } catch (SQLException e) {
            throw new SQLException(" !!! При изменении полей партнера произошла ошибка: " + e.getMessage() + " !!! ");
        }
    }

    //    Получение списка всех партнеров
    public List<Partner> getAllPartners() throws SQLException {
        List<Partner> partners = new ArrayList<>();
        String sql = "SELECT * FROM partners";
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Partner partner = new Partner(
                        rs.getInt("partner_id"),
                        rs.getString("partner_type"),
                        rs.getString("partner_name"),
                        rs.getString("director"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("legal_address"),
                        rs.getString("inn"),
                        rs.getInt("rating")
                );
                partners.add(partner);
            }
        } catch (SQLException e) {
            throw new SQLException(" !!! При получении списка партнеров произошла ошибка: " + e.getMessage() + " !!! ");
        }

        return partners;
    }

    //     Поиск партнера по id
    public Partner getPartner(int partnerId) throws SQLException {
        String sql = "SELECT * FROM partners WHERE partner_id = ?";
        Partner partner = null;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, partnerId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                partner = new Partner(
                        rs.getInt("partner_id"),       // это название колонки соответствует колонке из БД
                        rs.getString("partner_type"),
                        rs.getString("partner_name"),
                        rs.getString("director"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("legal_address"),
                        rs.getString("inn"),
                        rs.getInt("rating"),
                        rs.getInt("total_sales"),
                        rs.getInt("discount")
                );
            }
        } catch (SQLException e) {
            throw new SQLException("Ошибка при получении партнера: " + e.getMessage());
        }

        return partner; // Вернуть заполненный объект Partner
    }


    //     Метод, считающий общую сумму продаж партнера, чтобы потом можно было рассчитать скидку для партнера
    public List<Partner> calculateTotalSalesForPartners() throws SQLException {
        List<Partner> partners = new ArrayList<>();
        String sql = "SELECT p.*, SUM(pp.quantity) AS total_sales FROM partners p " +
                "LEFT JOIN partner_products pp ON p.partner_name = pp.partner_name " +
                "GROUP BY p.partner_id";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Partner partner = new Partner(
                        rs.getInt("partner_id"),
                        rs.getString("partner_type"),
                        rs.getString("partner_name"),
                        rs.getString("director"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("legal_address"),
                        rs.getString("inn"),
                        rs.getInt("rating")
                );

                // Получаем общее количество продаж
                int totalSales = rs.getInt("total_sales");
                partner.setTotalSales(totalSales); // Устанавливаем общее количество продаж

                // Устанавливаем скидку в зависимости от total_sales
                if (totalSales < 10000) {
                    partner.setDiscount(0);
                } else if (totalSales < 50000) {
                    partner.setDiscount(5);
                } else if (totalSales < 300000) {
                    partner.setDiscount(10);
                } else {
                    partner.setDiscount(15);
                }

                partners.add(partner);
            }
        } catch (SQLException e) {
            throw new SQLException("Ошибка при получении общего количества продаж: " + e.getMessage());
        }

        return partners;
    }


    public void updateTotalSales() throws SQLException {
        // SQL запрос для обновления total_sales
        String sql = "UPDATE partners p " +
                "SET total_sales = COALESCE((SELECT SUM(pp.quantity) FROM partner_products pp WHERE pp.partner_name = p.partner_name), 0)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Выполняем обновление
            int updatedRows = stmt.executeUpdate();
            System.out.println("Обновлено строк: " + updatedRows);
        } catch (SQLException e) {
            throw new SQLException("Ошибка при обновлении общих продаж: " + e.getMessage());
        }
    }

    public void updateDiscounts() throws SQLException {
        // SQL запрос для обновления скидок
        String sql = "UPDATE partners SET discount = " +
                "CASE " +
                "WHEN total_sales < 10000 THEN 0 " +
                "WHEN total_sales >= 10000 AND total_sales < 50000 THEN 5 " +
                "WHEN total_sales >= 50000 AND total_sales < 300000 THEN 10 " +
                "WHEN total_sales >= 300000 THEN 15 " +
                "END";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Выполняем обновление
            int updatedRows = stmt.executeUpdate();
            System.out.println("Обновлено строк: " + updatedRows);
        } catch (SQLException e) {
            throw new SQLException("Ошибка при обновлении скидок: " + e.getMessage());
        }
    }

    //    Удаление партнера по id
    public void delete(int partnerId) throws SQLException {
        String sql = "DELETE FROM partners WHERE partner_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, partnerId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Партнер с ID " + partnerId + " успешно удален.");
            } else {
                System.out.println("Партнер с ID " + partnerId + " не найден.");
            }
        } catch (SQLException e) {
            throw new SQLException(" !!! При удалении партнера произошла ошибка: " + e.getMessage() + " !!! ");
        }
    }


}