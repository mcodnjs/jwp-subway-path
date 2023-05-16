package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class SectionTest {

    @DisplayName("Section이 정상적으로 생성된다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10})
    void Section(int validDistance) {
        // given
        Station 강남역 = new Station(1L, "강남역");
        Station 선릉역 = new Station(2L, "선릉역");

        // when & then
        assertDoesNotThrow(() -> new Section(null, 강남역, 선릉역, validDistance));
    }

    @DisplayName("유효하지 않은 거리로 Section 생성 시, IllegalArgumentException이 발생한다.")
    @ParameterizedTest
    @ValueSource(ints = {-100, -1})
    void Section_DistanceValidationFail(int invalidDistance) {
        // given
        Station 강남역 = new Station(1L, "강남역");
        Station 선릉역 = new Station(2L, "선릉역");

        // when & then
        assertThatThrownBy(() -> new Section(null, 강남역, 선릉역, invalidDistance))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("특정 Station을 포함하고 있는지에 따라 Boolean 값을 반환한다.")
    @Test
    void containStation() {
        // given
        Station 강남역 = new Station(1L, "강남역");
        Station 선릉역 = new Station(2L, "선릉역");
        Station 잠실역 = new Station(3L, "잠실역");

        Section 상행종점_강남역 = new Section(Station.empty(), Station.empty(), 0);
        Section 강남역_선릉역 = new Section(강남역, 선릉역, 5);
        Section 선릉역_하행종점 = new Section(선릉역, Station.empty(), 0);

        // when & then
        assertAll("containStation",
                () -> assertFalse(강남역_선릉역.containStation(잠실역)),
                () -> assertTrue(강남역_선릉역.containStation(강남역)),
                () -> assertTrue(강남역_선릉역.containStation(선릉역))
        );
    }

    @DisplayName("해당 Section이 상행종점인지에 따라 Boolean 값을 반환한다.")
    @Test
    void isUpFinalStation() {
        // given
        Station 강남역 = new Station(1L, "강남역");
        Station 선릉역 = new Station(2L, "선릉역");

        Section 상행종점_강남역 = new Section(Station.empty(), Station.empty(), 0);
        Section 강남역_선릉역 = new Section(강남역, 선릉역, 5);
        Section 선릉역_하행종점 = new Section(선릉역, Station.empty(), 0);

        // when & then
        assertAll("isUpFinalStation",
                () -> assertTrue(상행종점_강남역.isUpFinalStation()),
                () -> assertFalse(강남역_선릉역.isUpFinalStation()),
                () -> assertFalse(선릉역_하행종점.isUpFinalStation())
        );
    }
}
