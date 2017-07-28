package com.jaha.web.emaul.model;

import org.hibernate.validator.constraints.NotBlank;

/**
 * @author 이현수(lhs@jahasmart.com), 2016. 7. 6.
 * @description 무인택배시스템
 */
public class ParcelLockerForm {

    public static class Search {

        private SearchType searchType;
        private String searchKeyword;

        public SearchType getSearchType() {
            return searchType;
        }

        public void setSearchType(SearchType searchType) {
            this.searchType = searchType;
        }

        public String getSearchKeyword() {
            return searchKeyword;
        }

        public void setSearchKeyword(String searchKeyword) {
            this.searchKeyword = searchKeyword;
        }
    }

    public static class Update {

        @NotBlank
        private String uuid;

        @NotBlank
        private String name;

        @NotBlank
        private String authKey;

        private String location;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAuthKey() {
            return authKey;
        }

        public void setAuthKey(String authKey) {
            this.authKey = authKey;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }

    public static class Create {

        @NotBlank
        private String name;

        private String location;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }

    public enum SearchType {

        LOCKER_NAME("1", "택배함이름"), LOCKER_UUID("2", "택배함 ID");

        private String code;
        private String desc;

        SearchType(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public String getCode() {
            return this.code;
        }

        public String getDesc() {
            return desc;
        }

        public static SearchType value(String code){
            switch (code){
                case "1" :
                    return LOCKER_NAME;
                case "2" :
                    return LOCKER_UUID;
                default :
                    return LOCKER_NAME;
            }
        }
    }
}
