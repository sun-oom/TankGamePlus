/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44)
 Source Host           : localhost:3306
 Source Schema         : tank_game

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44)
 File Encoding         : 65001

 Date: 17/06/2026 23:14:16
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for game_saves
-- ----------------------------
DROP TABLE IF EXISTS `game_saves`;
CREATE TABLE `game_saves`  (
  `save_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT (uuid()),
  `save_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '存档名称',
  `save_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '保存时间',
  `player_x` int NOT NULL COMMENT '玩家X坐标',
  `player_y` int NOT NULL COMMENT '玩家Y坐标',
  `player_direction` tinyint NOT NULL COMMENT '玩家朝向(0上1下2左3右)',
  `score` int NULL DEFAULT 0 COMMENT '得分',
  `level` int NULL DEFAULT 1 COMMENT '关卡',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态:0进行中,1胜利,2失败',
  `enemy_data` json NULL COMMENT '敌方坦克数据(JSON格式)',
  `play_time` int NULL DEFAULT 0 COMMENT '游戏时长(秒)',
  PRIMARY KEY (`save_id`) USING BTREE,
  INDEX `idx_save_time`(`save_time` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '游戏存档表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of game_saves
-- ----------------------------
INSERT INTO `game_saves` VALUES ('0f3fd03f-699d-11f1-9fbd-f83dc6585e57', '自动存档 2026-06-17 00:04:31', '2026-06-17 00:04:31', 295, 514, 0, 0, 1, 2, '[{\"x\": 27, \"y\": 226, \"direction\": 1}, {\"x\": 41, \"y\": 407, \"direction\": 0}, {\"x\": 568, \"y\": 477, \"direction\": 0}, {\"x\": 177, \"y\": 126, \"direction\": 1}, {\"x\": 378, \"y\": 69, \"direction\": 3}, {\"x\": 689, \"y\": 357, \"direction\": 2}, {\"x\": 196, \"y\": 417, \"direction\": 0}, {\"x\": 556, \"y\": 105, \"direction\": 1}, {\"x\": 434, \"y\": 234, \"direction\": 2}, {\"x\": 749, \"y\": 462, \"direction\": 2}, {\"x\": 632, \"y\": 315, \"direction\": 2}, {\"x\": 18, \"y\": 91, \"direction\": 2}, {\"x\": 350, \"y\": 387, \"direction\": 1}]', 0);
INSERT INTO `game_saves` VALUES ('226bca19-69a3-11f1-9fbd-f83dc6585e57', '自动存档 2026-06-17 00:48:00', '2026-06-17 00:48:00', 400, 564, 0, 0, 1, 2, '[{\"x\": 692, \"y\": 497, \"direction\": 0}, {\"x\": 219, \"y\": 75, \"direction\": 1}, {\"x\": 544, \"y\": 27, \"direction\": 1}, {\"x\": 460, \"y\": 24, \"direction\": 1}, {\"x\": 505, \"y\": 516, \"direction\": 1}, {\"x\": 3, \"y\": 542, \"direction\": 3}, {\"x\": 4, \"y\": 414, \"direction\": 0}, {\"x\": 164, \"y\": 222, \"direction\": 0}, {\"x\": 93, \"y\": 65, \"direction\": 2}, {\"x\": 39, \"y\": 35, \"direction\": 0}, {\"x\": 6, \"y\": 224, \"direction\": 1}, {\"x\": 81, \"y\": 476, \"direction\": 3}, {\"x\": 212, \"y\": 319, \"direction\": 1}, {\"x\": 671, \"y\": 537, \"direction\": 2}, {\"x\": 635, \"y\": 93, \"direction\": 1}]', 0);
INSERT INTO `game_saves` VALUES ('832166da-6a5b-11f1-a0a8-f83dc6585e57', '自动存档 2026-06-17 22:47:49', '2026-06-17 22:47:49', 560, 504, 3, 0, 1, 0, '[{\"x\": 2040, \"y\": 1514, \"direction\": 0}]', 0);
INSERT INTO `game_saves` VALUES ('b14230a4-69a4-11f1-9fbd-f83dc6585e57', '自动存档 2026-06-17 00:59:09', '2026-06-17 00:59:09', 400, 564, 0, 0, 1, 2, '[{\"x\": 542, \"y\": 20, \"direction\": 2}, {\"x\": 577, \"y\": 376, \"direction\": 1}, {\"x\": 657, \"y\": 489, \"direction\": 0}, {\"x\": 161, \"y\": 320, \"direction\": 3}, {\"x\": 128, \"y\": 3, \"direction\": 2}, {\"x\": 417, \"y\": 261, \"direction\": 2}, {\"x\": 195, \"y\": 530, \"direction\": 1}, {\"x\": 250, \"y\": 0, \"direction\": 3}, {\"x\": 75, \"y\": 267, \"direction\": 2}, {\"x\": 401, \"y\": 198, \"direction\": 1}, {\"x\": 192, \"y\": 415, \"direction\": 1}, {\"x\": 244, \"y\": 175, \"direction\": 2}, {\"x\": 20, \"y\": 328, \"direction\": 1}, {\"x\": 303, \"y\": 405, \"direction\": 0}, {\"x\": 722, \"y\": 171, \"direction\": 2}]', 0);
INSERT INTO `game_saves` VALUES ('c7986d22-6a4d-11f1-a0a8-f83dc6585e57', '自动存档 2026-06-17 21:09:31', '2026-06-17 21:09:31', 400, 564, 0, 0, 1, 2, '[{\"x\": 749, \"y\": 275, \"direction\": 1}, {\"x\": 380, \"y\": 9, \"direction\": 1}, {\"x\": 716, \"y\": 237, \"direction\": 3}, {\"x\": 734, \"y\": 180, \"direction\": 2}, {\"x\": 77, \"y\": 561, \"direction\": 0}, {\"x\": 464, \"y\": 51, \"direction\": 1}, {\"x\": 408, \"y\": 125, \"direction\": 3}, {\"x\": 614, \"y\": 240, \"direction\": 1}, {\"x\": 319, \"y\": 44, \"direction\": 3}, {\"x\": 524, \"y\": 81, \"direction\": 1}, {\"x\": 489, \"y\": 170, \"direction\": 3}, {\"x\": 653, \"y\": 207, \"direction\": 3}, {\"x\": 542, \"y\": 189, \"direction\": 0}, {\"x\": 178, \"y\": 54, \"direction\": 1}, {\"x\": 56, \"y\": 7, \"direction\": 2}]', 0);
INSERT INTO `game_saves` VALUES ('d48dc4b7-6a4d-11f1-a0a8-f83dc6585e57', '自动存档 2026-06-17 21:09:53', '2026-06-17 21:09:53', 400, 564, 0, 0, 1, 2, '[{\"x\": 689, \"y\": 488, \"direction\": 2}, {\"x\": 380, \"y\": 132, \"direction\": 0}, {\"x\": 698, \"y\": 327, \"direction\": 1}, {\"x\": 632, \"y\": 270, \"direction\": 1}, {\"x\": 77, \"y\": 438, \"direction\": 1}, {\"x\": 461, \"y\": 159, \"direction\": 1}, {\"x\": 576, \"y\": 125, \"direction\": 3}, {\"x\": 524, \"y\": 453, \"direction\": 2}, {\"x\": 187, \"y\": 42, \"direction\": 1}, {\"x\": 518, \"y\": 126, \"direction\": 1}, {\"x\": 399, \"y\": 170, \"direction\": 3}, {\"x\": 563, \"y\": 297, \"direction\": 1}, {\"x\": 452, \"y\": 327, \"direction\": 2}, {\"x\": 88, \"y\": 267, \"direction\": 2}, {\"x\": 120, \"y\": 75, \"direction\": 1}]', 0);
INSERT INTO `game_saves` VALUES ('e4cb3eb5-699a-11f1-9fbd-f83dc6585e57', '自动存档 2026-06-16 23:49:00', '2026-06-16 23:49:00', 400, 564, 0, 0, 1, 2, '[{\"x\": 552, \"y\": 139, \"direction\": 2}, {\"x\": 400, \"y\": 69, \"direction\": 1}, {\"x\": 77, \"y\": 80, \"direction\": 0}, {\"x\": 516, \"y\": 215, \"direction\": 1}, {\"x\": 594, \"y\": 78, \"direction\": 1}, {\"x\": 81, \"y\": 309, \"direction\": 2}, {\"x\": 272, \"y\": 129, \"direction\": 3}, {\"x\": 501, \"y\": 266, \"direction\": 1}, {\"x\": 162, \"y\": 373, \"direction\": 1}, {\"x\": 459, \"y\": 152, \"direction\": 0}, {\"x\": 300, \"y\": 344, \"direction\": 1}, {\"x\": 248, \"y\": 380, \"direction\": 2}, {\"x\": 338, \"y\": 146, \"direction\": 3}, {\"x\": 262, \"y\": 70, \"direction\": 3}, {\"x\": 285, \"y\": 13, \"direction\": 3}]', 0);
INSERT INTO `game_saves` VALUES ('f28e25dd-699a-11f1-9fbd-f83dc6585e57', '自动存档 2026-06-16 23:49:24', '2026-06-16 23:49:24', 365, 404, 0, 0, 1, 2, '[{\"x\": 528, \"y\": 15, \"direction\": 1}, {\"x\": 215, \"y\": 75, \"direction\": 3}, {\"x\": 181, \"y\": 520, \"direction\": 1}, {\"x\": 570, \"y\": 417, \"direction\": 1}, {\"x\": 261, \"y\": 45, \"direction\": 0}, {\"x\": 325, \"y\": 33, \"direction\": 3}, {\"x\": 514, \"y\": 162, \"direction\": 0}, {\"x\": 731, \"y\": 294, \"direction\": 3}, {\"x\": 602, \"y\": 205, \"direction\": 1}, {\"x\": 656, \"y\": 114, \"direction\": 3}, {\"x\": 15, \"y\": 332, \"direction\": 2}, {\"x\": 54, \"y\": 525, \"direction\": 0}, {\"x\": 376, \"y\": 518, \"direction\": 2}, {\"x\": 330, \"y\": 147, \"direction\": 2}]', 0);

SET FOREIGN_KEY_CHECKS = 1;
