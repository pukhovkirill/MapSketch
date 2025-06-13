package com.pukhovkirill.mapsketch.dto;

import java.util.List;

import lombok.Data;
import lombok.Builder;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
public final class GeoObjectResource {

    @NotNull
    private final String type;

    @NotNull
    private final List<GeoObjectPayload> features;

}
