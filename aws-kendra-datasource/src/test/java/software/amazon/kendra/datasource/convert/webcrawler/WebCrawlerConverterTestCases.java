package software.amazon.kendra.datasource.convert.webcrawler;

import software.amazon.awssdk.services.kendra.model.AuthenticationConfiguration;
import software.amazon.awssdk.services.kendra.model.BasicAuthenticationConfiguration;
import software.amazon.awssdk.services.kendra.model.ProxyConfiguration;
import software.amazon.awssdk.services.kendra.model.SeedUrlConfiguration;
import software.amazon.awssdk.services.kendra.model.SiteMapsConfiguration;
import software.amazon.awssdk.services.kendra.model.Urls;
import software.amazon.awssdk.services.kendra.model.WebCrawlerConfiguration;
import software.amazon.kendra.datasource.WebCrawlerSeedUrlConfiguration;
import software.amazon.kendra.datasource.WebCrawlerUrls;

import java.util.Arrays;

import static software.amazon.kendra.datasource.convert.webcrawler.WebCrawlerConverterTestCaseConstants.COLLECTION_EMPTY;
import static software.amazon.kendra.datasource.convert.webcrawler.WebCrawlerConverterTestCaseConstants.CRAWL_DEPTH;
import static software.amazon.kendra.datasource.convert.webcrawler.WebCrawlerConverterTestCaseConstants.CRAWL_MODE;
import static software.amazon.kendra.datasource.convert.webcrawler.WebCrawlerConverterTestCaseConstants.CREDENTIAL_1;
import static software.amazon.kendra.datasource.convert.webcrawler.WebCrawlerConverterTestCaseConstants.CREDENTIAL_2;
import static software.amazon.kendra.datasource.convert.webcrawler.WebCrawlerConverterTestCaseConstants.EMPTY_MODEL_AUTHENTICATION_CONFIGURATION;
import static software.amazon.kendra.datasource.convert.webcrawler.WebCrawlerConverterTestCaseConstants.EMPTY_SDK_AUTHENTICATION_CONFIGURATION;
import static software.amazon.kendra.datasource.convert.webcrawler.WebCrawlerConverterTestCaseConstants.HOST_1;
import static software.amazon.kendra.datasource.convert.webcrawler.WebCrawlerConverterTestCaseConstants.HOST_2;
import static software.amazon.kendra.datasource.convert.webcrawler.WebCrawlerConverterTestCaseConstants.MAX_CONTENT_SITE_PER_PAGE_IN_MEGABYTES;
import static software.amazon.kendra.datasource.convert.webcrawler.WebCrawlerConverterTestCaseConstants.MAX_LINKS_PER_PAGE;
import static software.amazon.kendra.datasource.convert.webcrawler.WebCrawlerConverterTestCaseConstants.MAX_URLS_PER_MINUTE;
import static software.amazon.kendra.datasource.convert.webcrawler.WebCrawlerConverterTestCaseConstants.PORT_1;
import static software.amazon.kendra.datasource.convert.webcrawler.WebCrawlerConverterTestCaseConstants.PORT_2;
import static software.amazon.kendra.datasource.convert.webcrawler.WebCrawlerConverterTestCaseConstants.PROXY_CREDENTIAL;
import static software.amazon.kendra.datasource.convert.webcrawler.WebCrawlerConverterTestCaseConstants.PROXY_HOST;
import static software.amazon.kendra.datasource.convert.webcrawler.WebCrawlerConverterTestCaseConstants.PROXY_PORT;
import static software.amazon.kendra.datasource.convert.webcrawler.WebCrawlerConverterTestCaseConstants.SEED_URLS;
import static software.amazon.kendra.datasource.convert.webcrawler.WebCrawlerConverterTestCaseConstants.SITE_MAP;
import static software.amazon.kendra.datasource.convert.webcrawler.WebCrawlerConverterTestCaseConstants.URL_EXCLUSION_PATTERNS;
import static software.amazon.kendra.datasource.convert.webcrawler.WebCrawlerConverterTestCaseConstants.URL_INCLUSION_PATTERNS;

public class WebCrawlerConverterTestCases {

