package org.weatherwear.clients.Models;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 *
 */
public final class Weather {
    private final @NotNull @NotBlank double tempInCelsius;
    private final @NotNull @NotBlank double precipitationProbability;

    public Weather(@NotNull @NotBlank double tempInCelsius, @NotNull @NotBlank double precipitationProbability) {
        this.tempInCelsius = tempInCelsius;
        this.precipitationProbability = precipitationProbability;
    }

    public @NotNull @NotBlank double getTempInCelsius() {
        return tempInCelsius;
    }

    public @NotNull @NotBlank double getPrecipitationProbability() {
        return precipitationProbability;
    }

}