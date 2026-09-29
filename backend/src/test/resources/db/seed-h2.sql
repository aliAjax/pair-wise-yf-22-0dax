-- H2（测试）专用种子数据，语法与 PostgreSQL 版 seed.sql 等价。
MERGE INTO app_user (id, username, password, display_name, role, enabled) KEY(id) VALUES
  (1001, 'inspector', 'inspector123', '质检员小李', 'QUALITY_INSPECTOR', TRUE),
  (1002, 'supervisor', 'supervisor123', '产线主管老王', 'LINE_SUPERVISOR', TRUE),
  (1003, 'manager', 'manager123', '质量经理赵姐', 'QUALITY_MANAGER', TRUE),
  (1004, 'auditor', 'auditor123', '审计员小周', 'AUDITOR', TRUE);

MERGE INTO work_order (id, order_no, product_code, product_name, planned_qty, line_code, start_at, status, created_at) KEY(id) VALUES
  (2001, 'WO20260920001', 'P-MOTOR-07', '7号伺服电机', 500, 'LINE-A', TIMESTAMP '2026-09-20 08:30:00', 'RUNNING', TIMESTAMP '2026-09-20 08:00:00');

MERGE INTO product_batch (id, batch_no, work_order_id, quantity, material_lot_no, produced_at, batch_status, created_at) KEY(id) VALUES
  (3001, 'B2026092401', 2001, 200, 'MAT-2026-0918', TIMESTAMP '2026-09-24 16:00:00', 'PENDING', TIMESTAMP '2026-09-24 16:05:00');

MERGE INTO batch_conclusion (id, batch_id, conclusion_status, reason, final_inspection_id, updated_at) KEY(id) VALUES
  (4001, 3001, 'PENDING', '批次刚创建，尚无检验与不良记录', NULL, TIMESTAMP '2026-09-24 16:05:00');

MERGE INTO batch_conclusion_history (id, batch_id, from_status, to_status, reason, changed_by, changed_at) KEY(id) VALUES
  (5001, 3001, NULL, 'PENDING', '批次创建，初始结论为待处理', 'system', TIMESTAMP '2026-09-24 16:05:00');
