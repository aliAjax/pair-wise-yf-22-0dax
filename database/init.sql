-- quality-trace PostgreSQL 15 初始化脚本（docker compose 首启时执行）
-- 注意：backend/src/main/resources/db/schema.sql 与 seed.sql 是本文件的拆分副本，修改时需同步。

CREATE TABLE IF NOT EXISTS app_user (
  id BIGINT PRIMARY KEY,
  username TEXT NOT NULL UNIQUE,
  password TEXT NOT NULL,
  display_name TEXT,
  role TEXT NOT NULL,
  enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS work_order (
  id BIGINT PRIMARY KEY,
  order_no TEXT NOT NULL UNIQUE,
  product_code TEXT NOT NULL,
  product_name TEXT,
  planned_qty INTEGER,
  line_code TEXT,
  start_at TIMESTAMP,
  status TEXT NOT NULL,
  created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS product_batch (
  id BIGINT PRIMARY KEY,
  batch_no TEXT NOT NULL UNIQUE,
  work_order_id BIGINT REFERENCES work_order(id),
  quantity INTEGER,
  material_lot_no TEXT,
  produced_at TIMESTAMP,
  batch_status TEXT NOT NULL,
  created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS quality_inspection (
  id BIGINT PRIMARY KEY,
  batch_id BIGINT NOT NULL,
  inspector_id BIGINT,
  inspector_name TEXT,
  inspection_type TEXT NOT NULL,
  standard_version TEXT,
  result_status TEXT NOT NULL,
  inspected_at TIMESTAMP,
  content_hash TEXT UNIQUE,
  created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS inspection_item_result (
  id BIGINT PRIMARY KEY,
  inspection_id BIGINT NOT NULL,
  item_code TEXT NOT NULL,
  item_name TEXT,
  measured_value NUMERIC(18,4),
  limit_min NUMERIC(18,4),
  limit_max NUMERIC(18,4),
  item_status TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS defect_record (
  id BIGINT PRIMARY KEY,
  batch_id BIGINT NOT NULL,
  defect_type TEXT NOT NULL,
  defect_qty INTEGER,
  severity TEXT NOT NULL,
  root_cause TEXT,
  disposition_status TEXT NOT NULL,
  disposition_note TEXT,
  registered_by TEXT,
  disposed_by TEXT,
  content_hash TEXT UNIQUE,
  created_at TIMESTAMP,
  disposed_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS batch_conclusion (
  id BIGINT PRIMARY KEY,
  batch_id BIGINT NOT NULL UNIQUE,
  conclusion_status TEXT NOT NULL,
  reason TEXT,
  final_inspection_id BIGINT,
  updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS batch_conclusion_history (
  id BIGINT PRIMARY KEY,
  batch_id BIGINT NOT NULL,
  from_status TEXT,
  to_status TEXT NOT NULL,
  reason TEXT,
  changed_by TEXT,
  changed_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS audit_log (
  id BIGINT PRIMARY KEY,
  actor TEXT,
  action TEXT,
  target_type TEXT,
  target_id TEXT,
  detail TEXT,
  created_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_batch_work_order ON product_batch(work_order_id);
CREATE INDEX IF NOT EXISTS idx_inspection_batch ON quality_inspection(batch_id);
CREATE INDEX IF NOT EXISTS idx_item_inspection ON inspection_item_result(inspection_id);
CREATE INDEX IF NOT EXISTS idx_defect_batch ON defect_record(batch_id);
CREATE INDEX IF NOT EXISTS idx_conclusion_history_batch ON batch_conclusion_history(batch_id);
CREATE INDEX IF NOT EXISTS idx_audit_target ON audit_log(target_type, target_id);

-- 种子数据（幂等）
INSERT INTO app_user (id, username, password, display_name, role, enabled) VALUES
  (1001, 'inspector', 'inspector123', '质检员小李', 'QUALITY_INSPECTOR', TRUE),
  (1002, 'supervisor', 'supervisor123', '产线主管老王', 'LINE_SUPERVISOR', TRUE),
  (1003, 'manager', 'manager123', '质量经理赵姐', 'QUALITY_MANAGER', TRUE),
  (1004, 'auditor', 'auditor123', '审计员小周', 'AUDITOR', TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO work_order (id, order_no, product_code, product_name, planned_qty, line_code, start_at, status, created_at) VALUES
  (2001, 'WO20260920001', 'P-MOTOR-07', '7号伺服电机', 500, 'LINE-A', TIMESTAMP '2026-09-20 08:30:00', 'RUNNING', TIMESTAMP '2026-09-20 08:00:00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_batch (id, batch_no, work_order_id, quantity, material_lot_no, produced_at, batch_status, created_at) VALUES
  (3001, 'B2026092401', 2001, 200, 'MAT-2026-0918', TIMESTAMP '2026-09-24 16:00:00', 'PENDING', TIMESTAMP '2026-09-24 16:05:00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO batch_conclusion (id, batch_id, conclusion_status, reason, final_inspection_id, updated_at) VALUES
  (4001, 3001, 'PENDING', '批次刚创建，尚无检验与不良记录', NULL, TIMESTAMP '2026-09-24 16:05:00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO batch_conclusion_history (id, batch_id, from_status, to_status, reason, changed_by, changed_at) VALUES
  (5001, 3001, NULL, 'PENDING', '批次创建，初始结论为待处理', 'system', TIMESTAMP '2026-09-24 16:05:00')
ON CONFLICT (id) DO NOTHING;
