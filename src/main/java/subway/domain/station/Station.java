package subway.domain.station;

public class Station {

    private final StationName stationName;

    public Station(final StationName stationName) {
        this.stationName = stationName;
    }

    public String getStationName() {
        return stationName.getName();
    }
}