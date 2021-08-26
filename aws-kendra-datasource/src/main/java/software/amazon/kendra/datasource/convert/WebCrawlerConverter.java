package software.amazon.kendra.datasource.convert;

import software.amazon.kendra.datasource.DataSourceConfiguration;
import software.amazon.kendra.datasource.ProxyConfiguration;
import software.amazon.kendra.datasource.WebCrawlerAuthenticationConfiguration;
import software.amazon.kendra.datasource.WebCrawlerBasicAuthentication;
import software.amazon.kendra.datasource.WebCrawlerConfiguration;
import software.amazon.kendra.datasource.WebCrawlerSeedUrlConfiguration;
import software.amazon.kendra.datasource.WebCrawlerSiteMapsConfiguration;
import software.amazon.kendra.datasource.WebCrawlerUrls;


public class WebCrawlerConverter {
    public static software.amazon.awssdk.services.kendra.model.WebCrawlerConfiguration toSdkDataSourceConfiguration(WebCrawlerConfiguration model) {
        if (model == null) {
            return null;
        }
        return software.amazon.awssdk.services.kendra.model.WebCrawlerConfiguration.builder()
            .urls(toSdk(model.getUrls()))
            .crawlDepth(model.getCrawlDepth())
            .maxLinksPerPage(model.getMaxLinksPerPage())
            .maxContentSizePerPageInMegaBytes(NumberConverter.doubleToFloat(model.getMaxContentSizePerPageInMegaBytes()))
            .maxUrlsPerMinuteCrawlRate(model.getMaxUrlsPerMinuteCrawlRate())
            .urlInclusionPatterns(StringListConverter.toSdk(model.getUrlInclusionPatterns()))
            .urlExclusionPatterns(StringListConverter.toSdk(model.getUrlExclusionPatterns()))
            .proxyConfiguration(toSdk(model.getProxyConfiguration()))
            .authenticationConfiguration(toSdk(model.getAuthenticationConfiguration()))
            .build();
    }

    public static DataSourceConfiguration toModelDataSourceConfiguration(
            software.amazon.awssdk.services.kendra.model.WebCrawlerConfiguration webCrawlerConfiguration) {
        return DataSourceConfiguration.builder()
            .webCrawlerConfiguration(toModel(webCrawlerConfiguration))
            .build();
    }

    private static WebCrawlerConfiguration toModel(software.amazon.awssdk.services.kendra.model.WebCrawlerConfiguration sdk) {
        if (sdk == null) {
            return null;
        }
        return WebCrawlerConfiguration.builder()
            .urls(toModel(sdk.urls()))
            .crawlDepth(sdk.crawlDepth())
            .maxLinksPerPage(sdk.maxLinksPerPage())
            .maxContentSizePerPageInMegaBytes(NumberConverter.floatToDouble(sdk.maxContentSizePerPageInMegaBytes()))
            .maxUrlsPerMinuteCrawlRate(sdk.maxUrlsPerMinuteCrawlRate())
            .urlInclusionPatterns(StringListConverter.toModel(sdk.urlInclusionPatterns()))
            .urlExclusionPatterns(StringListConverter.toModel(sdk.urlExclusionPatterns()))
            .proxyConfiguration(toModel(sdk.proxyConfiguration()))
            .authenticationConfiguration(toModel(sdk.authenticationConfiguration()))
            .build();
    }

    static software.amazon.awssdk.services.kendra.model.SeedUrlConfiguration toSdk(WebCrawlerSeedUrlConfiguration model) {
        if (model == null) {
            return null;
        }
        return software.amazon.awssdk.services.kendra.model.SeedUrlConfiguration.builder()
            .seedUrls(StringListConverter.toSdk(model.getSeedUrls()))
            .webCrawlerMode(model.getWebCrawlerMode())
            .build();
    }

    static WebCrawlerSeedUrlConfiguration toModel(
            software.amazon.awssdk.services.kendra.model.SeedUrlConfiguration sdk) {
        if (sdk == null) {
            return null;
        }

        return WebCrawlerSeedUrlConfiguration.builder()
            .seedUrls(StringListConverter.toModel(sdk.seedUrls()))
            .webCrawlerMode(sdk.webCrawlerMode().toString())
            .build();
    }

