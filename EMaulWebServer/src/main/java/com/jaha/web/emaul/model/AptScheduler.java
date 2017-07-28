package com.jaha.web.emaul.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "apt_scheduler")
public class AptScheduler extends BaseSecuModel {

    /** push_timing예약시에 batch_schedule의 batch_group_key에 사용하기위해 생성할키의 prefix */
    @Transient
    public final String aptSchedulerPushPrefix = "AptSchedulerPush_";

    /** exposure_timing예약시에 batch_schedule의 batch_group_key에 사용하기위해 생성할키의 prefix */
    @Transient
    public final String aptSchedulerExposurePrefix = "AptSchedulerExposure_";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    public Long aptId;

    @Column(length = 5)
    public String noticeTarget;

    @Transient
    public String noticeTargetName;

    @Column(length = 20)
    public String noticeTargetDong;

    @Column(length = 20)
    public String noticeTargetHo;

    @Column(length = 10)
    public String startDate;

    @Column(length = 5)
    public String startTime;

    @Column(length = 10)
    public String endDate;

    @Column(length = 5)
    public String endTime;

    @Column(length = 60)
    public String title;

    @Column(length = 200)
    public String content;

    @Column(length = 45)
    public String pushTiming;

    @Column(length = 45)
    public String exposureTiming;

    @Transient
    public String[] pushDate;

    @Transient
    public String[] pushTime;

    @Transient
    public String exposureDate;

    @Transient
    public String exposureTime;

    @Column(length = 45)
    public String status;

    public Long regId;

    public Date regDate;

    @Transient
    public String regDateStr;

    public Long modId;

    public Date modDate;

    @Transient
    public String modDateStr;

    @Transient
    public String period;

    @Transient
    public String fullName;

    @Transient
    // 복호화한 이름
    private String fullNameDecrypted;

    @Transient
    public List<BatchSchedule> pushTimingList;

    @Transient
    public BatchSchedule exposureTimingSchedule;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAptId() {
        return aptId;
    }

    public void setAptId(Long aptId) {
        this.aptId = aptId;
    }

    public String getNoticeTarget() {
        return noticeTarget;
    }

    public void setNoticeTarget(String noticeTarget) {
        this.noticeTarget = noticeTarget;
    }

    public String getNoticeTargetDong() {
        return noticeTargetDong;
    }

    public void setNoticeTargetDong(String noticeTargetDong) {
        this.noticeTargetDong = noticeTargetDong;
    }

    public String getNoticeTargetHo() {
        return noticeTargetHo;
    }

    public void setNoticeTargetHo(String noticeTargetHo) {
        this.noticeTargetHo = noticeTargetHo;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPushTiming() {
        return pushTiming;
    }

    public void setPushTiming(String pushTiming) {
        this.pushTiming = pushTiming;
    }

    public String getExposureTiming() {
        return exposureTiming;
    }

    public void setExposureTiming(String exposureTiming) {
        this.exposureTiming = exposureTiming;
    }

    public String[] getPushDate() {
        return pushDate;
    }

    public void setPushDate(String[] pushDate) {
        this.pushDate = pushDate;
    }

    public String[] getPushTime() {
        return pushTime;
    }

    public void setPushTime(String[] pushTime) {
        this.pushTime = pushTime;
    }

    public String getExposureDate() {
        return exposureDate;
    }

    public void setExposureDate(String exposureDate) {
        this.exposureDate = exposureDate;
    }

    public String getExposureTime() {
        return exposureTime;
    }

    public void setExposureTime(String exposureTime) {
        this.exposureTime = exposureTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getRegId() {
        return regId;
    }

    public void setRegId(Long regId) {
        this.regId = regId;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public String getRegDateStr() {
        return regDateStr;
    }

    public void setRegDateStr(String regDateStr) {
        this.regDateStr = regDateStr;
    }

    public Long getModId() {
        return modId;
    }

    public void setModId(Long modId) {
        this.modId = modId;
    }

    public Date getModDate() {
        return modDate;
    }

    public void setModDate(Date modDate) {
        this.modDate = modDate;
    }

    public String getModDateStr() {
        return modDateStr;
    }

    public void setModDateStr(String modDateStr) {
        this.modDateStr = modDateStr;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    // 이름 가져오기
    public String getFullName() {
        if (this.fullNameDecrypted == null) {
            this.fullNameDecrypted = descString(fullName);
            return this.fullNameDecrypted;
        }

        return this.fullNameDecrypted;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNoticeTargetName() {
        return noticeTargetName;
    }

    public void setNoticeTargetName(String noticeTargetName) {
        this.noticeTargetName = noticeTargetName;
    }

    public List<BatchSchedule> getPushTimingList() {
        return pushTimingList;
    }

    public void setPushTimingList(List<BatchSchedule> pushTimingList) {
        this.pushTimingList = pushTimingList;
    }

    public BatchSchedule getExposureTimingSchedule() {
        return exposureTimingSchedule;
    }

    public void setExposureTimingSchedule(BatchSchedule exposureTimingSchedule) {
        this.exposureTimingSchedule = exposureTimingSchedule;
    }

    public String getAptSchedulerPushBatchGroupKey() {
        return aptSchedulerPushPrefix + id;
    }

    public String getAptSchedulerExposureBatchGroupKey() {
        return aptSchedulerExposurePrefix + id;
    }

    @PrePersist
    public void prePersist() {
        regDate = new Date();
        modDate = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        modDate = new Date();
    }

    @PreRemove
    public void preRemove() {

    }

}
