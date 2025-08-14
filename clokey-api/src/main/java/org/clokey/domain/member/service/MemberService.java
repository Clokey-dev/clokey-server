package org.clokey.domain.member.service;

import org.clokey.domain.member.dto.request.ProfileRequest;
import org.clokey.domain.member.dto.response.ProfileResponse;

public interface MemberService {

    ProfileResponse updateProfile(ProfileRequest request);

    void clokeyIdUsingCheck(String clokeyId);
}
