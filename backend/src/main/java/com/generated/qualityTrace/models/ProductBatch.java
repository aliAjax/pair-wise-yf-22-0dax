package com.generated.qualityTrace.models;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("product_batch")
public class ProductBatch {
  @TableId(type = IdType.INPUT)
  private Long id;
  private String batchNo;
  private Long workOrderId;
  private Integer quantity;
  private String materialLotNo;
  private LocalDateTime producedAt;
  private String batchStatus;
  private LocalDateTime createdAt;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getBatchNo() { return batchNo; }
  public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
  public Long getWorkOrderId() { return workOrderId; }
  public void setWorkOrderId(Long workOrderId) { this.workOrderId = workOrderId; }
  public Integer getQuantity() { return quantity; }
  public void setQuantity(Integer quantity) { this.quantity = quantity; }
  public String getMaterialLotNo() { return materialLotNo; }
  public void setMaterialLotNo(String materialLotNo) { this.materialLotNo = materialLotNo; }
  public LocalDateTime getProducedAt() { return producedAt; }
  public void setProducedAt(LocalDateTime producedAt) { this.producedAt = producedAt; }
  public String getBatchStatus() { return batchStatus; }
  public void setBatchStatus(String batchStatus) { this.batchStatus = batchStatus; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
