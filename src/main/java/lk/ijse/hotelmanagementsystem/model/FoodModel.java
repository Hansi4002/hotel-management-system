package lk.ijse.hotelmanagementsystem.model;

import lk.ijse.hotelmanagementsystem.db.DBConnection;
import lk.ijse.hotelmanagementsystem.dto.FoodDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FoodModel {

    public static String getNextMenuId() throws SQLException{
        String sql = "SELECT menu_id FROM food ORDER BY menu_id DESC LIMIT 1";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String lastId = rs.getString(1);
                int lastIdNumberInt = Integer.parseInt(lastId.substring(1));
                int nextId = lastIdNumberInt + 1;
                return String.format("F%03d", nextId);
            } else {
                return "F001";
            }
        }
    }

    public boolean saveFood(FoodDTO dto) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO food (menu_id, avaialble, category, price, item_name, description) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setString(1, dto.getMenuId());
            stm.setBoolean(2, dto.getAvailable().equals("Available"));
            stm.setString(3, dto.getCategory());
            stm.setDouble(4, dto.getPrice());
            stm.setString(5, dto.getItemName());
            stm.setString(6, dto.getDescription());

            return stm.executeUpdate() > 0;
        }
    }

    public boolean updateFood(FoodDTO dto) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE food SET  avaialble=?, category=?, price=?, item_name=?, description=? WHERE menu_id=?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setBoolean(1, dto.getAvailable().equals("Available"));
            stm.setString(2, dto.getCategory());
            stm.setDouble(3, dto.getPrice());
            stm.setString(4, dto.getItemName());
            stm.setString(5, dto.getDescription());
            stm.setString(6, dto.getMenuId());
            return stm.executeUpdate() > 0;
        }
    }

    public List<FoodDTO> getAllFoods() throws SQLException, ClassNotFoundException {
        List<FoodDTO> foodList = new ArrayList<>();
        String sql = "SELECT * FROM food";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                FoodDTO food = new FoodDTO(
                        rs.getString("menu_id"),
                        rs.getString("avaialble"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getString("item_name"),
                        rs.getString("description")
                );
                foodList.add(food);
            }
        }
        return foodList;
    }

    public boolean deleteFood(String menuId) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM food WHERE menu_id=?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setString(1, menuId);
            return stm.executeUpdate() > 0;
        }
    }
}