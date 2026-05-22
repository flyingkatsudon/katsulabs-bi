package com.katsulabs.bi.config;

import com.katsulabs.bi.application.aggregate.AggregateQueryPort;
import com.katsulabs.bi.application.aggregate.QueryAggregateUseCase;
import com.katsulabs.bi.application.aggregate.ViewAggregateQueryUseCase;
import com.katsulabs.bi.application.auth.LoginUseCase;
import com.katsulabs.bi.application.auth.PasswordHasher;
import com.katsulabs.bi.application.auth.ResourceTypeLoader;
import com.katsulabs.bi.application.category.DeleteCategoryUseCase;
import com.katsulabs.bi.application.category.ListCategoriesUseCase;
import com.katsulabs.bi.application.category.SaveCategoryUseCase;
import com.katsulabs.bi.application.category.UpdateCategoryUseCase;
import com.katsulabs.bi.application.board.DeleteBoardUseCase;
import com.katsulabs.bi.application.board.GetBoardUseCase;
import com.katsulabs.bi.application.board.ListBoardsUseCase;
import com.katsulabs.bi.application.board.SaveBoardUseCase;
import com.katsulabs.bi.application.board.UpdateBoardUseCase;
import com.katsulabs.bi.application.dataset.DeleteDatasetUseCase;
import com.katsulabs.bi.application.dataset.GetDatasetUseCase;
import com.katsulabs.bi.application.dataset.ListDatasetsUseCase;
import com.katsulabs.bi.application.dataset.PreviewDatasetSqlUseCase;
import com.katsulabs.bi.application.dataset.PreviewDatasetUseCase;
import com.katsulabs.bi.application.dataset.SaveDatasetUseCase;
import com.katsulabs.bi.application.dataset.UpdateDatasetUseCase;
import com.katsulabs.bi.application.datasource.DatasourceConnectionTestPort;
import com.katsulabs.bi.application.dataset.DatasetSqlPreviewPort;
import com.katsulabs.bi.application.datasource.TestDatasourceUseCase;
import com.katsulabs.bi.application.datasource.DeleteDatasourceUseCase;
import com.katsulabs.bi.application.datasource.GetDatasourceUseCase;
import com.katsulabs.bi.application.datasource.ListDatasourcesUseCase;
import com.katsulabs.bi.application.datasource.SaveDatasourceUseCase;
import com.katsulabs.bi.application.datasource.UpdateDatasourceUseCase;
import com.katsulabs.bi.application.health.GetHealthUseCase;
import com.katsulabs.bi.application.widget.DeleteWidgetUseCase;
import com.katsulabs.bi.application.widget.GetWidgetUseCase;
import com.katsulabs.bi.application.widget.ListWidgetsUseCase;
import com.katsulabs.bi.application.widget.SaveWidgetUseCase;
import com.katsulabs.bi.application.user.DeleteUserUseCase;
import com.katsulabs.bi.application.user.ListUsersUseCase;
import com.katsulabs.bi.application.user.SaveUserUseCase;
import com.katsulabs.bi.application.user.UpdateUserUseCase;
import com.katsulabs.bi.application.widget.UpdateWidgetUseCase;
import com.katsulabs.bi.domain.board.BoardRepository;
import com.katsulabs.bi.domain.category.CategoryRepository;
import com.katsulabs.bi.domain.dataset.DatasetRepository;
import com.katsulabs.bi.domain.datasource.DatasourceRepository;
import com.katsulabs.bi.domain.user.UserRepository;
import com.katsulabs.bi.domain.widget.WidgetRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    GetHealthUseCase getHealthUseCase() {
        return new GetHealthUseCase();
    }

    @Bean
    LoginUseCase loginUseCase(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            ResourceTypeLoader resourceTypeLoader) {
        return new LoginUseCase(userRepository, passwordHasher, resourceTypeLoader);
    }

    @Bean
    ListCategoriesUseCase listCategoriesUseCase(CategoryRepository categoryRepository) {
        return new ListCategoriesUseCase(categoryRepository);
    }

    @Bean
    SaveCategoryUseCase saveCategoryUseCase(CategoryRepository categoryRepository) {
        return new SaveCategoryUseCase(categoryRepository);
    }

    @Bean
    UpdateCategoryUseCase updateCategoryUseCase(CategoryRepository categoryRepository) {
        return new UpdateCategoryUseCase(categoryRepository);
    }

    @Bean
    DeleteCategoryUseCase deleteCategoryUseCase(CategoryRepository categoryRepository) {
        return new DeleteCategoryUseCase(categoryRepository);
    }

    @Bean
    ListBoardsUseCase listBoardsUseCase(BoardRepository boardRepository) {
        return new ListBoardsUseCase(boardRepository);
    }

    @Bean
    GetBoardUseCase getBoardUseCase(BoardRepository boardRepository) {
        return new GetBoardUseCase(boardRepository);
    }

    @Bean
    SaveBoardUseCase saveBoardUseCase(BoardRepository boardRepository) {
        return new SaveBoardUseCase(boardRepository);
    }

    @Bean
    UpdateBoardUseCase updateBoardUseCase(BoardRepository boardRepository) {
        return new UpdateBoardUseCase(boardRepository);
    }

    @Bean
    DeleteBoardUseCase deleteBoardUseCase(BoardRepository boardRepository) {
        return new DeleteBoardUseCase(boardRepository);
    }

    @Bean
    ListDatasetsUseCase listDatasetsUseCase(DatasetRepository datasetRepository) {
        return new ListDatasetsUseCase(datasetRepository);
    }

    @Bean
    GetDatasetUseCase getDatasetUseCase(DatasetRepository datasetRepository) {
        return new GetDatasetUseCase(datasetRepository);
    }

    @Bean
    SaveDatasetUseCase saveDatasetUseCase(DatasetRepository datasetRepository) {
        return new SaveDatasetUseCase(datasetRepository);
    }

    @Bean
    UpdateDatasetUseCase updateDatasetUseCase(DatasetRepository datasetRepository) {
        return new UpdateDatasetUseCase(datasetRepository);
    }

    @Bean
    DeleteDatasetUseCase deleteDatasetUseCase(DatasetRepository datasetRepository) {
        return new DeleteDatasetUseCase(datasetRepository);
    }

    @Bean
    PreviewDatasetUseCase previewDatasetUseCase(DatasetSqlPreviewPort datasetSqlPreviewPort) {
        return new PreviewDatasetUseCase(datasetSqlPreviewPort);
    }

    @Bean
    PreviewDatasetSqlUseCase previewDatasetSqlUseCase(DatasetSqlPreviewPort datasetSqlPreviewPort) {
        return new PreviewDatasetSqlUseCase(datasetSqlPreviewPort);
    }

    @Bean
    ListDatasourcesUseCase listDatasourcesUseCase(DatasourceRepository datasourceRepository) {
        return new ListDatasourcesUseCase(datasourceRepository);
    }

    @Bean
    GetDatasourceUseCase getDatasourceUseCase(DatasourceRepository datasourceRepository) {
        return new GetDatasourceUseCase(datasourceRepository);
    }

    @Bean
    SaveDatasourceUseCase saveDatasourceUseCase(DatasourceRepository datasourceRepository) {
        return new SaveDatasourceUseCase(datasourceRepository);
    }

    @Bean
    UpdateDatasourceUseCase updateDatasourceUseCase(DatasourceRepository datasourceRepository) {
        return new UpdateDatasourceUseCase(datasourceRepository);
    }

    @Bean
    DeleteDatasourceUseCase deleteDatasourceUseCase(DatasourceRepository datasourceRepository) {
        return new DeleteDatasourceUseCase(datasourceRepository);
    }

    @Bean
    TestDatasourceUseCase testDatasourceUseCase(
            DatasourceRepository datasourceRepository, DatasourceConnectionTestPort connectionTestPort) {
        return new TestDatasourceUseCase(datasourceRepository, connectionTestPort);
    }

    @Bean
    ListWidgetsUseCase listWidgetsUseCase(WidgetRepository widgetRepository) {
        return new ListWidgetsUseCase(widgetRepository);
    }

    @Bean
    GetWidgetUseCase getWidgetUseCase(WidgetRepository widgetRepository) {
        return new GetWidgetUseCase(widgetRepository);
    }

    @Bean
    SaveWidgetUseCase saveWidgetUseCase(WidgetRepository widgetRepository) {
        return new SaveWidgetUseCase(widgetRepository);
    }

    @Bean
    UpdateWidgetUseCase updateWidgetUseCase(WidgetRepository widgetRepository) {
        return new UpdateWidgetUseCase(widgetRepository);
    }

    @Bean
    DeleteWidgetUseCase deleteWidgetUseCase(WidgetRepository widgetRepository) {
        return new DeleteWidgetUseCase(widgetRepository);
    }

    @Bean
    QueryAggregateUseCase queryAggregateUseCase(AggregateQueryPort aggregateQueryPort) {
        return new QueryAggregateUseCase(aggregateQueryPort);
    }

    @Bean
    ViewAggregateQueryUseCase viewAggregateQueryUseCase(AggregateQueryPort aggregateQueryPort) {
        return new ViewAggregateQueryUseCase(aggregateQueryPort);
    }

    @Bean
    ListUsersUseCase listUsersUseCase(UserRepository userRepository) {
        return new ListUsersUseCase(userRepository);
    }

    @Bean
    SaveUserUseCase saveUserUseCase(UserRepository userRepository, PasswordHasher passwordHasher) {
        return new SaveUserUseCase(userRepository, passwordHasher);
    }

    @Bean
    UpdateUserUseCase updateUserUseCase(UserRepository userRepository, PasswordHasher passwordHasher) {
        return new UpdateUserUseCase(userRepository, passwordHasher);
    }

    @Bean
    DeleteUserUseCase deleteUserUseCase(UserRepository userRepository) {
        return new DeleteUserUseCase(userRepository);
    }
}
