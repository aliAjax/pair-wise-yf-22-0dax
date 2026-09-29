package com.generated.qualityTrace.types;

import java.util.List;

/** 不良处置进度汇总，客户追查批次时一眼看到还有几条严重不良挂着。 */
public record DispositionProgressDto(
    int totalDefects,
    int openCount,
    int closedCount,
    int seriousTotal,
    int seriousOpenCount,
    List<String> openSeriousDefectIds) {

  public boolean hasOpenSerious() {
    return seriousOpenCount > 0;
  }
}
