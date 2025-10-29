package org.clokey.domain.lookbook.service;

import org.clokey.domain.lookbook.dto.request.LookBookCreateRequest;
import org.clokey.domain.lookbook.dto.request.LookBookUpdateRequest;
import org.clokey.domain.lookbook.dto.response.LookBookCreateResponse;

public interface LookBookService {

    LookBookCreateResponse createLookBook(LookBookCreateRequest request);

    void updateLookBook(Long lookBookId, LookBookUpdateRequest request);
}
