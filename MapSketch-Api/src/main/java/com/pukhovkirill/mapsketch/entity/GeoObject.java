package com.pukhovkirill.mapsketch.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(fluent = true, chain = true)
public final class GeoObject extends BaseEntity {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private GeoObjectType type;

    @NotNull
    private String coordinates;

    public enum GeoObjectType {
        POINT("Point"),
        POLYGON("Polygon"),
        LINE_STRING("LineString");

        private final String type;

        GeoObjectType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return this.type;
        }

        public static GeoObjectType fromString(String type) {
            for (GeoObjectType geoType : GeoObjectType.values()) {
                if (geoType.type.equalsIgnoreCase(type)) {
                    return geoType;
                }
            }
            throw new IllegalArgumentException("Unknown GeoObjectType: " + type);
        }
    }
}

