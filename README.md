# 制造业质量追溯 API 服务

面向小型制造工厂的批次质量追溯后端服务，把**检验单**和**不良记录**统一按批号归集，覆盖工单、批次、检验项、不良处置、批次结论判定和追溯查询，避免“终检过了但严重不良批次被放掉”的问题。

## 批次结论规则（核心）

每个批次维护一个当前结论（`batch_conclusion`）和一条结论变化流水（`batch_conclusion_history`），在**批次创建 / 检验登记 / 不良登记 / 不良处置**后自动重算：

1. **只要还有未关闭的严重不良（MAJOR 主要 / CRITICAL 致命），结论一律停在 `PENDING` 待处理**——即使终检合格也不能放行。
2. 严重不良全部处置（CLOSED）后，结合最近一次**终检**结果：
   - 终检 `PASS` 或 `CONDITIONAL_PASS`（让步接收）→ `RELEASABLE` 可放行；
   - 终检 `FAIL` / `RECHECK`，或尚**无终检**记录 → `RECHECK` 待复检；
3. 新建批次（无检验、无不良）初始为 `PENDING` 待处理。
4. 结论状态每次变化都追加一条历史并写审计日志；状态不变只更新原因，不写流水。
5. **幂等**：同一批号 + 完全相同的检验单/不良内容重复提交，直接返回原记录（响应中 `duplicated=true`），不良数量、检验项不会重复累计；已关闭的不良再次提交处置也按原记录返回。

## 快速启动

```bash
cp .env.example .env && docker compose up -d
```

启动后：

- 健康检查：<http://localhost:21114/health>
- 登录拿 token：`POST http://localhost:21114/api/auth/login`

```bash
curl -s -X POST http://localhost:21114/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"inspector","password":"inspector123"}'
```

种子账号（密码为明文演示值，仅限本地）：

| 用户名 | 密码 | 角色 |
|---|---|---|
| inspector | inspector123 | 质检员 QUALITY_INSPECTOR |
| supervisor | supervisor123 | 产线主管 LINE_SUPERVISOR |
| manager | manager123 | 质量经理 QUALITY_MANAGER |
| auditor | auditor123 | 审计员 AUDITOR |

## API 示例（均需 `Authorization: Bearer <token>`）

```bash
# 1. 建工单 / 批次
curl -X POST http://localhost:21114/api/work-orders -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"orderNo":"WO-001","productCode":"P-MOTOR-07","productName":"伺服电机","plannedQty":500,"lineCode":"LINE-A"}'

curl -X POST http://localhost:21114/api/batches -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"batchNo":"B-001","workOrderId":123,"quantity":200,"materialLotNo":"MAT-0918"}'

# 2. 登记检验（含检验项；类型 FIRST/PATROL/FINAL）
curl -X POST http://localhost:21114/api/inspections -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"batchNo":"B-001","inspectionType":"FINAL","standardVersion":"v1","items":[
       {"itemCode":"DIM-A","itemName":"长度","measuredValue":10.01,"limitMin":9.9,"limitMax":10.1}]}'

# 3. 登记严重不良（登记后批次结论被压回待处理）
curl -X POST http://localhost:21114/api/defects -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"batchNo":"B-001","defectType":"壳体裂纹","defectQty":3,"severity":"CRITICAL","rootCause":"来料缺陷"}'

# 4. 处置推进：IN_PROGRESS 处置中 / CLOSED 已关闭（关闭后结合终检重算结论）
curl -X POST http://localhost:21114/api/defects/1/disposition -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"dispositionStatus":"CLOSED","dispositionNote":"返工复检合格"}'

# 5. 按批号追溯：工单 + 检验项 + 不良处置进度 + 当前结论 + 结论变化历史
curl http://localhost:21114/api/batches/trace/B-001 -H "Authorization: Bearer $TOKEN"
```

## 接口清单

| 方法 | 路径 | 说明 | 允许角色 |
|---|---|---|---|
| POST | `/api/auth/login` | 登录获取 JWT | 公开 |
| POST | `/api/work-orders` | 创建工单（校验产品编码，写操作日志） | 主管、经理 |
| POST | `/api/work-orders/{id}/actions` | 开工 start / 暂停 pause / 完工 finish | 主管、经理 |
| GET | `/api/work-orders` `/api/work-orders/{id}` | 工单查询 | 全部角色 |
| POST | `/api/batches` | 创建批次并更新工单产出进度 | 主管、经理 |
| GET | `/api/batches` | 批次列表（含当前结论） | 全部角色 |
| POST | `/api/inspections` | 登记检验单 + 逐项写入检验结果（幂等） | 质检员、经理 |
| GET | `/api/inspections/batch/{batchNo}` | 按批号查检验单及检验项 | 全部角色 |
| GET | `/api/inspection-item-results/inspection/{id}` | 查某张检验单的检验项 | 全部角色 |
| POST | `/api/defects` | 登记不良并反向更新批次结论（幂等） | 质检员、经理 |
| POST | `/api/defects/{id}/disposition` | 不良处置推进 / 关闭 | 质检员、主管、经理 |
| GET | `/api/defects/batch/{batchNo}` | 按批号查不良及处置进度 | 全部角色 |
| GET | `/api/batches/trace/{batchNo}` | **批次全链路追溯树** | 全部角色 |
| GET | `/api/batches/trace/{batchNo}/conclusion` | 批次当前结论 | 全部角色 |
| GET | `/api/batches/trace/{batchNo}/conclusion/history` | 结论变化流水 | 全部角色 |
| GET | `/api/audit-logs` `/api/audit-logs/{type}/{id}` | 操作/追溯审计日志 | 经理、审计员 |

