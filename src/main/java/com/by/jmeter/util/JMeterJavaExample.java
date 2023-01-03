package com.by.jmeter.util;

import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;
import static us.abstracta.jmeter.javadsl.JmeterDsl.*;
import static us.abstracta.jmeter.javadsl.dashboard.DashboardVisualizer.*;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import us.abstracta.jmeter.javadsl.core.TestPlanStats;
import us.abstracta.jmeter.javadsl.dashboard.DashboardVisualizer;


public class JMeterJavaExample {

    @Test
    public void test() throws Exception{
        TestPlanStats stats = testPlan(
                threadGroup(1,10,
                        httpSampler("http://localhost:3000/users")
                                .header("ancd","hello")
                                .children(
                                        uniformRandomTimer(Duration.ofSeconds(1),Duration.ofSeconds(5))
                                )
                ),
                jtlWriter("target/jmeter-results/jtls"),
                //resultsTreeVisualizer()
                DashboardVisualizer.dashboardVisualizer()
        ).run();
        assertThat(stats.overall().sampleTimePercentile99()).isLessThan(Duration.ofMillis(50));

    }


    public static void main(String args[]){

    }

}
