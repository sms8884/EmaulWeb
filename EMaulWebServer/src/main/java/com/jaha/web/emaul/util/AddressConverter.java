package com.jaha.web.emaul.util;

import com.jaha.web.emaul.model.Address;

/**
 * Created by doring on 15. 4. 16..
 */
public class AddressConverter {

    public static String toStringAddress(Address address) {
        String addr;
        if (address.법정리명 == null || address.법정리명.isEmpty()) {
            addr = String.format("%s %s", address.시도명, address.시군구명);
            if (address.도로명 != null && address.건물본번 != null) {
                addr += String.format(" %s %s", address.도로명, address.건물본번);
            }
            if (address.건물부번 != null && address.건물부번 != 0) {
                addr += ("-" + address.건물부번);
            }
            if ("1".equals(address.공동주택여부)) {
                addr += String.format(" (%s)", address.법정읍면동명);
            } else {
                addr += String.format(" (%s, %s)", address.법정읍면동명, address.시군구용건물명);
            }
        } else {
            addr = String.format("%s %s %s %s %s", address.시도명, address.시군구명, address.법정읍면동명, address.도로명, address.건물본번);
            if (address.건물부번 != null && address.건물부번 != 0) {
                addr += ("-" + address.건물부번);
            }
        }

        return addr;
    }

    /**
     * 옛날 지번 반환(구글지도로 위경도 좌표를 검색하기 위하여)
     *
     * @param address
     * @return
     */
    public static String toStringAddressOld(Address address) {
        String addr = String.format("%s %s %s", address.시도명, address.시군구명, address.법정읍면동명);

        if (address.법정리명 != null && !address.법정리명.isEmpty()) {
            addr += (" " + address.법정리명);
        }

        if (address.지번본번 != null && address.지번본번 != 0) {
            addr += (" " + address.지번본번);
        }

        if (address.지번부번 != null && address.지번부번 != 0) {
            addr += ("-" + address.지번부번);
        }

        return addr;
    }

}
