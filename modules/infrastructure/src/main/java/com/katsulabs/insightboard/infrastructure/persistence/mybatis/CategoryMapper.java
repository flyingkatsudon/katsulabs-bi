package com.katsulabs.insightboard.infrastructure.persistence.mybatis;

import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CategoryMapper {

    List<CategoryRow> findAll();

    CategoryRow findById(@Param("id") long id);

    int countByName(@Param("name") String name, @Param("excludeId") Long excludeId);

    void insert(CategoryRow row);

    void update(CategoryRow row);

    void delete(@Param("id") long id);
}
