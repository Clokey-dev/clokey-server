package org.clokey.domain.coordinate.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.clokey.cloth.entity.Cloth;
import org.clokey.coordinate.entity.Coordinate;
import org.clokey.domain.cloth.exception.ClothErrorCode;
import org.clokey.domain.cloth.repository.ClothRepository;
import org.clokey.domain.coordinate.dto.request.DailyCoordinateCreateRequest;
import org.clokey.domain.coordinate.dto.response.DailyCoordinateCreateResponse;
import org.clokey.domain.coordinate.repository.CoordinateRepository;
import org.clokey.exception.BaseCustomException;
import org.clokey.global.util.MemberUtil;
import org.clokey.member.entity.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoordinateServiceImpl implements CoordinateService {

    private final MemberUtil memberUtil;

    private final CoordinateRepository coordinateRepository;
    private final ClothRepository clothRepository;

    @Override
    @Transactional
    public DailyCoordinateCreateResponse createDailyCoordinate(DailyCoordinateCreateRequest request) {
        final Member currentMember = memberUtil.getCurrentMember();
        final List<Cloth> clothes = clothRepository.findAllById(request.clothIds());



        validateAllClothesExist(request,clothes);
        validateAllClothesOwnership(currentMember,clothes);


        Coordinate coordinate = Coordinat


        return null;
    }


    private void validateAllClothesExist(DailyCoordinateCreateRequest request, List<Cloth> clothes){
        if(!Objects.equals(request.clothIds().size(),clothes.size())){
            throw new BaseCustomException(ClothErrorCode.ClOTH_NOT_FOUND);
        }
    }

    private void validateAllClothesOwnership(Member member, List<Cloth> clothes) {
        boolean containsClothesNotMine = clothes.stream()
                .anyMatch(cloth -> !cloth.getMember().getId().equals(member.getId()));

        if (containsClothesNotMine) {
            throw new BaseCustomException(ClothErrorCode.NOT_CLOTH_OWNER);
        }
    }

    private void validateDailyCoordinateExist(){

    }
}
