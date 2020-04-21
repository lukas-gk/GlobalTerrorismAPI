package com.NowakArtur97.GlobalTerrorismAPI.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.mapper.DTOMapper;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.TargetRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.TargetService;
import com.NowakArtur97.GlobalTerrorismAPI.service.impl.TargetServiceImpl;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("TargetServiceImpl_Tests")
class TargetServiceImplTest {

	private TargetService targetService;

	@Mock
	private TargetRepository targetRepository;

	@Mock
	private DTOMapper dtoMapper;

	@BeforeEach
	private void setUp() {

		targetService = new TargetServiceImpl(targetRepository, dtoMapper);
	}

	@Test
	void when_targets_exist_and_return_all_targets_should_return_targets() {

		List<TargetNode> targetsListExpected = new ArrayList<>();

		TargetNode target1 = new TargetNode("target1");
		TargetNode target2 = new TargetNode("target2");
		TargetNode target3 = new TargetNode("target3");

		targetsListExpected.add(target1);
		targetsListExpected.add(target2);
		targetsListExpected.add(target3);

		Page<TargetNode> targetsExpected = new PageImpl<>(targetsListExpected);

		Pageable pageable = PageRequest.of(0, 100);

		when(targetRepository.findAll(pageable)).thenReturn(targetsExpected);

		Page<TargetNode> targetsActual = targetService.findAll(pageable);

		assertAll(() -> assertNotNull(targetsActual, () -> "shouldn`t return null"),
				() -> assertEquals(targetsListExpected, targetsActual.getContent(),
						() -> "should contain: " + targetsListExpected + ", but was: " + targetsActual.getContent()),
				() -> assertEquals(targetsExpected.getNumberOfElements(), targetsActual.getNumberOfElements(),
						() -> "should return page with: " + targetsExpected.getNumberOfElements()
								+ " elements, but was: " + targetsActual.getNumberOfElements()),
				() -> verify(targetRepository, times(1)).findAll(pageable),
				() -> verifyNoMoreInteractions(targetRepository), () -> verifyNoInteractions(dtoMapper));
	}

	@Test
	void when_targets_not_exist_and_return_all_targets_should_not_return_any_targets() {

		List<TargetNode> targetsListExpected = new ArrayList<>();

		Page<TargetNode> targetsExpected = new PageImpl<>(targetsListExpected);

		Pageable pageable = PageRequest.of(0, 100);

		when(targetRepository.findAll(pageable)).thenReturn(targetsExpected);

		Page<TargetNode> targetsActual = targetService.findAll(pageable);

		assertAll(() -> assertNotNull(targetsActual, () -> "shouldn`t return null"),
				() -> assertEquals(targetsListExpected, targetsActual.getContent(),
						() -> "should contain empty list, but was: " + targetsActual.getContent()),
				() -> assertEquals(targetsListExpected, targetsActual.getContent(),
						() -> "should contain: " + targetsListExpected + ", but was: " + targetsActual.getContent()),
				() -> assertEquals(targetsExpected.getNumberOfElements(), targetsActual.getNumberOfElements(),
						() -> "should return empty page, but was: " + targetsActual.getNumberOfElements()),
				() -> verify(targetRepository, times(1)).findAll(pageable),
				() -> verifyNoMoreInteractions(targetRepository), () -> verifyNoInteractions(dtoMapper));
	}

	@Test
	void when_targets_exists_and_return_one_target_should_return_one_target() {

		Long expectedTargetId = 1L;

		TargetNode targetExpected = new TargetNode("target");

		when(targetRepository.findById(expectedTargetId)).thenReturn(Optional.of(targetExpected));

		Optional<TargetNode> targetActualOptional = targetService.findById(expectedTargetId);

		TargetNode targetActual = targetActualOptional.get();

		assertAll(() -> assertTrue(targetActualOptional.isPresent(), () -> "shouldn`t return empty optional"),
				() -> assertEquals(targetExpected.getId(), targetActual.getId(),
						() -> "should return target with id: " + expectedTargetId + ", but was" + targetActual.getId()),
				() -> assertEquals(targetExpected.getTarget(), targetActual.getTarget(),
						() -> "should return target with target: " + targetExpected.getTarget() + ", but was"
								+ targetActual.getTarget()),
				() -> verify(targetRepository, times(1)).findById(expectedTargetId),
				() -> verifyNoMoreInteractions(dtoMapper), () -> verifyNoInteractions(dtoMapper));
	}

	@Test
	void when_targets_not_exists_and_return_one_target_should_return_empty_optional() {

		Long expectedTargetId = 1L;

		when(targetRepository.findById(expectedTargetId)).thenReturn(Optional.empty());

		Optional<TargetNode> targetActualOptional = targetService.findById(expectedTargetId);

		assertAll(() -> assertTrue(targetActualOptional.isEmpty(), () -> "should return empty optional"),
				() -> verify(targetRepository, times(1)).findById(expectedTargetId),
				() -> verifyNoMoreInteractions(targetRepository), () -> verifyNoInteractions(dtoMapper));
	}

