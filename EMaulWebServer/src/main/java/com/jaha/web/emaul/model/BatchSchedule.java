package com.jaha.web.emaul.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

@Entity
@Table(name = "batch_schedule")
public class BatchSchedule {

    /** 일련번호 */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int seq;

    /** 배치작업명. */
    @Column(length = 90)
    public String batchTaskName;

    /** 배치클래스명 */
    @Column(length = 90)
    public String batchClassName;

    /** 배치작업을수행을요청할URL */
    @Column(length = 90)
    public String batchTaskReqUrl;

    /** GET/POST */
    @Column(length = 5)
    public String httpMethod;

    /** URL로넘길파라미터(예:name1=value1&name2=value2) */
    @Column(length = 300)
    public String params;

    /** 배치스케줄메모 */
    @Column(length = 3000)
    public String memo;

    /** 크론표현식(예,0 0 8-10 * * ?) */
    @Column(length = 20)
    public String cronExp;

    /** 스케줄상태(active/unactive/refresh) */
    @Column(length = 10)
    public String scheduleState;

    /** 등록자ID */
    @Column(length = 20)
    public String regId;

    /** 등록일시 */
    public Date regDate;

    /** 수정자ID */
    @Column(length = 20)
    public String modId;

    /** 수정일시 */
    public Date modDate;

    /** 배치스케줄실행간격(분) */
    public int scheduleInterval;

    /** 스케줄실행시간(분단위까지,yyyyMMddHHmm) */
    @Column(length = 12)
    public String nextRuntime;

    @Column(length = 40)
    public String batchGroupKey;

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getBatchTaskName() {
        return batchTaskName;
    }

    public void setBatchTaskName(String batchTaskName) {
        this.batchTaskName = batchTaskName;
    }

    public String getBatchClassName() {
        return batchClassName;
    }

    public void setBatchClassName(String batchClassName) {
        this.batchClassName = batchClassName;
    }

    public String getBatchTaskReqUrl() {
        return batchTaskReqUrl;
    }

    public void setBatchTaskReqUrl(String batchTaskReqUrl) {
        this.batchTaskReqUrl = batchTaskReqUrl;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getCronExp() {
        return cronExp;
    }

    public void setCronExp(String cronExp) {
        this.cronExp = cronExp;
    }

    public String getScheduleState() {
        return scheduleState;
    }

    public void setScheduleState(String scheduleState) {
        this.scheduleState = scheduleState;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public String getModId() {
        return modId;
    }

    public void setModId(String modId) {
        this.modId = modId;
    }

    public Date getModDate() {
        return modDate;
    }

    public void setModDate(Date modDate) {
        this.modDate = modDate;
    }

    public int getScheduleInterval() {
        return scheduleInterval;
    }

    public void setScheduleInterval(int scheduleInterval) {
        this.scheduleInterval = scheduleInterval;
    }

    public String getNextRuntime() {
        return nextRuntime;
    }

    public void setNextRuntime(String nextRuntime) {
        this.nextRuntime = nextRuntime;
    }

    public String getBatchGroupKey() {
        return batchGroupKey;
    }

    public void setBatchGroupKey(String batchGroupKey) {
        this.batchGroupKey = batchGroupKey;
    }

    @PrePersist
    public void prePersist() {
        regDate = new Date();
        scheduleState = "active";
    }

    @PreUpdate
    public void preUpdate() {
        modDate = new Date();
    }

    @PreRemove
    public void preRemove() {

    }

}
