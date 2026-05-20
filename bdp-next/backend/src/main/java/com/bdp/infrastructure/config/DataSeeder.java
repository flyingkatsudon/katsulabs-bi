package com.bdp.infrastructure.config;

import com.bdp.domain.analytics.DailyKwdTrend;
import com.bdp.domain.metadata.DashboardBoard;
import com.bdp.domain.metadata.DashboardCategory;
import com.bdp.domain.metadata.DashboardDataset;
import com.bdp.domain.metadata.DashboardDatasource;
import com.bdp.domain.metadata.DashboardUser;
import com.bdp.domain.metadata.DashboardWidget;
import com.bdp.infrastructure.persistence.DailyKwdTrendRepository;
import com.bdp.infrastructure.persistence.DashboardBoardRepository;
import com.bdp.infrastructure.persistence.DashboardCategoryRepository;
import com.bdp.infrastructure.persistence.DashboardDatasetRepository;
import com.bdp.infrastructure.persistence.DashboardDatasourceRepository;
import com.bdp.domain.metadata.DashboardRole;
import com.bdp.domain.metadata.DashboardUserRole;
import com.bdp.infrastructure.persistence.DashboardRoleRepository;
import com.bdp.infrastructure.persistence.DashboardUserRepository;
import com.bdp.infrastructure.persistence.DashboardUserRoleRepository;
import com.bdp.infrastructure.persistence.DashboardWidgetRepository;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    private static final String SAMPLE_LAYOUT =
            """
            {"type":"grid","rows":[{"height":360,"widgets":[{"width":6,"name":"키워드 트렌드","show":true,"loading":false,"widget":{"id":1}},{"width":6,"name":"감성 분포","show":true,"loading":false,"widget":{"id":2}}]}]}
            """;

    private static final String WIDGET_LINE =
            """
            {"chart_type":"line","option":{"title":{"text":"키워드 트렌드"},"tooltip":{},"xAxis":{"type":"category","data":["D-2","D-1","D"]},"yAxis":{"type":"value"},"series":[{"type":"line","data":[15,18,25]}]}}
            """;

    private static final String WIDGET_PIE =
            """
            {"chart_type":"pie","option":{"title":{"text":"감성 분포"},"tooltip":{"trigger":"item"},"series":[{"type":"pie","radius":"55%","data":[{"value":12,"name":"긍정"},{"value":4,"name":"부정"},{"value":9,"name":"중립"}]}]}}
            """;

    @Bean
    @Profile("local")
    CommandLineRunner seedLocalData(
            DashboardUserRepository userRepository,
            DailyKwdTrendRepository trendRepository,
            DashboardCategoryRepository categoryRepository,
            DashboardBoardRepository boardRepository,
            DashboardWidgetRepository widgetRepository,
            DashboardDatasetRepository datasetRepository,
            DashboardDatasourceRepository datasourceRepository,
            DashboardRoleRepository roleRepository,
            DashboardUserRoleRepository userRoleRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                DashboardUser admin = new DashboardUser();
                admin.setUserId("1");
                admin.setLoginName("admin");
                admin.setUserName("Administrator");
                admin.setUserPassword(passwordEncoder.encode("admin"));
                admin.setUserStatus("ACTIVE");
                admin.setBusinessCode("DEFAULT");
                userRepository.save(admin);
            }
            if (roleRepository.count() == 0) {
                DashboardRole adminRole = new DashboardRole();
                adminRole.setRoleId("ADMIN");
                adminRole.setRoleName("Administrator");
                adminRole.setUserId("1");
                roleRepository.save(adminRole);
                DashboardUserRole ur = new DashboardUserRole();
                ur.setUserId("1");
                ur.setRoleId("ADMIN");
                userRoleRepository.save(ur);
            }
            if (trendRepository.count() == 0) {
                seedTrend(trendRepository, LocalDate.now().minusDays(2), "코스피", "상승", 15);
                seedTrend(trendRepository, LocalDate.now().minusDays(1), "코스피", "변동성", 18);
                seedTrend(trendRepository, LocalDate.now(), "금리", "한국은행", 25);
            }
            if (categoryRepository.count() == 0) {
                DashboardCategory cat = new DashboardCategory();
                cat.setCategoryName("시장 브리핑");
                cat.setUserId("1");
                categoryRepository.save(cat);

                DashboardDatasource ds = new DashboardDatasource();
                ds.setUserId("1");
                ds.setSourceName("Local H2");
                ds.setSourceType("jdbc");
                ds.setConfig("{\"driver\":\"org.h2.Driver\",\"jdbcurl\":\"jdbc:h2:mem:bdp\"}");
                datasourceRepository.save(ds);

                DashboardDataset dataset = new DashboardDataset();
                dataset.setUserId("1");
                dataset.setDatasetName("데모 데이터셋");
                dataset.setCategoryName("default");
                dataset.setDataJson(
                        "{\"datasource\":1,\"query\":{\"table\":\"daily_kwd_trend_cnt_minimal_v2\"},\"schema\":{\"measure\":[{\"column\":\"doc_cnt_both\",\"aggType\":\"sum\"}],\"dimension\":[{\"columnName\":\"doc_date\"},{\"columnName\":\"kwd_a\"}]}}");
                datasetRepository.save(dataset);

                DashboardWidget w1 = new DashboardWidget();
                w1.setUserId("1");
                w1.setWidgetName("키워드 트렌드");
                w1.setCategoryName("default");
                w1.setDataJson(WIDGET_LINE);
                widgetRepository.save(w1);

                DashboardWidget w2 = new DashboardWidget();
                w2.setUserId("1");
                w2.setWidgetName("감성 분포");
                w2.setCategoryName("default");
                w2.setDataJson(WIDGET_PIE);
                widgetRepository.save(w2);

                DashboardBoard board = new DashboardBoard();
                board.setUserId("1");
                board.setCategoryId(cat.getCategoryId());
                board.setBoardName("데모 대시보드");
                board.setLayoutJson(SAMPLE_LAYOUT);
                boardRepository.save(board);
            }
        };
    }

    private static void seedTrend(
            DailyKwdTrendRepository repo, LocalDate date, String kwdA, String kwdB, int both) {
        DailyKwdTrend row = new DailyKwdTrend();
        row.setFid("BDPC04030205");
        row.setBusinessCode("FIN");
        row.setCategoryCode("BDPC01");
        row.setDocDate(date);
        row.setKwdA(kwdA);
        row.setKwdB(kwdB);
        row.setDocCntBoth(both);
        row.setPosCntBoth(10);
        row.setNegCntBoth(3);
        repo.save(row);
    }
}
