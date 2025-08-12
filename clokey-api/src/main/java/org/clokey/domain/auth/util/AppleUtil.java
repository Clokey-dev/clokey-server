package org.clokey.domain.auth.util;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.clokey.properties.OauthProperties;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@CacheConfig(cacheManager = "appleSecretClientManager", cacheNames = "appleClientSecret")
public class AppleUtil {

    private final OauthProperties oauthProperties;

    /** Apple Client Secret을 받급합니다. (180일 단위로 캐싱) */
    @Cacheable(key = "'v1'", sync = true)
    public String getClientSecret() {
        return generateClientSecretJwt();
    }

    /** 캐시 미스가 일어날 경우 재생성 후 레디스에 저장 */
    private String generateClientSecretJwt() {
        try {
            String privateKeyPem =
                    oauthProperties
                            .apple()
                            .privateKey()
                            .replace("-----BEGIN PRIVATE KEY-----", "")
                            .replace("-----END PRIVATE KEY-----", "")
                            .replaceAll("\\s", "");
            byte[] pkcs8 = Base64.getDecoder().decode(privateKeyPem);
            var keySpec = new PKCS8EncodedKeySpec(pkcs8);
            var kf = KeyFactory.getInstance("EC");
            var privateKey = (java.security.interfaces.ECPrivateKey) kf.generatePrivate(keySpec);

            var header =
                    new com.nimbusds.jose.JWSHeader.Builder(JWSAlgorithm.ES256)
                            .keyID(oauthProperties.apple().keyId())
                            .type(JOSEObjectType.JWT)
                            .build();

            var now = Instant.now();
            var exp = now.plusSeconds(60L * 60 * 24 * 180);
            var claims =
                    new JWTClaimsSet.Builder()
                            .issuer(oauthProperties.apple().teamId()) // iss
                            .subject(oauthProperties.apple().clientId()) // sub (Service ID)
                            .audience("https://appleid.apple.com") // aud
                            .issueTime(Date.from(now)) // iat
                            .expirationTime(Date.from(exp)) // exp
                            .build();

            var signed = new SignedJWT(header, claims);
            signed.sign(new ECDSASigner(privateKey));
            return signed.serialize();

        } catch (Exception e) {
            throw new IllegalStateException("Failed to create Apple client_secret", e);
        }
    }
}
