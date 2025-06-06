package lk.ijse.hotelmanagementsystem.model;

import lk.ijse.hotelmanagementsystem.db.DBConnection;
import lk.ijse.hotelmanagementsystem.dto.PaymentDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PaymentModel {

    public boolean deletePayment(String paymentId) throws SQLException {
        String sql = "DELETE FROM payment WHERE payment_id = ?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, paymentId);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<PaymentDTO> getAllPayments() throws SQLException {
        List<PaymentDTO> paymentList = new ArrayList<>();
        String sql = "SELECT * FROM payment";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                PaymentDTO payment = new PaymentDTO(
                        rs.getString("payment_id"),
                        rs.getString("reservation_id"),
                        rs.getTimestamp("payment_date"),
                        rs.getDouble("amount"),
                        rs.getString("transaction_id"),
                        rs.getString("payment_method"),
                        rs.getString("status")
                );
                paymentList.add(payment);
            }
        }
        return paymentList;
    }

    public List<PaymentDTO> searchPayments(String searchTerm) throws SQLException {
        List<PaymentDTO> paymentList = new ArrayList<>();
        String sql = "SELECT * FROM payment WHERE payment_id LIKE ? OR reservation_id LIKE ?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, "%" + searchTerm + "%");
            stmt.setString(2, "%" + searchTerm + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PaymentDTO payment = new PaymentDTO(
                            rs.getString("payment_id"),
                            rs.getString("reservation_id"),
                            rs.getTimestamp("payment_date"),
                            rs.getDouble("amount"),
                            rs.getString("transaction_id"),
                            rs.getString("payment_method"),
                            rs.getString("status")
                    );
                    paymentList.add(payment);
                }
            }
        }
        return paymentList;
    }

    public String getNextPaymentId() throws SQLException {
        String sql = "SELECT payment_id FROM payment WHERE payment_id REGEXP '^PAY[0-9]{3}$' ORDER BY CAST(SUBSTRING(payment_id, 4) AS UNSIGNED) DESC LIMIT 1";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String lastId = rs.getString(1);
                int lastIdNumber = Integer.parseInt(lastId.substring(3));
                int nextId = lastIdNumber + 1;
                return String.format("PAY%03d", nextId);
            } else {
                return "PAY001";
            }
        }
    }

    public List<String> loadReservationIds() throws SQLException {
        List<String> reservationIds = new ArrayList<>();
        String sql = "SELECT reservation_id FROM reservation";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                reservationIds.add(rs.getString("reservation_id"));
            }
        }
        return reservationIds;
    }

    public boolean updatePayment(PaymentDTO dto) throws SQLException {
        String sql = "UPDATE payment SET reservation_id = ?, payment_date = ?, amount = ?, transaction_id = ?, payment_method = ?, status = ? WHERE payment_id = ?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, dto.getReservationId());
            stmt.setTimestamp(2, dto.getPaymentDate());
            stmt.setDouble(3, dto.getAmount());
            stmt.setString(4, dto.getTransactionId());
            stmt.setString(5, dto.getPaymentMethod());
            stmt.setString(6, dto.getStatus());
            stmt.setString(7, dto.getPaymentId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean savePayment(PaymentDTO dto) throws SQLException {
        String sql = "INSERT INTO payment (payment_id, reservation_id, payment_date, amount, transaction_id, payment_method, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, dto.getPaymentId());
            stmt.setString(2, dto.getReservationId());
            stmt.setTimestamp(3, dto.getPaymentDate());
            stmt.setDouble(4, dto.getAmount());
            stmt.setString(5, dto.getTransactionId());
            stmt.setString(6, dto.getPaymentMethod());
            stmt.setString(7, dto.getStatus());
            return stmt.executeUpdate() > 0;
        }
    }

    public Double getReservationTotalCost(String reservationId) throws SQLException {
        String sql = "SELECT total_cost FROM reservation WHERE reservation_id = ?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total_cost");
                }
                return null;
            }
        }
    }
}
