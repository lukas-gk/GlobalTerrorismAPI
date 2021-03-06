package com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder;

import com.NowakArtur97.GlobalTerrorismAPI.baseModel.City;
import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Event;
import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Target;
import com.NowakArtur97.GlobalTerrorismAPI.dto.CityDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.CityModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.EventModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.CityNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.ibm.icu.util.Calendar;

import java.util.Date;

public final class EventBuilder {

    private final static Date startDate = Calendar.getInstance().getTime();

    private Long id = 1L;

    private String summary = "summary";

    private String motive = "motive";

    private Date date = startDate;

    private Boolean isPartOfMultipleIncidents = true;

    private Boolean isSuccessful = true;

    private Boolean isSuicidal = true;

    private Target target = null;

    private City city = null;

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

    public EventBuilder withIsSuicidal(Boolean isSuicidal) {

        this.isSuicidal = isSuicidal;

        return this;
    }

    public EventBuilder withTarget(Target target) {

        this.target = target;

        return this;
    }

    public EventBuilder withCity(City city) {

        this.city = city;

        return this;
    }

    public Event build(ObjectType type) {

        Event event;

        switch (type) {

            case DTO:
                event = EventDTO.builder().summary(summary).motive(motive).date(date)
                        .isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
                        .isSuicidal(isSuicidal).target((TargetDTO) target).city((CityDTO) city).build();
                break;

            case NODE:
                event = EventNode.builder().id(id).summary(summary).motive(motive).date(date)
                        .isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
                        .isSuicidal(isSuicidal).target((TargetNode) target).city((CityNode) city).build();
                break;

            case MODEL:

                event = EventModel.builder().id(id).summary(summary).motive(motive).date(date)
                        .isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
                        .isSuicidal(isSuicidal).target((TargetModel) target).city((CityModel) city).build();
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

        this.date = startDate;

        this.isPartOfMultipleIncidents = true;

        this.isSuccessful = true;

        this.isSuicidal = true;

        this.target = null;

        this.city = null;
    }
}
