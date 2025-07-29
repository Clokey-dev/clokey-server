package org.clokey.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.clokey.code.BaseSuccessCode;
import org.clokey.exception.BaseErrorCode;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class BaseResponse<T> {

    @JsonProperty("isSuccess")
    private final Boolean isSuccess;

    private final String code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    public static <T> BaseResponse<T> onSuccess(BaseSuccessCode code, T result) {
        return new BaseResponse<>(
                true, code.getReasonDto().code(), code.getReasonDto().message(), result);
    }

    public static <T> BaseResponse<T> onFailure(BaseErrorCode code, T result) {
        return new BaseResponse<>(
                false, code.getErrorReason().code(), code.getErrorReason().message(), result);
    }

    public static <T> BaseResponse<T> onFailure(String code, String message, T data) {
        return new BaseResponse<>(false, code, message, data);
    }
}
