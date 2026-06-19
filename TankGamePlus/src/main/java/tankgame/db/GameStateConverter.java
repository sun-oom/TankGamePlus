package tankgame.db;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import tankgame.Setting;
import tankgame.TankEnemy;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 游戏状态转换器 — 负责敌方坦克数据在 Java 对象和 JSON 字符串之间的转换
 */
public class GameStateConverter {

    private static final Gson gson = new Gson();

    /**
     * 可序列化的敌方坦克状态（仅保存需要持久化的字段）
     */
    public static class EnemyState {
        public int x;
        public int y;
        public int direction;

        public EnemyState() {}

        public EnemyState(int x, int y, int direction) {
            this.x = x;
            this.y = y;
            this.direction = direction;
        }
    }

    /**
     * 将敌方坦克列表序列化为 JSON 字符串
     */
    public static String enemiesToJson(List<TankEnemy> enemies) {
        List<EnemyState> states = new ArrayList<>();
        for (TankEnemy enemy : enemies) {
            if (enemy.alive) {
                states.add(new EnemyState(enemy.x, enemy.y, enemy.direction));
            }
        }
        return gson.toJson(states);
    }

    /**
     * 从 JSON 字符串反序列化为敌方坦克列表
     */
    public static List<TankEnemy> jsonToEnemies(String json) {
        List<TankEnemy> enemies = new ArrayList<>();
        if (json == null || json.isEmpty() || json.equals("[]")) {
            return enemies;
        }

        try {
            Type listType = new TypeToken<List<EnemyState>>(){}.getType();
            List<EnemyState> states = gson.fromJson(json, listType);

            for (EnemyState state : states) {
                TankEnemy enemy = new TankEnemy(
                        state.x, state.y,
                        Setting.TANK_WIDTH, Setting.TANK_HEIGHT
                );
                enemy.direction = state.direction;
                enemies.add(enemy);
            }
        } catch (Exception e) {
            System.err.println("解析敌方数据失败: " + e.getMessage());
            // 返回空列表，让游戏在没有敌方坦克的情况下也能继续
        }
        return enemies;
    }
}
