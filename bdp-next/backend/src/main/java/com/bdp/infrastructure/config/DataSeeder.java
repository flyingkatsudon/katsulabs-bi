package com.bdp.infrastructure.config;

import com.bdp.domain.analytics.DailyKwdTrend;
import com.bdp.domain.metadata.DashboardUser;
import com.bdp.infrastructure.persistence.DailyKwdTrendRepository;
import com.bdp.infrastructure.persistence.DashboardUserRepository;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    @Profile("local")
    CommandLineRunner seedLocalData(
            DashboardUserRepository userRepository,
            DailyKwdTrendRepository trendRepository,
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
            if (trendRepository.count() == 0) {
                seedTrend(trendRepository, LocalDate.now().minusDays(2), "코스피", "상승", 15);
                seedTrend(trendRepository, LocalDate.now().minusDays(1), "코스피", "변동성", 18);
                seedTrend(trendRepository, LocalDate.now(), "금리", "한국은행", 25);
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
