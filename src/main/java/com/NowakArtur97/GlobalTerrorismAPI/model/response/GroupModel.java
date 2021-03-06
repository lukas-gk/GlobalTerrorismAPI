package com.NowakArtur97.GlobalTerrorismAPI.model.response;

import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Group;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@ApiModel(description = "Details about the Target")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
public class GroupModel extends RepresentationModel<GroupModel> implements Group {

    @ApiModelProperty(notes = "The unique id of the Group")
    private Long id;

    @ApiModelProperty(notes = "The group's name")
    private String name;

    @ApiModelProperty(notes = "The event's caused by the group")
    private List<EventModel> eventsCaused;

    public void addEvent(EventModel eventModel) {

        eventsCaused.add(eventModel);
    }
}
