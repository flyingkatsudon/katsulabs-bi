package com.katsulabs.bi.infrastructure.persistence.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WidgetMapper {

    List<WidgetRow> findAllSummaries();

    WidgetRow findById(@Param("widgetId") long widgetId);

    long countByName(
            @Param("userId") String userId,
            @Param("name") String name,
            @Param("categoryName") String categoryName,
            @Param("excludeId") Long excludeId);

    void insert(WidgetRow row);

    void update(WidgetRow row);

    void delete(@Param("widgetId") long widgetId);
}
