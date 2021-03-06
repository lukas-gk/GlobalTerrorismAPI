package com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder;

import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Country;
import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Region;
import com.NowakArtur97.GlobalTerrorismAPI.dto.CountryDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.CountryModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.RegionModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.CountryNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.RegionNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;

public final class CountryBuilder {

    private Long id = 1L;

    private String name = "country";

    private Region region = null;

    public CountryBuilder withId(Long id) {

        this.id = id;

        return this;
    }

    public CountryBuilder withName(String name) {

        this.name = name;

        return this;
    }

    public CountryBuilder withRegion(Region region) {

        this.region = region;

        return this;
    }

    public Country build(ObjectType type) {

        Country country;

        switch (type) {

            case DTO:

                country = new CountryDTO(name);

                break;

            case NODE:

                country = new CountryNode(id, name, (RegionNode) region);

                break;

            case MODEL:

                country = new CountryModel(id, name, (RegionModel) region);

                break;

            default:
                throw new RuntimeException("The specified type does not exist");
        }

        resetProperties();

        return country;
    }

    private void resetProperties() {

        this.id = 1L;

        this.name = "country";

        this.region = null;
    }
}
