package vti.group10.football_booking.service.geo;

import java.util.Optional;

    public interface GeocodingService {
        record LatLng(double lat, double lng) {}
        Optional<LatLng> geocode(String fullAddress);
    }
