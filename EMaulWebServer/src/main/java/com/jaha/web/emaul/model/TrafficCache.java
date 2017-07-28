package com.jaha.web.emaul.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * Created by doring on 15. 5. 15..
 */
@Entity
@Table(name = "traffic_cache", indexes = {@Index(name = "idx_traffic_cache", columnList = "cacheDate")})
public class TrafficCache {
    @Id
    public String cacheKey;

    @Column(columnDefinition = "TEXT NOT NULL")
    public String json;

    @Column(nullable = false)
    public Date cacheDate;

    @Column(nullable = false)
    public Integer expireMinutes;
}
