package com.makovetskiy.jsvm.repository;

import com.makovetskiy.jsvm.model.JsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JsEntityRepository extends JpaRepository<JsEntity, String> {
}
