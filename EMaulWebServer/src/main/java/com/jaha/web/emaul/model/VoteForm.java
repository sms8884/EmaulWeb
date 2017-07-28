package com.jaha.web.emaul.model;

import java.util.Date;

public class VoteForm {

    public static class Search {

        private String status;
        private String type;
        private Date startDate;
        private Date endDate;
        private String title;
        private String targetAptName;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Date getStartDate() {
            return startDate;
        }

        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }

        public Date getEndDate() {
            return endDate;
        }

        public void setEndDate(Date endDate) {
            this.endDate = endDate;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTargetAptName() {
            return targetAptName;
        }

        public void setTargetAptName(String targetAptName) {
            this.targetAptName = targetAptName;
        }

        @Override public String toString() {
            return "Search{" + "status='" + status + '\'' + ", type='" + type + '\'' + ", startDate=" + startDate + ", endDate=" + endDate + ", title='" + title + '\'' + ", targetAptName='"
                    + targetAptName + '\'' + '}';
        }
    }

}
