package org.weatherwear.clients.LocationClient;

import org.weatherwear.clients.Models.Location;

public interface ILocationClient {
    Location getLocation() throws Exception;
    Location getLocation(String IATA) throws Exception;

    Location getLocationBackup() throws Exception;

    Location getLocationBackup(String IATA) throws Exception;

}
