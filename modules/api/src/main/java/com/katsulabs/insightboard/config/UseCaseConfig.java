package com.katsulabs.insightboard.config;

import com.katsulabs.insightboard.application.aggregate.AggregateQueryPort;
import com.katsulabs.insightboard.application.aggregate.QueryAggregateUseCase;
import com.katsulabs.insightboard.application.aggregate.ViewAggregateQueryUseCase;
import com.katsulabs.insightboard.application.auth.LoginUseCase;
import com.katsulabs.insightboard.application.auth.PasswordHasher;
import com.katsulabs.insightboard.application.auth.ResourceTypeLoader;
import com.katsulabs.insightboard.application.category.DeleteCategoryUseCase;
import com.katsulabs.insightboard.application.category.ListCategoriesUseCase;
import com.katsulabs.insightboard.application.category.SaveCategoryUseCase;
import com.katsulabs.insightboard.application.category.UpdateCategoryUseCase;
import com.katsulabs.insightboard.application.board.DeleteBoardUseCase;
import com.katsulabs.insightboard.application.board.GetBoardUseCase;
import com.katsulabs.insightboard.application.board.ListBoardsUseCase;
import com.katsulabs.insightboard.application.board.SaveBoardUseCase;
import com.katsulabs.insightboard.application.board.UpdateBoardUseCase;
import com.katsulabs.insightboard.application.dataset.DeleteDatasetUseCase;
import com.katsulabs.insightboard.application.dataset.GetDatasetUseCase;
import com.katsulabs.insightboard.application.dataset.ListDatasetsUseCase;
import com.katsulabs.insightboard.application.dataset.PreviewDatasetSqlUseCase;
import com.katsulabs.insightboard.application.dataset.PreviewDatasetUseCase;
import com.katsulabs.insightboard.application.dataset.SaveDatasetUseCase;
import com.katsulabs.insightboard.application.dataset.UpdateDatasetUseCase;
import com.katsulabs.insightboard.application.datasource.DatasourceConnectionTestPort;
import com.katsulabs.insightboard.application.dataset.DatasetSqlPreviewPort;
import com.katsulabs.insightboard.application.datasource.TestDatasourceUseCase;
import com.katsulabs.insightboard.application.datasource.DeleteDatasourceUseCase;
import com.katsulabs.insightboard.application.datasource.GetDatasourceUseCase;
import com.katsulabs.insightboard.application.datasource.ListDatasourcesUseCase;
import com.katsulabs.insightboard.application.datasource.SaveDatasourceUseCase;
import com.katsulabs.insightboard.application.datasource.UpdateDatasourceUseCase;
import com.katsulabs.insightboard.application.health.GetHealthUseCase;
import com.katsulabs.insightboard.application.widget.DeleteWidgetUseCase;
import com.katsulabs.insightboard.application.widget.GetWidgetUseCase;
import com.katsulabs.insightboard.application.widget.ListWidgetsUseCase;
import com.katsulabs.insightboard.application.widget.SaveWidgetUseCase;
import com.katsulabs.insightboard.application.user.DeleteUserUseCase;
import com.katsulabs.insightboard.application.user.ListUsersUseCase;
import com.katsulabs.insightboard.application.user.SaveUserUseCase;
import com.katsulabs.insightboard.application.user.UpdateUserUseCase;
import com.katsulabs.insightboard.application.widget.UpdateWidgetUseCase;
import com.katsulabs.insightboard.domain.board.BoardRepository;
import com.katsulabs.insightboard.domain.category.CategoryRepository;
import com.katsulabs.insightboard.domain.dataset.DatasetRepository;
import com.katsulabs.insightboard.domain.datasource.DatasourceRepository;
import com.katsulabs.insightboard.domain.user.UserRepository;
import com.katsulabs.insightboard.domain.widget.WidgetRepository;
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
