package com.katsulabs.insightboard.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import com.katsulabs.insightboard.domain.widget.WidgetDetail;
import com.katsulabs.insightboard.domain.widget.WidgetRepository;
import com.katsulabs.insightboard.domain.widget.WidgetSummary;
import com.katsulabs.insightboard.infrastructure.persistence.compat.CboardWidgetJson;
import com.katsulabs.insightboard.infrastructure.persistence.mybatis.WidgetBindingMapper;
import com.katsulabs.insightboard.infrastructure.persistence.mybatis.WidgetMapper;
import com.katsulabs.insightboard.infrastructure.persistence.mybatis.WidgetRow;
import org.springframework.stereotype.Repository;

@Repository
public class WidgetRepositoryImpl implements WidgetRepository {

    private final WidgetMapper widgetMapper;
    private final WidgetBindingMapper widgetBindingMapper;

    public WidgetRepositoryImpl(WidgetMapper widgetMapper, WidgetBindingMapper widgetBindingMapper) {
        this.widgetMapper = widgetMapper;
        this.widgetBindingMapper = widgetBindingMapper;
    }

    @Override
    public List<WidgetSummary> findAllSummaries() {
        return widgetMapper.findAllSummaries().stream().map(WidgetRepositoryImpl::toSummary).toList();
    }

    @Override
    public Optional<WidgetDetail> findById(long widgetId) {
        WidgetRow row = widgetMapper.findById(widgetId);
        if (row == null) {
            return Optional.empty();
        }
        var bindings = widgetBindingMapper.findByWidgetId(widgetId);
        String dataJson = CboardWidgetJson.compose(row, bindings);
        return Optional.of(toDetail(row, dataJson));
    }

    @Override
    public boolean existsByName(String userId, String name, String categoryName, Long excludeId) {
        return widgetMapper.countByName(userId, name, categoryName, excludeId) > 0;
    }

    @Override
    public long insert(String userId, String name, String categoryName, String dataJson) {
        WidgetRow row = new WidgetRow();
        row.setUserId(userId);
        row.setName(name);
        row.setCategoryName(defaultCategory(categoryName));
        CboardWidgetJson.persistFromJson(widgetMapper, widgetBindingMapper, row, dataJson);
        return row.getId();
    }

    @Override
    public void update(long id, String name, String categoryName, String dataJson) {
        WidgetRow row = widgetMapper.findById(id);
        if (row == null) {
            throw new IllegalArgumentException("위젯을 찾을 수 없습니다: " + id);
        }
        row.setName(name);
        row.setCategoryName(defaultCategory(categoryName));
        CboardWidgetJson.persistFromJson(widgetMapper, widgetBindingMapper, row, dataJson);
    }

    @Override
    public void delete(long id) {
        widgetBindingMapper.deleteByWidgetId(id);
        widgetMapper.delete(id);
    }

    private static WidgetSummary toSummary(WidgetRow row) {
        return new WidgetSummary(
                row.getId(),
                row.getName(),
                row.getUserId(),
                row.getUserName(),
                row.getCategoryName(),
                PersistenceMapperSupport.toInstant(row.getCreateTime()),
                PersistenceMapperSupport.toInstant(row.getUpdateTime()));
    }

    private static WidgetDetail toDetail(WidgetRow row, String dataJson) {
        return new WidgetDetail(
                row.getId(),
                row.getName(),
                row.getUserId(),
                row.getUserName(),
                row.getCategoryName(),
                dataJson,
                PersistenceMapperSupport.toInstant(row.getCreateTime()),
                PersistenceMapperSupport.toInstant(row.getUpdateTime()));
    }

    private static String defaultCategory(String categoryName) {
        return categoryName == null || categoryName.isBlank() ? "Default Category" : categoryName;
    }
}
