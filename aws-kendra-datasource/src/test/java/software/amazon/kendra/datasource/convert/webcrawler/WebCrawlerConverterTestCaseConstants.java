package software.amazon.kendra.datasource.convert.webcrawler;

import software.amazon.awssdk.services.kendra.model.BasicAuthenticationConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WebCrawlerConverterTestCaseConstants {
    static final List<String> SEED_URLS = Arrays.asList("https://url.abc", "https://url.xyz");
    static final List<String> SITE_MAP = Arrays.asList("https://site.abc", "https://site.xyz");
    static final String CRAWL_MODE = "EVERYTHING";

    static final String PROXY_HOST = "https://gtw.abc";
    static final int PROXY_PORT = 34343;
    static final String PROXY_CREDENTIAL = "PROXY_SECRET_ARN";

    static final String HOST_1 = "https://host.xyz";
    static final int PORT_1 = 8080;
    static final String CREDENTIAL_1 = "SECRET_ARN_1";

    static final String HOST_2 = "https://host.abc";
    static final int PORT_2 = 123;
    static final String CREDENTIAL_2 = "SECRET_ARN_2";

    static final int CRAWL_DEPTH = 5;
    static final int MAX_LINKS_PER_PAGE = 3;
    static final Double MAX_CONTENT_SITE_PER_PAGE_IN_MEGABYTES = 5.0;
    static final int MAX_URLS_PER_MINUTE = 5;

    static final List<String> COLLECTION_EMPTY = Collections.emptyList();
    static final List<BasicAuthenticationConfiguration> EMPTY_SDK_AUTHENTICATION_CONFIGURATION = Collections.emptyList();
    static final List<software.amazon.kendra.datasource.WebCrawlerBasicAuthentication> EMPTY_MODEL_AUTHENTICATION_CONFIGURATION
            = Collections.emptyList();
    static final List<String> URL_INCLUSION_PATTERNS = Arrays.asList("INC1", "INC2", "INC3");
    static final List<String> URL_EXCLUSION_PATTERNS = Arrays.asList("EXC1", "EXC2", "EXC3");
}
