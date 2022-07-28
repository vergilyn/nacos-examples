package com.vergilyn.examples.nacos;

import com.vergilyn.examples.nacos.autorefresh.SpringbootAutoRefreshApplication;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author vergilyn
 * @since 2021-12-10
 */
@SpringBootTest(classes = SpringbootAutoRefreshApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractSpringbootAutoRefreshApplication {
}
