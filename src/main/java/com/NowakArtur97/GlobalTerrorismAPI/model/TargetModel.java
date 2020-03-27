package com.NowakArtur97.GlobalTerrorismAPI.model;

import org.springframework.hateoas.RepresentationModel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@ApiModel(description = "Details about the Target")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
public class TargetModel extends RepresentationModel<TargetModel> {

	@ApiModelProperty(notes = "The unique id of the Target")
	private Long id;

	@ApiModelProperty(notes = "The target's name")
	private String target;
}
