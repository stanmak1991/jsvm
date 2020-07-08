package com.makovetskiy.jsvm.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_js_entity")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JsEntity extends GenericEntity {

    @Id
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @GeneratedValue(generator = "uuid")
    @Column(name = "id")
    private String id;

    @Column(name = "request")
    private String request;

    @Column(name = "console_response")
    private String consoleResponse;

    @Column(name = "console_error")
    private String consoleError;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "is_deleted")
    private Boolean isDeleted;
}
