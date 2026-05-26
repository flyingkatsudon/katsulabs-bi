package com.katsulabs.bi.infrastructure.domains.widget.persistence.mybatis;

import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface WidgetBindingMapper {
    List<WidgetBindingRow> findByWidgetId(@Param("widgetId") long widgetId);

    void deleteByWidgetId(@Param("widgetId") long widgetId);

    void insert(WidgetBindingRow row);
}
