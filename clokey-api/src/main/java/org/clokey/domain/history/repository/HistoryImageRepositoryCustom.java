package org.clokey.domain.history.repository;

import java.util.List;
import org.clokey.history.entity.HistoryImage;

public interface HistoryImageRepositoryCustom {
    void bulkInsertHistoryImages(List<HistoryImage> images);
}
