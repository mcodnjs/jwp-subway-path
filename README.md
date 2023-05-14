# jwp-subway-path

## API 기능 요구 사항
- [ ] 노선에 역 등록 API 신규 구현
- [ ] 노선에 역 제거 API 신규 구현
- [ ] 노선 조회 API 수정
  - 노선에 포함된 역을 순서대로 보여주도록 응답 개선
- [ ] 노선 목록 조회 API 수정
  - 노선에 포함된 역을 순서대로 보여주도록 응답 개선

## API 명세

> section
- 노선에 역 등록 API: POST /lines/{lineId}/stations/{stationId}
  - BODY: upStationId, distance
- 노선에 역 제거 API: DELETE /lines/{lineId}/stations/{stationId}
- 노선 조회 API: GET /lines/{lineId}/stations
- 모든 노선 조회 API: GET /lines/stations

## 비즈니스 규칙

- 노선에 역 등록
  - 등록 방식
    - 노선에 이미 등록되어있는 역을 기준으로 새로운 역을 등록합니다.
    - 기준이 되는 역이 없다면 등록할 수 없습니다.
    - 종점으로 등록 할 수 있다.
    - 노선에 역을 등록할 때, 거리 정보도 포함되어야 한다.
    - 노선에 등록된 역이 없는 경우, 두 역을 동시에 등록해야 한다.
    - 하나의 역은 여러 노선에 등록이 될 수 있다.
  - 갈래길 방지
    - 하나의 노선은 일직선으로 연결되야 합니다.
    - 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 등록할 수 없습니다.
  - 거리 정보 관리
    - 거리는 양의 정수여야 한다.
    - 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.

- 노선에서 역을 제거
  - 종점 제거
  - 재배치
    - 노선에 A - B - C 역이 연결되어 있을 때 B역을 제거할 경우 A - C로 재배치합니다.
    - A - C의 거리는 A - B, B - C 의 거리 합으로 정합니다.
  - 노선에 두 개 역
    - 노선에 등록된 역이 2개 인 경우 하나의 역을 제거할 때 두 역이 모두 제거되어야 합니다.

## 도메인 
- Station
  - 역 이름

- Line
  - 노선 이름
  - 노선 색

- Section
  - 노선
  - 상행역
  - 하행역
  - 거리

- Distance

## 엔티티
- Station
  - id
  - name

- Line
  - id
  - name
  - color

- Section
  - id
  - line_id
  - up_station_id
  - down_station_id
  - distance
