# spring-application-run

1. `SpringApplication#applyInitializers()` -> `SpringApplication#listeners`
```
0: org.springframework.boot.context.config.DelegatingApplicationContextInitializer
1: org.springframework.boot.autoconfigure.SharedMetadataReaderFactoryContextInitializer
2: org.springframework.boot.context.ContextIdApplicationContextInitializer
3: org.springframework.boot.context.ConfigurationWarningsApplicationContextInitializer
4: org.springframework.boot.rsocket.context.RSocketPortInfoApplicationContextInitializer
5: org.springframework.boot.web.context.ServerPortInfoApplicationContextInitializer
6: org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportLoggingListener
```

