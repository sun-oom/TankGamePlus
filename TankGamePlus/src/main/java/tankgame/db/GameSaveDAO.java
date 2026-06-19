package tankgame.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameSaveDAO {

    // 保存存档，返回数据库自动生成的 saveId
    public String saveGame(GameSave save) throws SQLException {
        String sql = """
            INSERT INTO game_saves
            (save_name, player_x, player_y, player_direction, score, level, status, enemy_data, play_time)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, save.getSaveName());
            pstmt.setInt(2, save.getPlayerX());
            pstmt.setInt(3, save.getPlayerY());
            pstmt.setInt(4, save.getPlayerDirection());
            pstmt.setInt(5, save.getScore());
            pstmt.setInt(6, save.getLevel());
            pstmt.setInt(7, save.getStatus());
            pstmt.setString(8, save.getEnemyData());
            pstmt.setInt(9, save.getPlayTime());

            pstmt.executeUpdate();

            // 获取数据库自动生成的 save_id
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    String generatedId = generatedKeys.getString(1);
                    save.setSaveId(generatedId);
                    System.out.println("存档保存成功: " + save.getSaveName() + " (ID: " + generatedId + ")");
                    return generatedId;
                }
            }
            System.out.println("存档保存成功: " + save.getSaveName());
            return null;
        } catch (SQLException e) {
            System.err.println("保存存档失败: " + e.getMessage());
            throw e;
        }
    }

    // 获取所有存档（按时间倒序）
    public List<GameSave> getAllSaves() throws SQLException {
        List<GameSave> saves = new ArrayList<>();
        String sql = "SELECT * FROM game_saves ORDER BY save_time DESC";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                saves.add(mapResultSetToSave(rs));
            }
        } catch (SQLException e) {
            System.err.println("获取存档列表失败: " + e.getMessage());
            throw e;
        }
        return saves;
    }

    // 获取进行中的存档（未结束的）
    public List<GameSave> getActiveSaves() throws SQLException {
        List<GameSave> saves = new ArrayList<>();
        String sql = "SELECT * FROM game_saves WHERE status = 0 ORDER BY save_time DESC";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                saves.add(mapResultSetToSave(rs));
            }
        } catch (SQLException e) {
            System.err.println("获取进行中存档失败: " + e.getMessage());
            throw e;
        }
        return saves;
    }

    // 获取最新的进行中存档（用于"继续游戏"直接加载）
    public GameSave getLatestActiveSave() throws SQLException {
        String sql = "SELECT * FROM game_saves WHERE status = 0 ORDER BY save_time DESC LIMIT 1";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return mapResultSetToSave(rs);
            }
        } catch (SQLException e) {
            System.err.println("获取最新存档失败: " + e.getMessage());
            throw e;
        }
        return null;
    }

    // 根据ID加载存档
    public GameSave loadSave(String saveId) throws SQLException {
        String sql = "SELECT * FROM game_saves WHERE save_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, saveId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSave(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("加载存档失败: " + e.getMessage());
            throw e;
        }
        return null;
    }

    // 删除存档
    public void deleteSave(String saveId) throws SQLException {
        String sql = "DELETE FROM game_saves WHERE save_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, saveId);
            int affected = pstmt.executeUpdate();
            System.out.println("删除了 " + affected + " 个存档");
        } catch (SQLException e) {
            System.err.println("删除存档失败: " + e.getMessage());
            throw e;
        }
    }

    // 更新存档状态（例如：游戏结束后标记为完成）
    public void updateStatus(String saveId, int status) throws SQLException {
        String sql = "UPDATE game_saves SET status = ? WHERE save_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, status);
            pstmt.setString(2, saveId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("更新存档状态失败: " + e.getMessage());
            throw e;
        }
    }

    // 将 ResultSet 映射为 GameSave 对象
    private GameSave mapResultSetToSave(ResultSet rs) throws SQLException {
        GameSave save = new GameSave();
        save.setSaveId(rs.getString("save_id"));
        save.setSaveName(rs.getString("save_name"));
        save.setSaveTime(rs.getTimestamp("save_time"));
        save.setPlayerX(rs.getInt("player_x"));
        save.setPlayerY(rs.getInt("player_y"));
        save.setPlayerDirection(rs.getInt("player_direction"));
        save.setScore(rs.getInt("score"));
        save.setLevel(rs.getInt("level"));
        save.setStatus(rs.getInt("status"));
        save.setEnemyData(rs.getString("enemy_data"));
        save.setPlayTime(rs.getInt("play_time"));
        return save;
    }
}