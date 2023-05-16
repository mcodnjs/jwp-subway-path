package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class LineTest {

    @DisplayName("Line이 정상적으로 생성된다.")
    @ParameterizedTest
    @CsvSource({
            "신분당선, bg-red-500",
            "유효한이름, bg-validColor-300"
    })
    void Line(String validName, String validColor) {
        // given
        Section section = new Section(Station.empty(), Station.empty(), 0);
        Sections sections = new Sections(List.of(section));

        // when & then
        assertDoesNotThrow(() -> new Line(null, validName, validColor, sections));
    }

    @DisplayName("유효하지 않은 이름으로 Line 생성 시, IllegalArgumentException이 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "5자이하만가능합니다."})
    void Line_NameValidationFail(String invalidName) {
        // given
        Section section = new Section(Station.empty(), Station.empty(), 0);
        Sections sections = new Sections(List.of(section));

        // when & then
        assertThatThrownBy(() -> new Line(null, invalidName, "bg-red-500", sections))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("유효하지 않은 색으로 Line 생성 시, IllegalArgumentException이 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "bg--", "bg-20자이상은불가능합니다-10000"})
    void Line_ColorValidationFail(String invalidColor) {
        // given
        Section section = new Section(Station.empty(), Station.empty(), 0);
        Sections sections = new Sections(List.of(section));

        // when & then
        assertThatThrownBy(() -> new Line(null, "신분당선", invalidColor, sections))
                .isInstanceOf(IllegalArgumentException.class);
    }
    
    @DisplayName("Line에 Session을 추가한다.")
    @Test
    void add() {
        // given
        Station 강남역 = new Station(1L, "강남역");
        Station 선릉역 = new Station(2L, "선릉역");
        Station 잠실역 = new Station(3L, "잠실역");

        Section 상행종점_강남역 = new Section(Station.empty(), 강남역, 0);
        Section 강남역_선릉역 = new Section(강남역, 선릉역, 5);
        Section 선릉역_하행종점 = new Section(선릉역, Station.empty(), 0);

        Sections sections = new Sections(new ArrayList<>(List.of(상행종점_강남역, 강남역_선릉역, 선릉역_하행종점)));
        Line 호선2 = new Line(null, "2호선", "bg-green-500", sections);

        // when
        Section 선릉역_잠실역 = new Section(선릉역, 잠실역, 3);
        Section 잠실역_하행종점 = new Section(잠실역, Station.empty(), 0);
        호선2.add(선릉역_잠실역);
        
        // then
        assertThat(호선2.getSections())
                .containsAll(List.of(상행종점_강남역, 강남역_선릉역, 선릉역_잠실역, 잠실역_하행종점));
    }

    @DisplayName("Line에 있는 Session을 삭제한다.")
    @Test
    void remove() {
        // given
        Station 강남역 = new Station(1L, "강남역");
        Station 선릉역 = new Station(2L, "선릉역");

        Section 상행종점_강남역 = new Section(Station.empty(), 강남역, 0);
        Section 강남역_선릉역 = new Section(강남역, 선릉역, 5);
        Section 선릉역_하행종점 = new Section(선릉역, Station.empty(), 0);

        Sections sections = new Sections(new ArrayList<>(List.of(상행종점_강남역, 강남역_선릉역, 선릉역_하행종점)));
        Line 호선2 = new Line(null, "2호선", "bg-green-500", sections);

        // when
        Sections deleteSections = new Sections(new ArrayList<>(List.of(강남역_선릉역, 선릉역_하행종점)));
        호선2.remove(deleteSections, 선릉역);

        // then
        assertThat(호선2.getSections()).isEmpty();
    }
}