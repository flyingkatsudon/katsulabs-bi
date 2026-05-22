package com.katsulabs.bi.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import com.katsulabs.bi.domain.category.CategoryRepository;
import com.katsulabs.bi.domain.category.CategorySummary;
import com.katsulabs.bi.infrastructure.persistence.mybatis.CategoryMapper;
import com.katsulabs.bi.infrastructure.persistence.mybatis.CategoryRow;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryMapper categoryMapper;

    public CategoryRepositoryImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<CategorySummary> findAll() {
        return categoryMapper.findAll().stream()
                .map(CategoryRepositoryImpl::toSummary)
                .toList();
    }

    @Override
    public Optional<CategorySummary> findById(long id) {
        CategoryRow row = categoryMapper.findById(id);
        return row == null ? Optional.empty() : Optional.of(toSummary(row));
    }

    @Override
    public boolean existsByName(String name, Long excludeId) {
        return categoryMapper.countByName(name, excludeId) > 0;
    }

    @Override
    public long insert(String userId, String name) {
        CategoryRow row = new CategoryRow();
        row.setName(name);
        row.setUserId(userId);
        categoryMapper.insert(row);
        return row.getId();
    }

    @Override
    public void update(long id, String userId, String name) {
        CategoryRow row = new CategoryRow();
        row.setId(id);
        row.setName(name);
        row.setUserId(userId);
        categoryMapper.update(row);
    }

    @Override
    public void delete(long id) {
        categoryMapper.delete(id);
    }

    private static CategorySummary toSummary(CategoryRow row) {
        return new CategorySummary(row.getId(), row.getName(), row.getUserId());
    }
}
