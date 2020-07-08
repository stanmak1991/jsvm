package com.makovetskiy.jsvm.model;

import javax.persistence.Column;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class GenericEntity {

    @Column(name = "time_create", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDate createTime;

    @Column(name = "time_update")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDate updateTime;

    public static LocalDate getLocalDateTime() {
        return LocalDateTime.now().toLocalDate();
    }

    @PrePersist
    private void save() {
        this.createTime = getLocalDateTime();
        this.updateTime = getLocalDateTime();
    }

}
