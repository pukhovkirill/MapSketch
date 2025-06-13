package com.pukhovkirill.mapsketch.converter;

import java.util.*;
import java.text.NumberFormat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.pukhovkirill.mapsketch.dto.Geometry;
import com.pukhovkirill.mapsketch.entity.GeoObject;
import com.pukhovkirill.mapsketch.dto.GeoObjectPayload;

@Slf4j
@Service
public class GeoObjectConverter extends BaseConverter<GeoObject, GeoObjectPayload> {

    private static final int MAX_FRACTION_DIGIT = 15;
    private static final Gson gson;
    private static final NumberFormat nf;

    static {
        gson = new GsonBuilder().setPrettyPrinting().create();
        nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(MAX_FRACTION_DIGIT);
    }

    @Override
    public GeoObject convert(GeoObjectPayload dto) {
        if (dto == null) {
            log.error("GeoObjectPayload is null");
            throw new IllegalArgumentException("GeoObjectPayload cannot be null");
        }

        Map<String, Object> props = dto.getProperties();
        Long id = getLongProperty(props, "id");
        String name = getStringProperty(props, "name");

        GeoObject.GeoObjectType type = GeoObject.GeoObjectType.fromString(
                dto.getGeometry().getType()
        );
        String coordinates = serializeCoordinates(dto.getGeometry());

        return new GeoObject()
                .id(id)
                .name(name)
                .type(type)
                .coordinates(coordinates);
    }

    @Override
    public GeoObjectPayload convert(GeoObject entity) {
        if (entity == null) {
            log.error("GeoObject is null");
            throw new IllegalArgumentException("GeoObject cannot be null");
        }

        List<Object> coords = deserializeCoordinates(
                entity.coordinates(),
                entity.type()
        );

        var geometryBuilder = Geometry.builder()
                .type(entity.type().toString());

        if (entity.type() == GeoObject.GeoObjectType.POLYGON) {
            geometryBuilder.coordinates(List.of(coords));
        } else {
            geometryBuilder.coordinates(coords);
        }

        Geometry geometry = geometryBuilder.build();
        Map<String, Object> props = Map.of(
                "id", entity.id(),
                "name", entity.name()
        );

        return GeoObjectPayload.builder()
                .type("Feature")
                .geometry(geometry)
                .properties(props)
                .build();
    }

    private String serializeCoordinates(Geometry geometry) {
        log.trace("serializeCoordinates called: type={}, coords={}",
                geometry.getType(), geometry.getCoordinates()
        );
        List<?> coordinates = geometry.getCoordinates();
        GeoObject.GeoObjectType type = GeoObject.GeoObjectType.fromString(
                geometry.getType()
        );

        return switch (type) {
            case POINT -> formatPoint(coordinates);
            case LINE_STRING -> formatPath(coordinates);
            case POLYGON -> formatPath((List<?>) coordinates.getFirst());
        };
    }

    private List<Object> deserializeCoordinates(String coordJson, GeoObject.GeoObjectType type) {
        log.trace("deserializeCoordinates input JSON: {}", coordJson);
        List<?> parsed = gson.fromJson(coordJson, List.class);
        log.trace("Parsed JSON into List: {}", parsed);

        if (type == GeoObject.GeoObjectType.POINT) {
            if (parsed.size() != 2) {
                log.error("Point must have exactly 2 coordinates, found: {}", parsed.size());
                throw new IllegalArgumentException("Point must have exactly 2 coordinates");
            }
            return new ArrayList<>(parsed);
        }

        List<Object> result = new ArrayList<>();
        for (Object obj : parsed) {
            if (obj instanceof List<?> point && point.size() == 2) {
                result.add(List.of(point.get(0), point.get(1)));
            } else {
                log.error("Each coordinate must be a list of 2 elements, invalid: {}", obj);
                throw new IllegalArgumentException("Each coordinate must be a list of 2 elements");
            }
        }
        return result;
    }

    private String formatPoint(List<?> coords) {
        log.trace("formatPoint coords: {}", coords);
        if (coords.size() != 2) {
            log.error("POINT must have exactly 2 coordinates, found: {}", coords.size());
            throw new IllegalArgumentException("POINT must have exactly 2 coordinates");
        }
        String lon = nf.format(toDouble(coords.get(0)));
        String lat = nf.format(toDouble(coords.get(1)));
        String result = String.format("[%s,%s]", lon, lat);
        log.trace("formatPoint result: {}", result);
        return result;
    }

    private String formatPath(List<?> coords) {
        log.trace("formatPath coords list size: {}", coords.size());
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < coords.size(); i++) {
            Object obj = coords.get(i);
            if (!(obj instanceof List<?> point) || point.size() != 2) {
                log.error("Each coordinate must be a list of 2 elements, invalid: {}", obj);
                throw new IllegalArgumentException("Each coordinate must be a list of 2 elements");
            }
            String lon = nf.format(toDouble(point.get(0)));
            String lat = nf.format(toDouble(point.get(1)));
            builder.append(String.format("[%s,%s]", lon, lat));
            if (i < coords.size() - 1) {
                builder.append(", ");
            }
        }
        builder.append("]");
        String result = builder.toString();
        log.trace("formatPath result: {}", result);
        return result;
    }

    private double toDouble(Object obj) {
        if (obj instanceof Number num) {
            return num.doubleValue();
        }
        log.error("Coordinate is not a number: {}", obj);
        throw new IllegalArgumentException("Coordinate is not a number: " + obj);
    }

    private Long getLongProperty(Map<String, Object> props, String key) {
        Object value = props.get(key);
        if (value instanceof Number num) {
            return num.longValue();
        }
        return null;
    }

    private String getStringProperty(Map<String, Object> props, String key) {
        Object value = props.get(key);
        if (value instanceof String str) {
            return str;
        }
        log.error("Missing or invalid string property '{}': {}", key, value);
        throw new IllegalArgumentException("Missing or invalid string property: " + key);
    }
}
