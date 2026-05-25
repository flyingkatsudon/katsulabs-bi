package org.cboard.domain.category;

import java.util.List;

public interface CategoryRepository {

    List<CategorySummary> findAll();
}
