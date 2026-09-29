package com.generated.qualityTrace;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 批次质量追溯主流程端到端测试：
 * 登录 -> 建工单/批次 -> 登记严重不良（结论=待处理）-> 终检通过（仍待处理）
 * -> 重复登记不累计 -> 关闭不良（结论=可放行）-> 结论变化历史 -> 追溯汇总。
 */
@SpringBootTest
@AutoConfigureMockMvc
class BatchTraceFlowIntegrationTest {

  @Autowired
  private MockMvc mockMvc;
  private final ObjectMapper mapper = new ObjectMapper();

  private String token(String username, String password) throws Exception {
    MvcResult result = mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
        .andExpect(status().isOk())
        .andReturn();
    return mapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
  }

  private String performJson(String token, String method, String uri, String body, int expected)
      throws Exception {
    var builder = method.equals("GET") ? get(uri) : post(uri);
    if (token != null) {
      builder.header("Authorization", "Bearer " + token);
    }
    if (body != null) {
      builder.contentType(MediaType.APPLICATION_JSON).content(body);
    }
    MvcResult result = mockMvc.perform(builder).andExpect(status().is(expected)).andReturn();
    return result.getResponse().getContentAsString();
  }

  @Test
  void fullFlow_severeDefectHoldsReleaseUntilClosedAndIsIdempotent() throws Exception {
    String inspector = token("inspector", "inspector123");
    String manager = token("manager", "manager123");

    // 1. 建工单 + 批次
    String orderBody = "{\"orderNo\":\"WO-IT-001\",\"productCode\":\"P-1\",\"productName\":\"电机\","
        + "\"plannedQty\":100,\"lineCode\":\"L1\"}";
    JsonNode order = mapper.readTree(performJson(manager, "POST", "/api/work-orders", orderBody, 200));
    long workOrderId = order.get("id").asLong();

    String batchBody = "{\"batchNo\":\"B-IT-001\",\"workOrderId\":" + workOrderId
        + ",\"quantity\":50,\"materialLotNo\":\"MAT-1\"}";
    JsonNode batch = mapper.readTree(performJson(manager, "POST", "/api/batches", batchBody, 200));
    assertEquals("PENDING", batch.get("conclusionStatus").asText());
    long batchId = batch.get("id").asLong();

    // 2. 登记 CRITICAL 严重不良 -> 结论必须停在待处理
    String defectBody = "{\"batchNo\":\"B-IT-001\",\"defectType\":\"壳体裂纹\",\"defectQty\":3,"
        + "\"severity\":\"CRITICAL\",\"rootCause\":\"来料缺陷\"}";
    JsonNode defect = mapper.readTree(performJson(inspector, "POST", "/api/defects", defectBody, 200));
    long defectId = defect.get("id").asLong();
    assertFalse(defect.get("duplicated").asBoolean());
    assertEquals("CRITICAL", defect.get("severity").asText());
    assertEquals("OPEN", defect.get("dispositionStatus").asText());

    JsonNode traceAfterDefect = mapper.readTree(
        performJson(inspector, "GET", "/api/batches/trace/B-IT-001", null, 200));
    assertEquals("PENDING", traceAfterDefect.path("conclusion").path("conclusionStatus").asText());
    assertEquals(1, traceAfterDefect.path("summary").path("criticalOpen").asInt());
    assertTrue(traceAfterDefect.path("summary").path("hasUnhandledSevereDefect").asBoolean());

    // 3. 即使终检合格，只要严重不良未关闭也不能放行
    String inspectionBody = "{\"batchNo\":\"B-IT-001\",\"inspectorId\":1001,"
        + "\"inspectionType\":\"FINAL\",\"standardVersion\":\"v1\",\"items\":["
        + "{\"itemCode\":\"DIM-A\",\"itemName\":\"长度\",\"measuredValue\":10.01,"
        + "\"limitMin\":9.9,\"limitMax\":10.1}]}";
    JsonNode inspection = mapper.readTree(
        performJson(inspector, "POST", "/api/inspections", inspectionBody, 200));
    assertEquals("PASS", inspection.get("resultStatus").asText());

    JsonNode traceAfterFinal = mapper.readTree(
        performJson(inspector, "GET", "/api/batches/trace/B-IT-001", null, 200));
    assertEquals("PENDING", traceAfterFinal.path("conclusion").path("conclusionStatus").asText());

    // 4. 重复提交同一不良 -> 命中原记录，数量不累计
    JsonNode dupDefect = mapper.readTree(
        performJson(inspector, "POST", "/api/defects", defectBody, 200));
    assertTrue(dupDefect.get("duplicated").asBoolean());
    assertEquals(defectId, dupDefect.get("id").asLong());
    JsonNode traceDup = mapper.readTree(
        performJson(inspector, "GET", "/api/batches/trace/B-IT-001", null, 200));
    assertEquals(1, traceDup.path("summary").path("defectCount").asInt());
    assertEquals(3, traceDup.path("summary").path("totalDefectQty").asInt());

    // 重复提交同一检验单 -> 命中原记录
    JsonNode dupInspection = mapper.readTree(
        performJson(inspector, "POST", "/api/inspections", inspectionBody, 200));
    assertTrue(dupInspection.get("duplicated").asBoolean());

    // 5. 先推进到处置中 -> 仍未关闭，结论仍待处理
    String progressBody = "{\"dispositionStatus\":\"IN_PROGRESS\",\"dispositionNote\":\"返工中\"}";
    performJson(manager, "POST", "/api/defects/" + defectId + "/disposition", progressBody, 200);
    JsonNode traceInProgress = mapper.readTree(
        performJson(inspector, "GET", "/api/batches/trace/B-IT-001", null, 200));
    assertEquals("PENDING", traceInProgress.path("conclusion").path("conclusionStatus").asText());

    // 6. 关闭不良 -> 终检合格 -> 可放行
    String closeBody = "{\"dispositionStatus\":\"CLOSED\",\"dispositionNote\":\"返工复检合格\"}";
    JsonNode closed = mapper.readTree(
        performJson(manager, "POST", "/api/defects/" + defectId + "/disposition", closeBody, 200));
    assertFalse(closed.get("duplicated").asBoolean());
    assertEquals("CLOSED", closed.get("dispositionStatus").asText());

    JsonNode traceReleased = mapper.readTree(
        performJson(inspector, "GET", "/api/batches/trace/B-IT-001", null, 200));
    assertEquals("RELEASABLE",
        traceReleased.path("conclusion").path("conclusionStatus").asText());
    assertEquals(0, traceReleased.path("summary").path("criticalOpen").asInt());
    assertFalse(traceReleased.path("summary").path("hasUnhandledSevereDefect").asBoolean());

    // 已关闭再处置 -> 幂等返回
    JsonNode closeAgain = mapper.readTree(
        performJson(manager, "POST", "/api/defects/" + defectId + "/disposition", closeBody, 200));
    assertTrue(closeAgain.get("duplicated").asBoolean());

    // 7. 结论变化历史：PENDING -> RELEASABLE 至少一次
    JsonNode history = mapper.readTree(performJson(inspector, "GET",
        "/api/batches/trace/B-IT-001/conclusion/history", null, 200));
    boolean hasReleaseTransition = false;
    for (JsonNode node : history) {
      if ("PENDING".equals(node.path("fromStatus").asText())
          && "RELEASABLE".equals(node.path("toStatus").asText())) {
        hasReleaseTransition = true;
      }
    }
    assertTrue(hasReleaseTransition, "必须记录 PENDING -> RELEASABLE 的结论变化");

    // 8. 追溯树包含工单、检验项、不良
    assertEquals("WO-IT-001", traceReleased.path("workOrder").path("orderNo").asText());
    assertEquals(1, traceReleased.path("inspections").size());
    assertEquals(1, traceReleased.path("inspections").get(0).path("items").size());
    assertEquals(1, traceReleased.path("defects").size());
    assertEquals(batchId, traceReleased.path("batch").path("id").asLong());
  }

