package com.pukhovkirill.mapsketch.dto;

import java.util.List;

import lombok.Data;
import lombok.Builder;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
public final class Geometry {

    @NotNull
    private final String type;

    @NotNull
    private final List<Object> coordinates;

}
