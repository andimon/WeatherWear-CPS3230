package org.weatherwear.clients.Models;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class Location {
    private final @NotNull @NotBlank String latitude;
    private final @NotNull @NotBlank String longitude;

    public Location(@NotNull @NotBlank String latitude, @NotNull @NotBlank String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

}