package org.cboard.infrastructure.persistence;

import java.util.List;

import org.cboard.domain.category.CategoryRepository;
import org.cboard.domain.category.CategorySummary;
import org.cboard.infrastructure.persistence.mybatis.CategoryMapper;
import org.cboard.infrastructure.persistence.mybatis.CategoryRow;
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

    private static CategorySummary toSummary(CategoryRow row) {
        return new CategorySummary(row.getId(), row.getName(), row.getUserId());
    }
}
