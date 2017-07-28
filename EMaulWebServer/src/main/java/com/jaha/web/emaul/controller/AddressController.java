package com.jaha.web.emaul.controller;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jaha.web.emaul.model.Address;
import com.jaha.web.emaul.service.HouseService;
import com.jaha.web.emaul.util.AddressConverter;

/**
 * Created by doring on 15. 4. 14..
 */
@Controller
public class AddressController {

    @Autowired
    private HouseService houseService;

    @RequestMapping(value = "/api/public/address/sido-names", method = RequestMethod.GET)
    public @ResponseBody List<String> getSidoNames() {
        return houseService.getSidoNames();
    }

    @RequestMapping(value = "/api/public/address/sigungu-names/{sidoName}", method = RequestMethod.GET)
    public @ResponseBody List<String> getSigunguNames(@PathVariable(value = "sidoName") String sidoName) {
        return houseService.getSigunguNames(sidoName);
    }

    @RequestMapping(value = "/api/public/address/eupmyundong-names/{sigunguName}", method = RequestMethod.GET)
    public @ResponseBody List<String> getEupMyunDongNames(@PathVariable(value = "sigunguName") String sigunguName) {
        return houseService.getEupMyunDongNames(sigunguName);
    }

    @RequestMapping(value = "/api/public/address/eupmyundong-names/{sidoName}/{sigunguName}", method = RequestMethod.GET)
    public @ResponseBody List<String> getEupMyunDongNames2(@PathVariable(value = "sidoName") String sidoName, @PathVariable(value = "sigunguName") String sigunguName) {
        return houseService.getEupMyunDongNames(sidoName, sigunguName);
    }

    @RequestMapping(value = "/api/public/address/search-building", method = RequestMethod.GET)
    public @ResponseBody String searchBuilding(@RequestParam(value = "sidoName") String sidoName, @RequestParam(value = "sigunguName") String sigunguName, @RequestParam(value = "eupmyundongName",
            required = false) String eupmyundongName, @RequestParam(value = "buildingName") String buildingName) {
        List<Address> addressList = houseService.searchBuilding(sidoName, sigunguName, eupmyundongName, buildingName);

        JSONArray jsonArray = new JSONArray();
        for (Address address : addressList) {
            JSONObject obj = new JSONObject();
            obj.put("address", AddressConverter.toStringAddress(address));
            obj.put("addressOld", AddressConverter.toStringAddressOld(address));
            obj.put("name", address.시군구용건물명);
            obj.put("code", address.건물관리번호);
            jsonArray.put(obj);
        }

        return jsonArray.toString();
    }
}
