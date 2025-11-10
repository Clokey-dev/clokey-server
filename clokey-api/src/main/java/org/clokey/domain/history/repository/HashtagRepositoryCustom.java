package org.clokey.domain.history.repository;

import java.util.List;
import org.clokey.history.entity.Hashtag;

public interface HashtagRepositoryCustom {
    List<Hashtag> bulkInsertHashtags(List<Hashtag> links);
}
