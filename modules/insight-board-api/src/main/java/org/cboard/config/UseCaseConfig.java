package org.cboard.config;

import org.cboard.application.aggregate.AggregateQueryPort;
import org.cboard.application.aggregate.QueryAggregateUseCase;
import org.cboard.application.aggregate.ViewAggregateQueryUseCase;
import org.cboard.application.auth.LoginUseCase;
import org.cboard.application.auth.PasswordHasher;
import org.cboard.application.auth.ResourceTypeLoader;
import org.cboard.application.category.ListCategoriesUseCase;
import org.cboard.application.board.DeleteBoardUseCase;
import org.cboard.application.board.GetBoardUseCase;
import org.cboard.application.board.ListBoardsUseCase;
import org.cboard.application.board.SaveBoardUseCase;
import org.cboard.application.board.UpdateBoardUseCase;
import org.cboard.application.dataset.DeleteDatasetUseCase;
import org.cboard.application.dataset.GetDatasetUseCase;
import org.cboard.application.dataset.ListDatasetsUseCase;
import org.cboard.application.dataset.PreviewDatasetSqlUseCase;
import org.cboard.application.dataset.PreviewDatasetUseCase;
import org.cboard.application.dataset.SaveDatasetUseCase;
import org.cboard.application.dataset.UpdateDatasetUseCase;
import org.cboard.application.datasource.DatasourceConnectionTestPort;
import org.cboard.application.dataset.DatasetSqlPreviewPort;
import org.cboard.application.datasource.TestDatasourceUseCase;
import org.cboard.application.datasource.DeleteDatasourceUseCase;
import org.cboard.application.datasource.GetDatasourceUseCase;
import org.cboard.application.datasource.ListDatasourcesUseCase;
import org.cboard.application.datasource.SaveDatasourceUseCase;
import org.cboard.application.datasource.UpdateDatasourceUseCase;
import org.cboard.application.health.GetHealthUseCase;
import org.cboard.application.widget.DeleteWidgetUseCase;
import org.cboard.application.widget.GetWidgetUseCase;
import org.cboard.application.widget.ListWidgetsUseCase;
import org.cboard.application.widget.SaveWidgetUseCase;
import org.cboard.application.widget.UpdateWidgetUseCase;
import org.cboard.domain.board.BoardRepository;
import org.cboard.domain.category.CategoryRepository;
import org.cboard.domain.dataset.DatasetRepository;
import org.cboard.domain.datasource.DatasourceRepository;
import org.cboard.domain.user.UserRepository;
import org.cboard.domain.widget.WidgetRepository;
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
}