  @Test
  void closedDefectWithoutFinalInspection_endsInRecheck() throws Exception {
    String manager = token("manager", "manager123");

    String orderBody = "{\"orderNo\":\"WO-IT-002\",\"productCode\":\"P-2\",\"productName\":\"齿轮\","
        + "\"plannedQty\":10,\"lineCode\":\"L2\"}";
    JsonNode order = mapper.readTree(performJson(manager, "POST", "/api/work-orders", orderBody, 200));
    String batchBody = "{\"batchNo\":\"B-IT-002\",\"workOrderId\":" + order.get("id").asLong()
        + ",\"quantity\":10,\"materialLotNo\":\"M2\"}";
    performJson(manager, "POST", "/api/batches", batchBody, 200);

    String defectBody = "{\"batchNo\":\"B-IT-002\",\"defectType\":\"异响\",\"defectQty\":1,"
        + "\"severity\":\"MAJOR\",\"rootCause\":\"装配偏差\"}";
    JsonNode defect = mapper.readTree(performJson(manager, "POST", "/api/defects", defectBody, 200));

    JsonNode before = mapper.readTree(
        performJson(manager, "GET", "/api/batches/trace/B-IT-002", null, 200));
    assertEquals("PENDING", before.path("conclusion").path("conclusionStatus").asText());

    String closeBody = "{\"dispositionStatus\":\"CLOSED\",\"dispectionNote\":\"x\"}";
    // 字段名笔误也要能通过校验（dispositionNote 可空），这里给正确字段
    closeBody = "{\"dispositionStatus\":\"CLOSED\",\"dispositionNote\":\"已处理\"}";
    performJson(manager, "POST", "/api/defects/" + defect.get("id").asLong()
        + "/disposition", closeBody, 200);

    JsonNode after = mapper.readTree(
        performJson(manager, "GET", "/api/batches/trace/B-IT-002", null, 200));
    // 严重不良处置完但没有终检 -> 待复检
    assertEquals("RECHECK", after.path("conclusion").path("conclusionStatus").asText());
  }

  @Test
  void authAndRbacAreEnforced() throws Exception {
    // 无 token -> 401
    mockMvc.perform(get("/api/batches")).andExpect(status().isUnauthorized());

    String auditor = token("auditor", "auditor123");
    // 审计员只读，不能登记检验 -> 403
    String body = "{\"batchNo\":\"B2026092401\",\"inspectionType\":\"PATROL\",\"items\":["
        + "{\"itemCode\":\"X\",\"measuredValue\":1}]}";
    performJson(auditor, "POST", "/api/inspections", body, 403);

    // 错误密码 -> 401
    performJson(null, "POST", "/api/auth/login",
        "{\"username\":\"auditor\",\"password\":\"wrong\"}", 401);
  }
}
