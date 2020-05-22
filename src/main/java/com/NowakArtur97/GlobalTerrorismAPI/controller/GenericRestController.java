package com.NowakArtur97.GlobalTerrorismAPI.controller;

import com.NowakArtur97.GlobalTerrorismAPI.dto.DTONode;
import com.NowakArtur97.GlobalTerrorismAPI.mediaType.PatchMediaType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.json.JsonMergePatch;
import javax.json.JsonPatch;
import javax.validation.Valid;

public interface GenericRestController<R> {

    @GetMapping
    ResponseEntity<PagedModel<R>> findAll(@PageableDefault(size = 100) Pageable pageable);

    @GetMapping(path = "/{id}")
    ResponseEntity<R> findById(Long id);

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
        // Added to remove the default 200 status added by Swagger
    ResponseEntity<R> add(DTONode dto);

    @PutMapping(path = "/{id}")
    ResponseEntity<R> update(@PathVariable("id") Long id, @RequestBody @Valid DTONode dto);

    @PatchMapping(path = "/{id}", consumes = PatchMediaType.APPLICATION_JSON_PATCH_VALUE)
    ResponseEntity<R> updateFields(@PathVariable("id") Long id, @RequestBody JsonPatch objectAsJsonPatch);

    @PatchMapping(path = "/{id2}", consumes = PatchMediaType.APPLICATION_JSON_MERGE_PATCH_VALUE)
    ResponseEntity<R> updateFields(@PathVariable("id2") Long id, @RequestBody JsonMergePatch objectAsJsonMergePatch);

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
        // Added to remove the default 200 status added by Swagger
    ResponseEntity<Void> delete(@PathVariable("id") Long id);
}
