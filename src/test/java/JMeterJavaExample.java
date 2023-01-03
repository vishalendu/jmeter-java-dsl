import org.apache.jmeter.protocol.http.util.HTTPConstants;
import org.junit.jupiter.api.Test;
import us.abstracta.jmeter.javadsl.core.TestPlanStats;

import java.io.IOException;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static us.abstracta.jmeter.javadsl.JmeterDsl.*;


public class JMeterJavaExample {

    @Test
    public void test1() throws Exception{
        TestPlanStats stats = testPlan(
                //threadGroup(1,10,
                threadGroup(1,Duration.ofSeconds(60),
                        httpSampler("Get_Users","http://localhost:3000/users")
                                .header("ancd","hello")
                                .children(
                                        uniformRandomTimer(Duration.ofSeconds(1),Duration.ofSeconds(5)),
                                //jsr223PostProcessor(c->System.out.println(c.prevResponse()))
                                        debugPostProcessor()

                                )
                ),
                jtlWriter("target/jmeter-results/jtls")
                //,resultsTreeVisualizer()
                //,DashboardVisualizer.dashboardVisualizer()
        ).run();
        assertThat(stats.overall().sampleTimePercentile99()).isLessThan(Duration.ofMillis(50));


    }

    @Test
    public void test2() throws IOException {
        TestPlanStats stats = testPlan(
                setupThreadGroup(
                        httpSampler("GET /users","http://localhost:3000/users")
                                .method(HTTPConstants.GET)
                                .children(
                                        jsr223PostProcessor("props.put('MY_TEST_TOKEN', prev.responseDataAsString)")
                                        ,debugPostProcessor()
                                )
                ),
                threadGroup(2, 10,
                        httpSampler("POST /order","http://localhost:3000/order")
                                .method(HTTPConstants.POST)
                                .header("Content-Type","application/json")
                                .header("Accept","application/json")
                                .body("${__P(MY_TEST_TOKEN)}")
                )
                ,jtlWriter("target/jmeter-results/jtls")
//                        .withResponseMessage(true)
//                        .withAllFields()
//                        .saveAsXml(true)
                ,resultsTreeVisualizer()
                ,htmlReporter("target/reports")
        ).run();
        assertThat(stats.overall().sampleTimePercentile99()).isLessThan(Duration.ofSeconds(5));
    }


    @Test
    public void test3() throws IOException {
        String usersIdVarName = "USER_IDS";
        String userIdVarName = "USER_ID";
        String usersPath = "users";
        testPlan(
                httpDefaults().url("http://my.service"),
                threadGroup(1, 1,
                        // httpSampler(usersPath)
                        dummySampler("[{\"id\": 1, \"name\": \"John\"}, {\"id\": 2, \"name\": \"Jane\"}]")
                                .url("http://my.service/")
                                .children(
                                        jsonExtractor(usersIdVarName, "[].id")
                                                .matchNumber(-1)
                                ),
                        forEachController(usersIdVarName, userIdVarName,
                                // httpSampler(usersPath + "/${" + userIdVarName + "}")
                                dummySampler("{\"name\": \"John or Jane\"}")
                                        .url("http://my.service/" + usersPath + "/${" + userIdVarName + "}")
                        )
                ),
                resultsTreeVisualizer()
                ,jtlWriter("target/jmeter-results/jtls")
//                ,influxDbListener("http://localhost:8086/write?db=jmeter")
        ).run();
    }

    public static void main(String args[]){

    }

}
