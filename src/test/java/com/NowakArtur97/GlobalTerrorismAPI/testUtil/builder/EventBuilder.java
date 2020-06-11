package com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder;

import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Event;
import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Target;
import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.ibm.icu.util.Calendar;

import java.util.Date;

public final class EventBuilder {

    private Long id = 1L;

    private String summary = "summary";

    private String motive = "motive";

    private Date date = Calendar.getInstance().getTime();

    private Boolean isPartOfMultipleIncidents = true;

    private Boolean isSuccessful = true;

    private Boolean isSuicide = true;

    private Target target = null;

    public EventBuilder withId(Long id) {

        this.id = id;

        return this;
    }

    public EventBuilder withSummary(String summary) {

        this.summary = summary;

        return this;
    }

    public EventBuilder withMotive(String motive) {

        this.motive = motive;

        return this;
    }

    public EventBuilder withDate(Date date) {

        this.date = date;

        return this;
    }

    public EventBuilder withIsPartOfMultipleIncidents(Boolean isPartOfMultipleIncidents) {

        this.isPartOfMultipleIncidents = isPartOfMultipleIncidents;

        return this;
    }

    public EventBuilder withIsSuccessful(Boolean isSuccessful) {

        this.isSuccessful = isSuccessful;

        return this;
    }

    public EventBuilder withIsSuicide(Boolean isSuicide) {

        this.isSuicide = isSuicide;

        return this;
    }

    public EventBuilder withTarget(Target target) {

        this.target = target;

        return this;
    }

    public Event build(ObjectType type) {

        Event event;

        switch (type) {

            case DTO:
                event = EventDTO.builder().summary(summary).motive(motive).date(date)
                        .isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
                        .isSuicide(isSuicide).target((TargetDTO) target).build();
                break;

            case NODE:
                event = EventNode.builder().id(id).summary(summary).motive(motive).date(date)
                        .isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
                        .isSuicide(isSuicide).target((TargetNode) target).build();
                break;

            case MODEL:

                event= EventModel.builder().id(id).summary(summary).motive(motive).date(date)
                        .isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
                        .isSuicide(isSuicide).target((TargetModel) target).build();
                break;

            default:
                throw new RuntimeException("The specified type does not exist");
        }

        resetProperties();

        return event;
    }

    private void resetProperties() {

        this.id = 1L;

        this.summary = "summary";

        this.motive = "motive";

        this.date = Calendar.getInstance().getTime();

        this.isPartOfMultipleIncidents = true;

        this.isSuccessful = true;

        this.isSuicide = true;

        this.target = null;
    }
}
