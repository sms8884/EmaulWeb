package com.jaha.web.emaul.util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Created by doring on 15. 5. 1..
 */
public class Locations {

    private final static Logger LOGGER = LoggerFactory.getLogger(Locations.class);

    public static class LatLng {
        public LatLng(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }

        public double lat;
        public double lng;
    }

    /**
     * 주소로 위경도 좌표 가져오기
     *
     * @param addressOld
     * @return
     */
    public static LatLng getLocationFromAddress(String addressOld) {
        RestTemplate restTemplate = new RestTemplate();

        // address 인코딩 하면 안됨. rest template 이 함.
        String resp = restTemplate.getForObject("http://maps.googleapis.com/maps/api/geocode/json?sensor=false&address=" + addressOld, String.class);
        LOGGER.debug("<<위경도좌표찾기, 주소>> {}", addressOld);
        LOGGER.debug("<<위경도좌표찾기, 구글맵 조회 결과>> {}", resp);

        if (resp == null || resp.isEmpty()) {
            return null;
        }

        JSONObject jsonObject = new JSONObject(resp);

        if (jsonObject.optString("status") == null || !"OK".equalsIgnoreCase(jsonObject.optString("status"))) {
            return null;
        }

        try {
            double lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
            double lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");

            return new LatLng(lat, lng);
        } catch (Exception e) {
            LOGGER.error("<<JSON 데이타에서 위경도 좌표를 가져오는 중 오류 발생>>", e.getMessage());
        }

        return null;
    }

    // public static void main(String[] args) {
    // Locations.getLocationFromAddress("서울시 동작구 상도동 535");
    // }

}
