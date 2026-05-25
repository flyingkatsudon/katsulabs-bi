package com.katsulabs.insightboard.domain.widget;

import java.util.List;
import java.util.Optional;

public interface WidgetRepository {

    List<WidgetSummary> findAllSummaries();

    Optional<WidgetDetail> findById(long widgetId);

    boolean existsByName(String userId, String name, String categoryName, Long excludeId);

    long insert(String userId, String name, String categoryName, String dataJson);

    void update(long id, String name, String categoryName, String dataJson);

    void delete(long id);
}
