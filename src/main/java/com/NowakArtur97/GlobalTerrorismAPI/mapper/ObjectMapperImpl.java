package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ObjectMapperImpl implements ObjectMapper {

    private final ModelMapper modelMapper;

    @Override
    public <T> T map(Object source, Class<T> destinationType) {

        return modelMapper.map(source, destinationType);
    }
}
