package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.node.RegionNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.RegionRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.RegionService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.RegionBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("RegionServiceImpl_Tests")
class RegionServiceImplTest {

    private RegionService regionService;

    @Mock
    private RegionRepository regionRepository;

    private static RegionBuilder regionBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        regionBuilder = new RegionBuilder();
    }

    @BeforeEach
    private void setUp() {

        regionService = new RegionServiceImpl(regionRepository);
    }

    @Test
    void when_find_existing_region_by_name_should_return_region() {

        String regionName = "region";

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.withName(regionName).build(ObjectType.NODE);

        when(regionRepository.findByName(regionName)).thenReturn(Optional.of(regionNodeExpected));

        Optional<RegionNode> regionNodeActualOptional = regionService.findByName(regionName);

        RegionNode regionNodeActual = regionNodeActualOptional.get();

        assertAll(() -> assertEquals(regionNodeExpected.getId(), regionNodeActual.getId(),
                () -> "should return region node with id: " + regionNodeExpected.getId()
                        + ", but was: " + regionNodeActual.getId()),
                () -> assertEquals(regionNodeExpected.getName(), regionNodeActual.getName(),
                        () -> "should return region node with name: " + regionNodeExpected.getName()
                                + ", but was: " + regionNodeActual.getName()),
                () -> verify(regionRepository, times(1)).findByName(regionName),
                () -> verifyNoMoreInteractions(regionRepository));
    }

    @Test
    void when_target_not_exists_and_return_one_target_should_return_empty_optional() {

        String regionName = "region";

        when(regionRepository.findByName(regionName)).thenReturn(Optional.empty());

        Optional<RegionNode> regionNodeActualOptional = regionService.findByName(regionName);

        assertAll(() -> assertTrue(regionNodeActualOptional.isEmpty(), () -> "should return empty optional"),
                () -> verify(regionRepository, times(1)).findByName(regionName),
                () -> verifyNoMoreInteractions(regionRepository));
    }

    @Test
    void when_check_by_name_if_existing_region_exists_should_return_true() {

        String regionName = "region";

        when(regionRepository.existsByName(regionName)).thenReturn(true);

        boolean isRegionExisting = regionService.existsByName(regionName);

        assertAll(() -> assertTrue(isRegionExisting, () -> "should return true, but was: false"),
                () -> verify(regionRepository, times(1)).existsByName(regionName),
                () -> verifyNoMoreInteractions(regionRepository));
    }

    @Test
    void when_check_by_name_if_not_existing_region_exists_should_return_true() {

        String notExistingRegionName = "not existing region";

        when(regionRepository.existsByName(notExistingRegionName)).thenReturn(false);

        boolean isRegionExisting = regionService.existsByName(notExistingRegionName);

        assertAll(() -> assertFalse(isRegionExisting, () -> "should return false, but was: true"),
                () -> verify(regionRepository, times(1)).existsByName(notExistingRegionName),
                () -> verifyNoMoreInteractions(regionRepository));
    }

    @Test
    void when_save_region_should_return_saved_region() {

        RegionNode regionNodeExpectedBeforeSave = (RegionNode) regionBuilder.withId(null).build(ObjectType.NODE);
        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);

        when(regionRepository.save(regionNodeExpectedBeforeSave)).thenReturn(regionNodeExpected);

        RegionNode regionNodeActual = regionService.save(regionNodeExpected);

        assertAll(() -> assertEquals(regionNodeExpected.getId(), regionNodeActual.getId(),
                () -> "should return region node with id: " + regionNodeExpected.getId()
                        + ", but was: " + regionNodeActual.getId()),
                () -> assertEquals(regionNodeExpected.getName(), regionNodeActual.getName(),
                        () -> "should return region node with name: " + regionNodeExpected.getName()
                                + ", but was: " + regionNodeActual.getName()),
                () -> verify(regionRepository, times(1)).save(regionNodeExpectedBeforeSave),
                () -> verifyNoMoreInteractions(regionRepository));
    }
}