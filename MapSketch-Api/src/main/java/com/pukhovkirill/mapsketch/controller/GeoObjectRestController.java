package com.pukhovkirill.mapsketch.controller;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.pukhovkirill.mapsketch.dto.GeoObjectResource;
import com.pukhovkirill.mapsketch.converter.GeoObjectConverter;
import com.pukhovkirill.mapsketch.exception.BadRequestException;
import com.pukhovkirill.mapsketch.repository.GeoObjectRepository;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class GeoObjectRestController {

    private final GeoObjectConverter converter;
    private final GeoObjectRepository repository;

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/geoobjects/{id}", method = RequestMethod.PUT)
    public String rename(@PathVariable Long id, @RequestBody String newName) {
        log.trace("rename called with id={}, newName='{}'", id, newName);
        if (newName == null || newName.isBlank()) {
            log.error("New name cannot be empty: '{}'", newName);
            throw new BadRequestException("New name cannot be empty");
        }

        var entity = repository.findById(id);
        log.trace("repository.findById({}) returned {}", id, entity);
        if (entity == null) {
            log.error("Object with id {} not found", id);
            throw new BadRequestException("Object with id " + id + " not found");
        }

        entity.name(newName);
        log.trace("Set new name for entity {}: {}", id, newName);

        repository.update(id, entity);
        log.trace("repository.update called for id={}", id);

        return "success";
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/geoobjects", method = RequestMethod.POST)
    public GeoObjectResource saveAll(@RequestBody GeoObjectResource obj) {
        log.trace("saveAll called with obj={}", obj);
        if (obj == null || obj.getFeatures().isEmpty()) {
            log.error("Features cannot be empty: {}", obj);
            throw new BadRequestException("Features cannot be empty");
        }

        var features = obj.getFeatures();
        log.trace("Features count={}", features.size());

        var newObjects = features.stream()
                .filter(x -> x.getProperties().containsKey("isNewFeature"))
                .map(converter::convert)
                .toList();
        log.trace("New objects to save count={}", newObjects.size());

        if (!newObjects.isEmpty()) {
            log.trace("Saving new objects: {}", newObjects);
            repository.saveAll(newObjects);
        }

        var existingObjects = features.stream()
                .filter(x -> x.getProperties().containsKey("isModified")
                        || !x.getProperties().containsKey("isNewFeature"))
                .filter(x -> (boolean) x.getProperties().get("isModified"))
                .map(converter::convert)
                .toList();
        log.trace("Existing objects to update count={}", existingObjects.size());

        existingObjects.forEach(x -> {
            repository.update(x.id(), x);
            log.trace("Updated existing object id={}", x.id());
        });

        var saved = newObjects.stream()
                .map(converter::convert)
                .toList();
        log.trace("Converted saved objects back to DTOs count={}", saved.size());

        GeoObjectResource result = GeoObjectResource.builder()
                .type("FeatureCollection")
                .features(saved)
                .build();
        log.trace("Returning GeoObjectResource with {} features", saved.size());
        return result;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/geoobjects/{id}", method = RequestMethod.DELETE)
    public Long delete(@PathVariable Long id) {
        log.trace("delete called with id={}", id);
        repository.deleteById(id);
        log.trace("Deleted entity id={}", id);
        return id;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/geoobjects/list", method = RequestMethod.GET)
    public GeoObjectResource findAll() {
        log.trace("findAll called");
        var list = repository.findAll().stream()
                .map(converter::convert)
                .toList();
        log.trace("Found {} objects", list.size());

        GeoObjectResource result = GeoObjectResource.builder()
                .type("FeatureCollection")
                .features(list)
                .build();
        log.trace("Returning GeoObjectResource with {} features", list.size());
        return result;
    }

}