    static software.amazon.awssdk.services.kendra.model.SiteMapsConfiguration toSdk(WebCrawlerSiteMapsConfiguration model) {
        if (model == null) {
            return null;
        }
        return software.amazon.awssdk.services.kendra.model.SiteMapsConfiguration.builder()
            .siteMaps(StringListConverter.toSdk(model.getSiteMaps()))
            .build();
    }

    static WebCrawlerSiteMapsConfiguration toModel(
            software.amazon.awssdk.services.kendra.model.SiteMapsConfiguration sdk) {
        if (sdk == null) {
            return null;
        }

        return WebCrawlerSiteMapsConfiguration.builder()
            .siteMaps(StringListConverter.toModel(sdk.siteMaps()))
            .build();
    }

    static software.amazon.awssdk.services.kendra.model.Urls toSdk(WebCrawlerUrls model) {
        if (model == null) {
            return null;
        }
        return software.amazon.awssdk.services.kendra.model.Urls.builder()
            .seedUrlConfiguration(toSdk(model.getSeedUrlConfiguration()))
            .siteMapsConfiguration(toSdk(model.getSiteMapsConfiguration()))
            .build();
    }

    static WebCrawlerUrls toModel(
            software.amazon.awssdk.services.kendra.model.Urls sdk) {
        if (sdk == null) {
            return null;
        }

        return WebCrawlerUrls.builder()
            .seedUrlConfiguration(toModel(sdk.seedUrlConfiguration()))
            .siteMapsConfiguration(toModel(sdk.siteMapsConfiguration()))
            .build();
    }

    static software.amazon.awssdk.services.kendra.model.ProxyConfiguration toSdk(ProxyConfiguration model) {
        if (model == null) {
            return null;
        }
        return software.amazon.awssdk.services.kendra.model.ProxyConfiguration.builder()
            .host(model.getHost())
            .port(model.getPort())
            .credentials(model.getCredentials())
            .build();
    }

    static ProxyConfiguration toModel(
            software.amazon.awssdk.services.kendra.model.ProxyConfiguration sdk) {
        if (sdk == null) {
            return null;
        }

        return ProxyConfiguration.builder()
            .host(sdk.host())
            .port(sdk.port())
            .credentials(sdk.credentials())
            .build();
    }

    static software.amazon.awssdk.services.kendra.model.BasicAuthenticationConfiguration toSdkBasciAuthentication(WebCrawlerBasicAuthentication model) {
        if (model == null) {
            return null;
        }
        return software.amazon.awssdk.services.kendra.model.BasicAuthenticationConfiguration.builder()
            .host(model.getHost())
            .port(model.getPort())
            .credentials(model.getCredentials())
            .build();
    }

    static WebCrawlerBasicAuthentication toModelBasciAuthentication(
            software.amazon.awssdk.services.kendra.model.BasicAuthenticationConfiguration sdk) {
        if (sdk == null) {
            return null;
        }

        return WebCrawlerBasicAuthentication.builder()
            .host(sdk.host())
            .port(sdk.port())
            .credentials(sdk.credentials())
            .build();
    }

    static software.amazon.awssdk.services.kendra.model.AuthenticationConfiguration toSdk(WebCrawlerAuthenticationConfiguration model) {
        if (model == null) {
            return null;
        }
        return software.amazon.awssdk.services.kendra.model.AuthenticationConfiguration.builder()
            .basicAuthentication(ListConverter.toSdk(model.getBasicAuthentication(), WebCrawlerConverter::toSdkBasciAuthentication))
            .build();
    }

    static WebCrawlerAuthenticationConfiguration toModel(
            software.amazon.awssdk.services.kendra.model.AuthenticationConfiguration sdk) {
        if (sdk == null) {
            return null;
        }

        return WebCrawlerAuthenticationConfiguration.builder()
            .basicAuthentication(ListConverter.toModel(sdk.basicAuthentication(), WebCrawlerConverter::toModelBasciAuthentication))
            .build();
    }
}
