package org.clokey.domain.lookbook.service;

import lombok.RequiredArgsConstructor;
import org.clokey.domain.lookbook.dto.request.LookBookCreateRequest;
import org.clokey.domain.lookbook.dto.response.LookBookCreateResponse;
import org.clokey.domain.lookbook.repository.LookBookRepository;
import org.clokey.global.util.MemberUtil;
import org.clokey.lookbook.entity.LookBook;
import org.clokey.member.entity.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LookBookServiceImpl implements LookBookService {

    private final LookBookRepository lookBookRepository;

    private final MemberUtil memberUtil;

    @Override
    @Transactional
    public LookBookCreateResponse createLookBook(LookBookCreateRequest request) {
        final Member currentMember = memberUtil.getCurrentMember();

        LookBook lookBook = LookBook.createLookBook(request.name(), currentMember);
        lookBookRepository.save(lookBook);

        return LookBookCreateResponse.from(lookBook);
    }
}
