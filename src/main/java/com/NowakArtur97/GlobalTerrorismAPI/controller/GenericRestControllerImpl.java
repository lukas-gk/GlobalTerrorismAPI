package com.NowakArtur97.GlobalTerrorismAPI.controller;

import com.NowakArtur97.GlobalTerrorismAPI.dto.DTONode;
import com.NowakArtur97.GlobalTerrorismAPI.exception.ResourceNotFoundException;
import com.NowakArtur97.GlobalTerrorismAPI.mediaType.PatchMediaType;
import com.NowakArtur97.GlobalTerrorismAPI.node.Node;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.util.patch.PatchHelper;
import com.NowakArtur97.GlobalTerrorismAPI.util.violation.ViolationHelper;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.json.JsonMergePatch;
import javax.json.JsonPatch;
import javax.validation.Valid;
import java.util.Optional;

@RestController
public abstract class GenericRestControllerImpl<M extends RepresentationModel<M>, D extends DTONode, T extends Node>
        implements GenericRestController<M, D> {

    private final int DEFAULT_DEPTH_FOR_JSON_PATCH = 5;

    private final String modelType;

    private final Class<T> nodeTypeParameterClass;

    private final Class<D> dtoTypeParameterClass;

    protected final GenericService<T, D> service;

    private final RepresentationModelAssemblerSupport<T, M> modelAssembler;

    protected final PagedResourcesAssembler<T> pagedResourcesAssembler;

    protected final PatchHelper patchHelper;

    protected final ViolationHelper<T, D> violationHelper;

    protected GenericRestControllerImpl(GenericService<T, D> service,
                                        RepresentationModelAssemblerSupport<T, M> modelAssembler,
                                        PagedResourcesAssembler<T> pagedResourcesAssembler,
                                        PatchHelper patchHelper, ViolationHelper<T, D> violationHelper) {

        Class<M> modelTypeParameterClass = (Class<M>) GenericTypeResolver.resolveTypeArguments(getClass(),
                GenericRestControllerImpl.class)[0];

        this.nodeTypeParameterClass = (Class<T>) GenericTypeResolver.resolveTypeArguments(getClass(),
                GenericRestControllerImpl.class)[2];
        this.dtoTypeParameterClass = (Class<D>) GenericTypeResolver.resolveTypeArguments(getClass(),
                GenericRestControllerImpl.class)[1];
        this.modelType = modelTypeParameterClass.getSimpleName();
        this.service = service;
        this.modelAssembler = modelAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.patchHelper = patchHelper;
        this.violationHelper = violationHelper;
    }

    @GetMapping
    @Override
    public ResponseEntity<PagedModel<M>> findAll(Pageable pageable) {

        Page<T> resources = service.findAll(pageable);
        PagedModel<M> pagedModel = pagedResourcesAssembler.toModel(resources, modelAssembler);

        return new ResponseEntity<>(pagedModel, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    @Override
    public ResponseEntity<M> findById(@PathVariable("id") Long id) {

        return service.findById(id).map(modelAssembler::toModel).map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(modelType, id));
    }

    @PostMapping
    @Override
    public ResponseEntity<M> add(@RequestBody @Valid D dto) {

        T node = service.saveNew(dto);

        M resource = modelAssembler.toModel(node);

        return new ResponseEntity<>(resource, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{id}")
    @Override
    public ResponseEntity<M> update(@PathVariable("id") Long id, @RequestBody @Valid D dto) {

        Optional<T> nodeOptional = service.findById(id);

        if (id != null && nodeOptional.isPresent()) {

            T node = service.update(nodeOptional.get(), dto);

            return new ResponseEntity<>(modelAssembler.toModel(node), HttpStatus.OK);

        } else {

            T node = service.saveNew(dto);

            return new ResponseEntity<>(modelAssembler.toModel(node), HttpStatus.CREATED);
        }
    }

    @PatchMapping(path = "/{id}", consumes = PatchMediaType.APPLICATION_JSON_PATCH_VALUE)
    @Override
    public ResponseEntity<M> updateFields(@PathVariable("id") Long id, @RequestBody JsonPatch objectAsJsonPatch) {

        T node = service.findById(id, DEFAULT_DEPTH_FOR_JSON_PATCH).orElseThrow(() -> new ResourceNotFoundException(modelType, id));

        T nodePatched = patchHelper.patch(objectAsJsonPatch, node, nodeTypeParameterClass);

        violationHelper.violate(nodePatched, dtoTypeParameterClass);

        nodePatched = service.save(nodePatched);

        M resource = modelAssembler.toModel(nodePatched);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @PatchMapping(path = "/{id2}", consumes = PatchMediaType.APPLICATION_JSON_MERGE_PATCH_VALUE)
    @Override
    public ResponseEntity<M> updateFields(@PathVariable("id2") Long id, @RequestBody JsonMergePatch objectAsJsonMergePatch) {

        T node = service.findById(id, DEFAULT_DEPTH_FOR_JSON_PATCH).orElseThrow(() -> new ResourceNotFoundException(modelType, id));

        T nodePatched = patchHelper.mergePatch(objectAsJsonMergePatch, node, nodeTypeParameterClass);

        violationHelper.violate(nodePatched, dtoTypeParameterClass);

        nodePatched = service.save(nodePatched);

        M resource = modelAssembler.toModel(nodePatched);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {

        service.delete(id).orElseThrow(() -> new ResourceNotFoundException(modelType, id));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    @Override
    public ResponseEntity<?> collectionOptions() {

        return ResponseEntity
                .ok()
                .allow(HttpMethod.GET, HttpMethod.POST, HttpMethod.OPTIONS)
                .build();
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.OPTIONS)
    @Override
    public ResponseEntity<?> singularOptions() {

        return ResponseEntity
                .ok()
                .allow(HttpMethod.GET, HttpMethod.PUT, HttpMethod.PATCH, HttpMethod.DELETE, HttpMethod.OPTIONS)
                .build();
    }
}
