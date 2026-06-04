*/  `       1密码均为 "password" 的 BCrypt 哈希（+使用 BCryptPasswordEncoder 生成）
- 下面哈希对应 "password" 原文，你可以用工具生成后替换
INSERT INTO users (username, password_hash, role) VALUES ('operator', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'operator');
INSERT INTO users (username, password_hash, role) VALUES ('admin', '$2a$10$dXJ3SW6G7P50lGm2QWA[='20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'admin');

INSERT INTO material (code, name, unit) VALUES ('CORN', '玉米', '吨');

INSERT INTO orders (order_no, task_type, material_id, target_net_weight, customer_or_supplier, status, created_time, created_by)
VALUES ('ORD001', 'RECEIVE', 1, 30.00, '供应商A', 'ACTIVE', NOW(), 1);