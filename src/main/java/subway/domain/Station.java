package subway.domain;

import java.util.Objects;

public class Station {

    private final Long id;
    private final String name;

    public Station() {
        this.id = null;
        this.name = null;
    }

    public Station(final Long id, final String name) {
        validateName(name);
        this.id = id;
        this.name = name;
    }

    private void validateName(final String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("역 이름은 공백을 입력할 수 없습니다.");
        }

        if (name.length() < 1 || name.length() > 15) {
            throw new IllegalArgumentException("역 이름은 1자 이상 15자 이하만 가능합니다.");
        }
    }

    public static Station empty() {
        return new Station();
    }

    public boolean isEmpty() {
        return id == null && name.equals("");
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(id, station.id) && Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