## 本地开发方式

- 后端：进入 `backend` 后用 Maven 运行开发命令，接口统一挂在 `/api`。

```bash
cd backend
mvn spring-boot:run        # 需本地或 compose 中的 PostgreSQL（见根目录 .env）
mvn test                   # 使用 H2 内存库跑端到端集成测试（无需 Docker/PostgreSQL）
```

## 访问地址或 CLI 示例

后端健康检查：<http://localhost:21114/health>

Actuator：<http://localhost:21114/actuator/health>

## 技术栈

| 层 | 技术 |
|---|---|
| 前端 | - |
| 后端 | Spring Boot 3 + Java 17 + MyBatis-Plus |
| 数据库 | PostgreSQL 15（测试用 H2 PostgreSQL 兼容模式） |
| 认证 | JWT（HS256）+ RBAC |
| 部署 | Docker Compose |

## 项目目录结构

```text
backend/src/main/java/com/generated/qualityTrace/
├── routes/               # 按实体分文件的路径常量
├── controllers/          # 按实体分文件，ControllerSupport 统一二次包装异常
├── services/             # 按实体分文件；BatchConclusionService 是结论规则引擎
├── models/               # MyBatis-Plus 实体（8 张表）
├── repositories/         # BaseMapper 数据访问层
├── middlewares/          # Auth/Rbac/AuditLog/RateLimit/ErrorHandler + 当前用户上下文
├── constants/            # 枚举、错误码、日志模板、状态文案、角色常量
├── constructors/         # 请求/响应 DTO 构造器（factory）
├── validators/           # 入参校验
├── utils/                # IdGenerator、IdempotencyHasher、JwtUtil、Formatters
├── types/                # 请求/响应 DTO（record）
├── exceptions/           # ServiceException/ControllerException/全局错误结构
└── config/               # WebMvcConfig（拦截器链）等
```

## 环境变量说明

- `COMPOSE_PROJECT_NAME`: Compose 项目名，默认 `quality-trace`
- `BACKEND_PORT`: 后端宿主机端口，默认 `21114`
- `DB_PORT`: 数据库宿主机端口，默认 `54320`
- `DB_USER/DB_PASSWORD/DB_NAME`: 数据库凭据
- `JWT_SECRET`: JWT 签名密钥（生产必须修改）

## Docker 部署说明

- 根 Compose 文件不写 `version`，顶层 `name: quality-trace`。
- 容器名均使用 `${COMPOSE_PROJECT_NAME:-quality-trace}` 前缀。
- 数据库使用命名卷 `db_data`，避免绑定中文路径。
- 数据库配置 healthcheck，后端通过 `depends_on: condition: service_healthy` 等待数据库。
- 数据库初始化脚本 `database/init.sql`；后端 `src/main/resources/db/{schema,seed}.sql` 是其拆分副本，改动需同步。
- 常见问题：端口占用时修改 `.env` 中端口后重启；需要重置数据时执行 `docker compose down -v`。

## 枚举/常量出现位置清单

- **WorkOrderStatus**（PLANNED/RUNNING/PAUSED/FINISHED/CANCELLED）：
  `constants/WorkOrderStatus`、`models/WorkOrder`、`constructors/WorkOrderDtoFactory`、`services/WorkOrderService`、`controllers/WorkOrderController`、`validators/WorkOrderValidator`、`utils/Formatters`、`constants/LogTemplates`、`types/CreateWorkOrderRequest`。
- **InspectionResultStatus**（PASS/FAIL/CONDITIONAL_PASS/RECHECK）：
  `constants/InspectionResultStatus`、`models/QualityInspection`、`constructors/QualityInspectionDtoFactory`、`services/QualityInspectionService`、`services/BatchConclusionService`（终检判定）、`validators/InspectionValidator`、`utils/Formatters`、`constants/LogTemplates`、`types/RegisterInspectionRequest`。
- **DefectSeverity**（MINOR/MAJOR/CRITICAL）：
  `constants/DefectSeverity`、`models/DefectRecord`、`constructors/DefectRecordDtoFactory`（风险等级）、`services/DefectRecordService`、`services/BatchConclusionService`（严重度门槛）、`validators/DefectValidator`、`utils/Formatters`、`constants/LogTemplates`、`types/RegisterDefectRequest`。
- **InspectionType**（FIRST/PATROL/FINAL）：`constants/InspectionType`、检验 model/service/validator/factory/Formatters。
- **DispositionStatus**（OPEN/IN_PROGRESS/CLOSED）：`constants/DispositionStatus`、不良 model/service/validator/factory/Formatters。
- **BatchConclusionStatus**（PENDING/RELEASABLE/RECHECK）：`constants/BatchConclusionStatus`、结论 model/service/factory、批次 model 冗余状态、追溯 DTO、Formatters、LogTemplates。

## 为什么会牵一发动全身

实体字段、枚举、日志模板、错误消息、DTO 构造器、校验器和格式化器被刻意拆散到多个目录；修改一个状态值通常需要同步类型、构造器、服务、控制器、日志模板、错误消息、README 与数据库脚本。批次结论同时被检验登记、不良登记与不良处置三条路径触发，任何一个实体的字段或枚举变化都会沿 `service → BatchConclusionService → factory → trace 响应` 扩散。

## License

MIT
