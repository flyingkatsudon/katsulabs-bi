package org.cboard.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.cboard.domain.widget.WidgetDetail;
import org.cboard.domain.widget.WidgetRepository;
import org.cboard.domain.widget.WidgetSummary;
import org.cboard.infrastructure.persistence.mybatis.WidgetMapper;
import org.cboard.infrastructure.persistence.mybatis.WidgetRow;
import org.springframework.stereotype.Repository;

@Repository
public class WidgetRepositoryImpl implements WidgetRepository {

    private final WidgetMapper widgetMapper;

    public WidgetRepositoryImpl(WidgetMapper widgetMapper) {
        this.widgetMapper = widgetMapper;
    }

    @Override
    public List<WidgetSummary> findAllSummaries() {
        return widgetMapper.findAllSummaries().stream().map(WidgetRepositoryImpl::toSummary).toList();
    }

    @Override
    public Optional<WidgetDetail> findById(long widgetId) {
        WidgetRow row = widgetMapper.findById(widgetId);
        return row == null ? Optional.empty() : Optional.of(toDetail(row));
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

    private static WidgetDetail toDetail(WidgetRow row) {
        return new WidgetDetail(
                row.getId(),
                row.getName(),
                row.getUserId(),
                row.getUserName(),
                row.getCategoryName(),
                row.getData(),
                PersistenceMapperSupport.toInstant(row.getCreateTime()),
                PersistenceMapperSupport.toInstant(row.getUpdateTime()));
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
        row.setData(dataJson);
        widgetMapper.insert(row);
        return row.getId();
    }

    @Override
    public void update(long id, String name, String categoryName, String dataJson) {
        WidgetRow row = new WidgetRow();
        row.setId(id);
        row.setName(name);
        row.setCategoryName(defaultCategory(categoryName));
        row.setData(dataJson);
        widgetMapper.update(row);
    }

    @Override
    public void delete(long id) {
        widgetMapper.delete(id);
    }

    private static String defaultCategory(String categoryName) {
        return categoryName == null || categoryName.isBlank() ? "默认分类" : categoryName;
    }
}
