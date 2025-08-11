package org.clokey.domain.member.service;

import org.clokey.domain.cloth.dto.request.ClothCreateRequests;
import org.clokey.domain.cloth.dto.response.ClothCreateResponse;
import org.clokey.domain.member.dto.request.ProfileRequest;
import org.clokey.domain.member.dto.response.ProfileResponse;

public interface MemberService {

    ProfileResponse updateProfile(ProfileRequest request);
}
