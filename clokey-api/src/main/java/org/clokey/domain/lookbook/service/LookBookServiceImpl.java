package org.clokey.domain.lookbook.service;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.clokey.domain.lookbook.dto.request.LookBookCreateRequest;
import org.clokey.domain.lookbook.dto.request.LookBookUpdateRequest;
import org.clokey.domain.lookbook.dto.response.LookBookCreateResponse;
import org.clokey.domain.lookbook.exception.LookBookErrorCode;
import org.clokey.domain.lookbook.repository.LookBookRepository;
import org.clokey.exception.BaseCustomException;
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

    @Override
    @Transactional
    public void updateLookBook(Long lookBookId, LookBookUpdateRequest request) {
        final Member currentMember = memberUtil.getCurrentMember();
        final LookBook lookBook = getLookBookById(lookBookId);

        validateLookBookOwner(lookBook, currentMember.getId());
        lookBook.updateLookBook(request.name());
    }

    private void validateLookBookOwner(LookBook lookBook, Long memberId) {
        if (!Objects.equals(lookBook.getMember().getId(), memberId)) {
            throw new BaseCustomException(LookBookErrorCode.NOT_LOOK_BOOK_OWNER);
        }
    }

    private LookBook getLookBookById(Long lookBookId) {
        return lookBookRepository
                .findById(lookBookId)
                .orElseThrow(() -> new BaseCustomException(LookBookErrorCode.LOOK_BOOK_NOT_FOUND));
    }
}