    static final WebCrawlerConverterTestCase[] TEST_CASES = {
        WebCrawlerConverterTestCase.builder()
            .description("Null")
            .input(new WebCrawlerConverterTestCase.SdkData(null))
            .expectedOutput(new WebCrawlerConverterTestCase.ModelData(null))
            .build(),
        WebCrawlerConverterTestCase.builder()
            .description("Null fields")
            .input(new WebCrawlerConverterTestCase.SdkData(
                WebCrawlerConfiguration.builder()
                    .build()
            ))
            .expectedOutput(new WebCrawlerConverterTestCase.ModelData(
                software.amazon.kendra.datasource.WebCrawlerConfiguration.builder()
                    .build()
            ))
            .build(),
        WebCrawlerConverterTestCase.builder()
            .description("Minimum Configuration")
            .input(new WebCrawlerConverterTestCase.SdkData(
                WebCrawlerConfiguration.builder()
                    .urls(Urls.builder()
                        .seedUrlConfiguration(
                            SeedUrlConfiguration.builder()
                                .seedUrls(SEED_URLS)
                                .webCrawlerMode(CRAWL_MODE)
                                .build()
                        )
                        .siteMapsConfiguration(
                            SiteMapsConfiguration.builder()
                                .siteMaps(SITE_MAP).build()
                        )
                        .build())
                    .build()
                ))
                .expectedOutput(new WebCrawlerConverterTestCase.ModelData(
                    software.amazon.kendra.datasource.WebCrawlerConfiguration.builder()
                        .urls(software.amazon.kendra.datasource.WebCrawlerUrls.builder()
                            .seedUrlConfiguration(software.amazon.kendra.datasource.WebCrawlerSeedUrlConfiguration.builder()
                                .seedUrls(SEED_URLS)
                                .webCrawlerMode(CRAWL_MODE)
                                .build())
                            .siteMapsConfiguration(software.amazon.kendra.datasource.WebCrawlerSiteMapsConfiguration.builder()
                                .siteMaps(SITE_MAP).build())
                            .build())
                        .build()
                ))
            .build(),
        WebCrawlerConverterTestCase.builder()
            .description("Full configuration")
            .input(new WebCrawlerConverterTestCase.SdkData(
                WebCrawlerConfiguration.builder()
                    .urls(Urls.builder()
                        .seedUrlConfiguration(
                            SeedUrlConfiguration.builder()
                                .seedUrls(SEED_URLS)
                                .webCrawlerMode(CRAWL_MODE)
                                .build()
                        )
                        .siteMapsConfiguration(
                            SiteMapsConfiguration.builder()
                                .siteMaps(SITE_MAP).build()
                        )
                        .build())
                    .crawlDepth(CRAWL_DEPTH)
                    .maxLinksPerPage(MAX_LINKS_PER_PAGE)
                    .maxContentSizePerPageInMegaBytes(MAX_CONTENT_SITE_PER_PAGE_IN_MEGABYTES.floatValue())
                    .maxUrlsPerMinuteCrawlRate(MAX_URLS_PER_MINUTE)
                    .urlInclusionPatterns(URL_INCLUSION_PATTERNS)
                    .urlExclusionPatterns(URL_EXCLUSION_PATTERNS)
                    .proxyConfiguration(ProxyConfiguration.builder()
                        .host(PROXY_HOST)
                        .port(PROXY_PORT)
                        .credentials(PROXY_CREDENTIAL)
                        .build())
                    .authenticationConfiguration(AuthenticationConfiguration.builder()
                        .basicAuthentication(Arrays.asList(
                            BasicAuthenticationConfiguration.builder()
                                .host(HOST_1)
                                .port(PORT_1)
                                .credentials(CREDENTIAL_1)
                                .build(),
                            BasicAuthenticationConfiguration.builder()
                                .host(HOST_2)
                                .port(PORT_2)
                                .credentials(CREDENTIAL_2)
                                .build()))
                        .build())
                    .build()
            ))
            .expectedOutput(new WebCrawlerConverterTestCase.ModelData(
                software.amazon.kendra.datasource.WebCrawlerConfiguration.builder()
                    .urls(software.amazon.kendra.datasource.WebCrawlerUrls.builder()
                        .seedUrlConfiguration(
                            software.amazon.kendra.datasource.WebCrawlerSeedUrlConfiguration.builder()
                                .seedUrls(SEED_URLS)
                                .webCrawlerMode(CRAWL_MODE)
                                .build())
                        .siteMapsConfiguration(
                            software.amazon.kendra.datasource.WebCrawlerSiteMapsConfiguration.builder()
                                .siteMaps(SITE_MAP).build())
                        .build())
                    .crawlDepth(CRAWL_DEPTH)
                    .maxLinksPerPage(MAX_LINKS_PER_PAGE)
                    .maxContentSizePerPageInMegaBytes(MAX_CONTENT_SITE_PER_PAGE_IN_MEGABYTES)
                    .maxUrlsPerMinuteCrawlRate(MAX_URLS_PER_MINUTE)
                    .urlInclusionPatterns(URL_INCLUSION_PATTERNS)
                    .urlExclusionPatterns(URL_EXCLUSION_PATTERNS)
                    .proxyConfiguration(
                        software.amazon.kendra.datasource.ProxyConfiguration.builder()
                            .host(PROXY_HOST)
                            .port(PROXY_PORT)
                            .credentials(PROXY_CREDENTIAL)
                            .build()
                    )
                    .authenticationConfiguration(
                        software.amazon.kendra.datasource.WebCrawlerAuthenticationConfiguration.builder()
                            .basicAuthentication(Arrays.asList(
                                software.amazon.kendra.datasource.WebCrawlerBasicAuthentication.builder()
                                    .host(HOST_1)
                                    .port(PORT_1)
                                    .credentials(CREDENTIAL_1)
                                    .build(),
                                software.amazon.kendra.datasource.WebCrawlerBasicAuthentication.builder()
                                    .host(HOST_2)
                                    .port(PORT_2)
                                    .credentials(CREDENTIAL_2)
                                    .build()))
                            .build())
                    .build()
            ))
            .build(),

        WebCrawlerConverterTestCase.builder()
            .description("Empty sdk list -> converted to null field")
            .isSymmetrical(false)
            .input(new WebCrawlerConverterTestCase.SdkData(
                WebCrawlerConfiguration.builder()
                    .urls(Urls.builder()
                        .seedUrlConfiguration(
                            SeedUrlConfiguration.builder()
                                .seedUrls(COLLECTION_EMPTY)
                                .webCrawlerMode(CRAWL_MODE)
                                .build()
                        )
                        .siteMapsConfiguration(
                            SiteMapsConfiguration.builder()
                                .siteMaps(COLLECTION_EMPTY).build()
                        )
                        .build())
                    .urlInclusionPatterns(COLLECTION_EMPTY)
                    .urlExclusionPatterns(COLLECTION_EMPTY)
                    .authenticationConfiguration(
                        AuthenticationConfiguration.builder()
                            .basicAuthentication(EMPTY_SDK_AUTHENTICATION_CONFIGURATION)
                            .build()
                    )
                    .build()
            ))
            .expectedOutput(new WebCrawlerConverterTestCase.ModelData(
                software.amazon.kendra.datasource.WebCrawlerConfiguration.builder()
                    .urls(software.amazon.kendra.datasource.WebCrawlerUrls.builder()
                        .seedUrlConfiguration(
                            software.amazon.kendra.datasource.WebCrawlerSeedUrlConfiguration.builder()
                                .webCrawlerMode(CRAWL_MODE)
                                .build())
                        .siteMapsConfiguration(
                            software.amazon.kendra.datasource.WebCrawlerSiteMapsConfiguration.builder()
                                .build())
                        .build())
                    .authenticationConfiguration(
                        software.amazon.kendra.datasource.WebCrawlerAuthenticationConfiguration.builder()
                            .build())
                .build()
            ))
            .build(),

        WebCrawlerConverterTestCase.builder()
            .description("Empty model list -> converted to null field")
            .isSymmetrical(false)
            .input(new WebCrawlerConverterTestCase.ModelData(
                software.amazon.kendra.datasource.WebCrawlerConfiguration.builder()
                    .urls(software.amazon.kendra.datasource.WebCrawlerUrls.builder()
                        .seedUrlConfiguration(
                            software.amazon.kendra.datasource.WebCrawlerSeedUrlConfiguration.builder()
                                .seedUrls(COLLECTION_EMPTY)
                                .webCrawlerMode(CRAWL_MODE)
                                .build())
                        .siteMapsConfiguration(
                            software.amazon.kendra.datasource.WebCrawlerSiteMapsConfiguration.builder()
                                .siteMaps(COLLECTION_EMPTY)
                                .build())
                            .build())
                    .urlInclusionPatterns(COLLECTION_EMPTY)
                    .urlExclusionPatterns(COLLECTION_EMPTY)
                    .authenticationConfiguration(
                        software.amazon.kendra.datasource.WebCrawlerAuthenticationConfiguration.builder()
                            .basicAuthentication(EMPTY_MODEL_AUTHENTICATION_CONFIGURATION)
                            .build())
                .build()))
            .expectedOutput(new WebCrawlerConverterTestCase.SdkData(
                WebCrawlerConfiguration.builder()
                    .urls(Urls.builder()
                        .seedUrlConfiguration(
                            SeedUrlConfiguration.builder()
                                .seedUrls(COLLECTION_EMPTY)
                                .webCrawlerMode(CRAWL_MODE)
                                .build()
                        )
                        .siteMapsConfiguration(
                            SiteMapsConfiguration.builder()
                                .siteMaps(COLLECTION_EMPTY)
                                .build()
                        )
                        .build())
                    .urlInclusionPatterns(COLLECTION_EMPTY)
                    .urlExclusionPatterns(COLLECTION_EMPTY)
                    .authenticationConfiguration(
                        AuthenticationConfiguration.builder()
                            .basicAuthentication(EMPTY_SDK_AUTHENTICATION_CONFIGURATION)
                            .build())
                    .build()
            ))
            .build(),

        WebCrawlerConverterTestCase.builder()
            .description("Failing test case - invalid")
            .isSymmetrical(false)
            .input(new WebCrawlerConverterTestCase.ModelData(
                software.amazon.kendra.datasource.WebCrawlerConfiguration.builder()
                    .crawlDepth(10)
                    .urls(WebCrawlerUrls.builder()
                        .seedUrlConfiguration(WebCrawlerSeedUrlConfiguration.builder()
                            .seedUrls(Arrays.asList("https://www.amazon.com"))
                            .webCrawlerMode("This is some value that isn't valid by our API but our converter will handle")
                            .build())
                        .build())
                    .build()
            ))
            .expectedOutput(new WebCrawlerConverterTestCase.SdkData(
                WebCrawlerConfiguration.builder()
                    .crawlDepth(10)
                    .urls(Urls.builder()
                        .seedUrlConfiguration(SeedUrlConfiguration.builder()
                            .seedUrls("https://www.amazon.com")
                            .webCrawlerMode("This is some value that isn't valid by our API but our converter will handle")
                            .build())
                        .build())
                    .build()
            ))
            .build(),

        WebCrawlerConverterTestCase.builder()
            .description("Failing test case - null web crawler mode")
            .isSymmetrical(false)
            .input(new WebCrawlerConverterTestCase.ModelData(
                software.amazon.kendra.datasource.WebCrawlerConfiguration.builder()
                    .crawlDepth(10)
                    .urls(WebCrawlerUrls.builder()
                        .seedUrlConfiguration(WebCrawlerSeedUrlConfiguration.builder()
                            .seedUrls(Arrays.asList("https://www.amazon.com"))
                            .webCrawlerMode(null)
                            .build())
                        .build())
                    .build()
            ))
            .expectedOutput(new WebCrawlerConverterTestCase.SdkData(
                WebCrawlerConfiguration.builder()
                    .crawlDepth(10)
                    .urls(Urls.builder()
                        .seedUrlConfiguration(SeedUrlConfiguration.builder()
                            .seedUrls("https://www.amazon.com")
                            .webCrawlerMode((String) null)
                            .build())
                        .build())
                    .build()
            ))
            .build(),
    };

    private WebCrawlerConverterTestCases() {
        // utility class
    }
}