	@Test
	void when_save_new_target_should_save_target() {

		Long targetId = 1L;

		String targetName = "Target";

		TargetDTO targetDTOExpected = new TargetDTO(targetName);

		TargetNode targetNodeExpectedBeforeSave = new TargetNode(null, targetName);
		TargetNode targetNodeExpected = new TargetNode(targetId, targetName);

		when(dtoMapper.mapToNode(targetDTOExpected, TargetNode.class)).thenReturn(targetNodeExpectedBeforeSave);
		when(targetRepository.save(targetNodeExpectedBeforeSave)).thenReturn(targetNodeExpected);

		TargetNode targetNodeActual = targetService.saveOrUpdate(null, targetDTOExpected);

		assertAll(
				() -> assertEquals(targetNodeExpected.getTarget(), targetNodeActual.getTarget(),
						() -> "should return target node with target: " + targetNodeExpected.getTarget() + ", but was: "
								+ targetNodeActual.getTarget()),
				() -> assertNotNull(targetNodeActual.getId(),
						() -> "should return target node with new id, but was: " + targetNodeActual.getId()),
				() -> verify(dtoMapper, times(1)).mapToNode(targetDTOExpected, TargetNode.class),
				() -> verifyNoMoreInteractions(dtoMapper),
				() -> verify(targetRepository, times(1)).save(targetNodeExpectedBeforeSave),
				() -> verifyNoMoreInteractions(targetRepository));
	}

	@Test
	void when_persist_update_new_target_should_update_target() {

		Long targetId = 1L;

		String targetName = "Target";

		TargetNode targetNodeExpectedBeforeSave = new TargetNode(null, targetName);
		TargetNode targetNodeExpected = new TargetNode(targetId, targetName);

		when(targetRepository.save(targetNodeExpectedBeforeSave)).thenReturn(targetNodeExpected);

		TargetNode targetNodeActual = targetService.persistUpdate(targetNodeExpectedBeforeSave);

		assertAll(
				() -> assertEquals(targetNodeExpected.getTarget(), targetNodeActual.getTarget(),
						() -> "should return target node with target: " + targetNodeExpected.getTarget() + ", but was: "
								+ targetNodeActual.getTarget()),
				() -> assertNotNull(targetNodeActual.getId(),
						() -> "should return target node with new id, but was: " + targetNodeActual.getId()),
				() -> verify(targetRepository, times(1)).save(targetNodeExpectedBeforeSave),
				() -> verifyNoMoreInteractions(targetRepository), () -> verifyNoInteractions(dtoMapper));
	}

	@Test
	void when_delete_target_by_id_target_should_delete_and_return_target() {

		String targetName = "Target";

		Long targetId = 1L;

		TargetNode targetNodeExpected = new TargetNode(targetId, targetName);

		when(targetRepository.findById(targetId)).thenReturn(Optional.of(targetNodeExpected));

		Optional<TargetNode> targetNodeOptional = targetService.delete(targetId);

		TargetNode targetNodeActual = targetNodeOptional.get();

		assertAll(
				() -> assertEquals(targetNodeExpected.getTarget(), targetNodeActual.getTarget(),
						() -> "should return target node with target: " + targetNodeExpected.getTarget() + ", but was: "
								+ targetNodeActual.getTarget()),
				() -> verify(targetRepository, times(1)).findById(targetId),
				() -> verify(targetRepository, times(1)).delete(targetNodeActual),
				() -> verifyNoMoreInteractions(targetRepository), () -> verifyNoInteractions(dtoMapper));
	}

	@Test
	void when_delete_target_by_id_not_existing_target_should_return_empty_optional() {

		Long targetId = 1L;

		when(targetRepository.findById(targetId)).thenReturn(Optional.empty());

		Optional<TargetNode> targetNodeOptional = targetService.delete(targetId);

		assertAll(
				() -> assertTrue(targetNodeOptional.isEmpty(),
						() -> "should return empty target node optional, but was: " + targetNodeOptional.get()),
				() -> verify(targetRepository, times(1)).findById(targetId),
				() -> verifyNoMoreInteractions(targetRepository), () -> verifyNoInteractions(dtoMapper));
	}

	@Test
	void when_checking_if_database_is_empty_and_it_is_empty_should_return_true() {

		Long databaseSize = 0L;

		when(targetRepository.count()).thenReturn(databaseSize);

		boolean isDatabaseEmpty = targetService.isDatabaseEmpty();

		assertAll(() -> assertTrue(isDatabaseEmpty, () -> "should database be empty, but that was: " + isDatabaseEmpty),
				() -> verify(targetRepository, times(1)).count(), () -> verifyNoMoreInteractions(targetRepository),
				() -> verifyNoInteractions(dtoMapper));
	}

	@Test
	void when_checking_if_database_is_empty_and_it_is_not_empty_should_return_false() {

		Long databaseSize = 10L;

		when(targetRepository.count()).thenReturn(databaseSize);

		boolean isDatabaseEmpty = targetService.isDatabaseEmpty();

		assertAll(
				() -> assertFalse(isDatabaseEmpty,
						() -> "should not database be empty, but that was: " + isDatabaseEmpty),
				() -> verify(targetRepository, times(1)).count(), () -> verifyNoMoreInteractions(targetRepository),
				() -> verifyNoInteractions(dtoMapper));
	}
}
