package subway.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.entity.SectionEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    private final RowMapper<SectionEntity> rowMapper = (rs, rowNum) ->
            new SectionEntity(
                    rs.getLong("id"),
                    rs.getLong("line_id"),
                    rs.getObject("up_station_id", Long.class),
                    rs.getObject("down_station_id", Long.class),
                    rs.getInt("distance")
            );

    public SectionDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
    }

    public Long insert(final SectionEntity sectionEntity) {
        Map<String, Object> params = new HashMap<>();
        params.put("line_id", sectionEntity.getLineId());
        params.put("up_station_id", sectionEntity.getUpStationId());
        params.put("down_station_id", sectionEntity.getDownStationId());
        params.put("distance", sectionEntity.getDistance());
        return simpleJdbcInsert.executeAndReturnKey(params).longValue();
    }

    public void insertAll(final List<SectionEntity> sectionEntities) {
        final SqlParameterSource[] array = sectionEntities.stream()
                .map(BeanPropertySqlParameterSource::new)
                .collect(Collectors.toList())
                .toArray(new SqlParameterSource[sectionEntities.size()]);
        simpleJdbcInsert.executeBatch(array);
    }

    public List<SectionEntity> findByLineIdAndStationId(final Long lineId, final Long stationId) {
        String sql = "SELECT * FROM section WHERE line_id = ? AND (up_station_id = ? OR down_station_id = ?)";
        return jdbcTemplate.query(sql, rowMapper, lineId, stationId, stationId);
    }

    public List<SectionEntity> findByLineId(final Long lineId) {
        String sql = "SELECT * FROM section WHERE line_id = ?";
        return jdbcTemplate.query(sql, rowMapper, lineId);
    }

    public List<SectionEntity> findAll() {
        String sql = "SELECT * FROM section";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public void deleteByLineIdAndStationId(final Long lineId, final Long stationId) {
        String sql = "DELETE FROM section WHERE line_id = ? AND (up_station_id = ? OR down_station_id = ?)";
        jdbcTemplate.update(sql, lineId, stationId, stationId);
    }

    public void deleteBySectionId(final Long sectionId) {
        String sql = "DELETE FROM section WHERE id = ?";
        jdbcTemplate.update(sql, sectionId);
    }

    public Optional<Long> findByStationIds(final Long upStationId, final Long downStationId) {
        String sql = "SELECT id FROM section WHERE up_station_id = ? AND down_station_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, Long.class, upStationId, downStationId));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    public void deleteByLineId(final Long lineId) {
        String sql = "DELETE FROM section WHERE line_id = ?";
        jdbcTemplate.update(sql, lineId);
    }
}

