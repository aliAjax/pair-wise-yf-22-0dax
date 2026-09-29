package com.generated.qualityTrace.models;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("work_order")
public class WorkOrder {
  @TableId(type = IdType.INPUT)
  private Long id;
  private String orderNo;
  private String productCode;
  private String productName;
  private Integer plannedQty;
  private String lineCode;
  private LocalDateTime startAt;
  private String status;
  private LocalDateTime createdAt;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getOrderNo() { return orderNo; }
  public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
  public String getProductCode() { return productCode; }
  public void setProductCode(String productCode) { this.productCode = productCode; }
  public String getProductName() { return productName; }
  public void setProductName(String productName) { this.productName = productName; }
  public Integer getPlannedQty() { return plannedQty; }
  public void setPlannedQty(Integer plannedQty) { this.plannedQty = plannedQty; }
  public String getLineCode() { return lineCode; }
  public void setLineCode(String lineCode) { this.lineCode = lineCode; }
  public LocalDateTime getStartAt() { return startAt; }
  public void setStartAt(LocalDateTime startAt) { this.startAt = startAt; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
