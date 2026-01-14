package org.clokey.domain.cloth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ClothInfoExtractAiResponseDTO(@JsonProperty("result") List<ResultItem> result) {

    public record ResultItem(
            @JsonProperty("categories") List<CategoryItem> categories,
            @JsonProperty("seasons") List<SeasonItem> seasons) {}

    public record CategoryItem(@JsonProperty("id") Long id, @JsonProperty("name") String name) {}

    public record SeasonItem(@JsonProperty("id") Long id, @JsonProperty("name") String name) {}
}
