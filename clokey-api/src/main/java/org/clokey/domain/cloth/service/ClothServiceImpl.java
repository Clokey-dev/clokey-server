package org.clokey.domain.cloth.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.clokey.category.entity.Category;
import org.clokey.cloth.entity.Cloth;
import org.clokey.domain.category.repository.CategoryRepository;
import org.clokey.domain.cloth.dto.request.ClothCreateRequest;
import org.clokey.domain.cloth.dto.request.ClothCreateRequests;
import org.clokey.domain.cloth.dto.response.ClothCreateResponse;
import org.clokey.domain.cloth.repository.ClothRepository;
import org.clokey.global.FakeAuthContext;
import org.clokey.member.entity.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClothServiceImpl implements ClothService {

    private final FakeAuthContext fakeAuthContext;

    private final ClothRepository clothRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public ClothCreateResponse createCloths(ClothCreateRequests request) {
        final Member currentMember = fakeAuthContext.getCurrentMember();

        List<Category> categories =
                categoryRepository.findAllByIdInOrder(
                        request.content().stream()
                                .map(ClothCreateRequest::categoryId)
                                .collect(Collectors.toList()));

        List<Cloth> cloths =
                IntStream.range(0, request.content().size())
                        .mapToObj(
                                i -> {
                                    ClothCreateRequest cr = request.content().get(i);
                                    Category category = categories.get(i);
                                    return Cloth.createCloth(
                                            cr.clothImageUrl(), category, currentMember);
                                })
                        .toList();

        clothRepository.saveAll(cloths);

        return ClothCreateResponse.from(cloths);
    }
}
