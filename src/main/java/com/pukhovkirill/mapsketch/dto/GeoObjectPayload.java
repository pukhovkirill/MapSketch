package com.pukhovkirill.mapsketch.dto;

import java.util.Map;

import lombok.Data;
import lombok.Builder;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
public final class GeoObjectPayload {

    @NotNull
    private final String type;

    @NotNull
    private final Geometry geometry;

    @NotNull
    private final Map<String, Object> properties;

}
