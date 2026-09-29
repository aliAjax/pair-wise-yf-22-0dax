-- quality-trace schema (PostgreSQL 15)
-- 与 docker compose 挂载的 database/init.sql 内容保持一致：本文件由该文件同步而来。

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
