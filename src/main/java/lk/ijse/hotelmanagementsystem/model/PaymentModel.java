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

    public boolean savePayment(PaymentDTO dto) throws SQLException {
        String sql = "INSERT INTO payment (payment_id, reservation_id, payment_date, amount, transaction_id, payment_method, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            setPaymentStatementParameters(stmt, dto);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updatePayment(PaymentDTO dto) throws SQLException {
        String sql = "UPDATE payment SET reservation_id = ?, payment_date = ?, amount = ?, " +
                "transaction_id = ?, payment_method = ?, status = ? WHERE payment_id = ?";

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

    public boolean deletePayment(String paymentId) throws SQLException {
        String sql = "DELETE FROM payment WHERE payment_id = ?";

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, paymentId);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<PaymentDTO> getAllPayments() throws SQLException {
        String sql = "SELECT * FROM payment ORDER BY payment_date DESC";
        return executePaymentQuery(sql);
    }

    public List<PaymentDTO> searchPayments(String searchTerm) throws SQLException {
        String sql = "SELECT * FROM payment WHERE payment_id LIKE ? OR reservation_id LIKE ? " +
                "OR transaction_id LIKE ? ORDER BY payment_date DESC";

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, "%" + searchTerm + "%");
            stmt.setString(2, "%" + searchTerm + "%");
            stmt.setString(3, "%" + searchTerm + "%");

            return getPaymentsFromResultSet(stmt.executeQuery());
        }
    }

    public String getNextPaymentId() throws SQLException {
        String sql = "SELECT payment_id FROM payment " +
                "WHERE payment_id REGEXP '^PAY[0-9]{3}$' " +
                "ORDER BY CAST(SUBSTRING(payment_id, 4) AS UNSIGNED) DESC LIMIT 1";

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString(1);
                int lastIdNumber = Integer.parseInt(lastId.substring(3));
                return String.format("PAY%03d", lastIdNumber + 1);
            }
            return "PAY001";
        }
    }

    public List<String> loadReservationIds() throws SQLException {
        String sql = "SELECT reservation_id FROM reservation WHERE status = 'Confirmed'";
        List<String> reservationIds = new ArrayList<>();

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                reservationIds.add(rs.getString("reservation_id"));
            }
        }
        return reservationIds;
    }

    public Double getReservationTotalCost(String reservationId) throws SQLException {
        String sql = "SELECT total_cost FROM reservation WHERE reservation_id = ?";

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble("total_cost") : null;
            }
        }
    }

    public String getReservationDetails(String reservationId) throws SQLException {
        String sql = "SELECT r.reservation_id, r.check_in_date, r.check_out_date, " +
                "CONCAT(g.first_name, ' ', g.last_name) AS guest_name, " +
                "GROUP_CONCAT(rt.type_name SEPARATOR ', ') AS room_types, " +
                "r.total_cost " +
                "FROM reservation r " +
                "JOIN guest g ON r.guest_id = g.guest_id " +
                "JOIN reservation_room rr ON r.reservation_id = rr.reservation_id " +
                "JOIN room rm ON rr.room_id = rm.room_id " +
                "JOIN room_type rt ON rm.type_id = rt.type_id " +
                "WHERE r.reservation_id = ? " +
                "GROUP BY r.reservation_id";

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return String.format(
                            rs.getString("reservation_id"),
                            rs.getString("guest_name"),
                            rs.getDate("check_in_date"),
                            rs.getDate("check_out_date"),
                            rs.getString("room_types"),
                            rs.getDouble("total_cost")
                    );
                }
                return "No details found for reservation: " + reservationId;
            }
        }
    }

    private List<PaymentDTO> executePaymentQuery(String sql) throws SQLException {
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            return getPaymentsFromResultSet(rs);
        }
    }

    private List<PaymentDTO> getPaymentsFromResultSet(ResultSet rs) throws SQLException {
        List<PaymentDTO> paymentList = new ArrayList<>();

        while (rs.next()) {
            paymentList.add(new PaymentDTO(
                    rs.getString("payment_id"),
                    rs.getString("reservation_id"),
                    rs.getTimestamp("payment_date"),
                    rs.getDouble("amount"),
                    rs.getString("transaction_id"),
                    rs.getString("payment_method"),
                    rs.getString("status")
            ));
        }
        return paymentList;
    }

    private void setPaymentStatementParameters(PreparedStatement stmt, PaymentDTO dto) throws SQLException {
        stmt.setString(1, dto.getPaymentId());
        stmt.setString(2, dto.getReservationId());
        stmt.setTimestamp(3, dto.getPaymentDate());
        stmt.setDouble(4, dto.getAmount());
        stmt.setString(5, dto.getTransactionId());
        stmt.setString(6, dto.getPaymentMethod());
        stmt.setString(7, dto.getStatus());
    }
}