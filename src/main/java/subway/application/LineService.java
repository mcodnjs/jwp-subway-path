package subway.application;

import org.springframework.stereotype.Service;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.*;
import subway.dto.*;
import subway.entity.LineEntity;
import subway.entity.SectionEntity;
import subway.entity.StationEntity;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;
    private final LineRepository lineRepository;

    public LineService(final LineDao lineDao, final SectionDao sectionDao, final StationDao stationDao, final LineRepository lineRepository) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
        this.lineRepository = lineRepository;
    }

    public Long save(LineRequest request) {
        LineEntity persistLine = lineDao.insert(new LineEntity(request.getName(), request.getColor()));
        return persistLine.getId();
    }

    public void saveStationInLine(final Long lineId, final LineStationRequest lineStationRequest) {
        LineEntity lineEntity = findLineById(lineId);

        validateDifferentStation(lineStationRequest);
        validateExistStation(lineStationRequest);

        Map<Long, Station> stationMap = makeStationMap(stationDao.findAll());
        Station upStation = stationMap.get(lineStationRequest.getUpStationId());
        Station downStation = stationMap.get(lineStationRequest.getDownStationId());

        List<Section> sections = lineRepository.findSectionsByLineIdWithSort(lineId);
        Line line = new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor(), new Sections(sections));
        Section section = new Section(upStation, downStation, lineStationRequest.getDistance());
        line.add(section);
        sync(lineId, line.getSections());
    }

    private void validateDifferentStation(final LineStationRequest lineStationRequest) {
        // 예외: 2개의 역이 동일한 역인 경우
        if (Objects.equals(lineStationRequest.getUpStationId(), lineStationRequest.getDownStationId())) {
            throw new IllegalArgumentException("구간은 서로 다른 역이여야 합니다.");
        }
    }

    private void validateExistStation(final LineStationRequest lineStationRequest) {
        // 예외: sectionRequest에 들어온 2개의 역이 모두 Station에 존재하는 역인지 검증
        stationDao.findById(lineStationRequest.getUpStationId())
                .orElseThrow(() -> new NoSuchElementException("해당하는 상행역이 존재하지 않습니다."));
        stationDao.findById(lineStationRequest.getDownStationId())
                .orElseThrow(() -> new NoSuchElementException("해당하는 하행역이 존재하지 않습니다."));
    }

    public List<LineEntity> findLines() {
        return lineDao.findAll();
    }

    public LineEntity findLineById(Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당하는 호선 ID가 없습니다."));
    }

    public List<LineStationResponse> findAll() {
        List<LineEntity> lineEntities = findLines();

        List<Line> lines = lineRepository.findLinesWithSort();
        Map<Long, List<Section>> lineMap = lines.stream()
                .collect(Collectors.toMap(Line::getId, Line::getSections));

        return lineEntities.stream()
                .map(lineEntity -> {
                    List<Section> sections = lineMap.getOrDefault(lineEntity.getId(), Collections.emptyList());
                    return LineStationResponse.from(
                            LineResponse.of(lineEntity),
                            toStationResponses(sections)
                    );
                }).collect(Collectors.toList());
    }

    public LineStationResponse findById(Long id) {
        LineEntity lineEntity = findLineById(id);
        List<Section> sections = lineRepository.findSectionsByLineIdWithSort(id);
        List<StationResponse> stationResponses = toStationResponses(sections);
        return LineStationResponse.from(LineResponse.of(lineEntity), stationResponses);
    }

    private List<StationResponse> toStationResponses(final List<Section> sections) {
        List<StationResponse> stationResponses = sections.stream()
                .map(station -> StationResponse.of(station.getUpStation()))
                .collect(Collectors.toList());
        if (stationResponses.size() == 0) {
            return Collections.emptyList();
        }
        stationResponses.add(StationResponse.of(sections.get(stationResponses.size() - 1).getDownStation()));
        return stationResponses;
    }

    public void update(Long id, LineRequest lineUpdateRequest) {
        lineDao.update(new LineEntity(id, lineUpdateRequest.getName(), lineUpdateRequest.getColor()));
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }

    public void deleteByLineIdAndStationId(final Long lineId, final Long stationId) {
        List<StationEntity> stationEntities = stationDao.findAll();
        Map<Long, Station> stationMap = makeStationMap(stationEntities);

        LineEntity lineEntity = findLineById(lineId);
        List<Section> sections = lineRepository.findSectionsByLineIdWithSort(lineId);

        Line line = new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor(), new Sections(sections));
        line.remove(stationMap.get(stationId));
        sync(lineId, line.getSections());
    }

    private void sync(final Long lineId, final List<Section> sections) {
        sectionDao.deleteByLineId(lineId);
        sectionDao.insertAll(
                sections.stream()
                        .map(section -> new SectionEntity(
                                section.getId(),
                                lineId,
                                section.getUpStation().getId(),
                                section.getDownStation().getId(),
                                section.getDistance(),
                                section.getOrder()
                        )).collect(Collectors.toList())
        );
    }

    private Map<Long, Station> makeStationMap(final List<StationEntity> stationDao) {
        return stationDao.stream()
                .collect(Collectors.toMap(
                        StationEntity::getId,
                        station -> new Station(station.getId(), station.getName())
                ));
    }
}
