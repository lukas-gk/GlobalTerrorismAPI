package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.dto.DTONode;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.Node;
import com.NowakArtur97.GlobalTerrorismAPI.repository.BaseRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
abstract class GenericServiceImpl<T extends Node, D extends DTONode> extends BasicGenericServiceImpl<T>
        implements GenericService<T, D> {

    private final Class<T> typeParameterClass;

    protected final ObjectMapper objectMapper;

    GenericServiceImpl(BaseRepository<T> repository, ObjectMapper objectMapper) {
        super(repository);
        this.typeParameterClass = (Class<T>) GenericTypeResolver.resolveTypeArguments(getClass(), GenericServiceImpl.class)[0];
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<T> findById(Long id, int depth) {

        return id != null ? repository.findById(id, depth) : Optional.empty();
    }

    @Override
    public T saveNew(D dto) {

        T node = objectMapper.map(dto, typeParameterClass);

        return repository.save(node);
    }

    @Override
    public T update(T node, D dto) {

        Long id = node.getId();

        node = objectMapper.map(dto, typeParameterClass);

        node.setId(id);

        return repository.save(node);
    }

    @Override
    public Optional<T> delete(Long id) {

        Optional<T> nodeOptional = findById(id);

        nodeOptional.ifPresent(repository::delete);

        return nodeOptional;
    }
}
