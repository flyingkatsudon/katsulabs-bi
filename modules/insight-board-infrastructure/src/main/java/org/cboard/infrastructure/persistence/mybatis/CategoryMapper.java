package org.cboard.infrastructure.persistence.mybatis;

import java.util.List;

public interface CategoryMapper {

    List<CategoryRow> findAll();
}
