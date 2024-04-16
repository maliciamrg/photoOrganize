package malicia.mrg.photo.organize.application.config;

import malicia.mrg.photo.organize.domain.HexMeImpl;
import malicia.mrg.photo.organize.domain.ddd.DomainService;
import malicia.mrg.photo.organize.domain.ddd.Stub;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(
        basePackages = {"malicia.mrg.photo.organize"},
        includeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {DomainService.class, Stub.class})})
public class DomainConfiguration {
}
