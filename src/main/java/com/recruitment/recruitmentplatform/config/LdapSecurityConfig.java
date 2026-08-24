package com.recruitment.recruitmentplatform.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.NullLdapAuthoritiesPopulator;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class LdapSecurityConfig {

    @Value("${app.ldap.user-search-base}")
    private String userSearchBase;

    @Value("${app.ldap.user-search-filter}")
    private String userSearchFilter;

    /*
     * ==========================================
     * LDAP CONTEXT SOURCE
     * ==========================================
     */
    @Bean
    @ConditionalOnProperty(name = "app.ldap.enabled", havingValue = "true")
    public LdapContextSource ldapContextSource() {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl("ldap://localhost:8389");
        contextSource.setBase("dc=example,dc=com");
        contextSource.setAnonymousReadOnly(true);
        return contextSource;
    }

    /*
     * ==========================================
     * LDAP AUTHENTICATION PROVIDER
     * ==========================================
     */
    @Bean
    @ConditionalOnProperty(name = "app.ldap.enabled", havingValue = "true")
    public LdapAuthenticationProvider ldapAuthenticationProvider(LdapContextSource contextSource) {

        BindAuthenticator bindAuthenticator = new BindAuthenticator(contextSource);

        FilterBasedLdapUserSearch userSearch = new FilterBasedLdapUserSearch(
                userSearchBase,
                userSearchFilter,
                contextSource
        );
        bindAuthenticator.setUserSearch(userSearch);

        return new LdapAuthenticationProvider(
                bindAuthenticator,
                new NullLdapAuthoritiesPopulator()
        );
    }

    /*
     * ==========================================
     * MYSQL AUTHENTICATION PROVIDER
     * ==========================================
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /*
     * ==========================================
     * AUTHENTICATION MANAGER
     * ==========================================
     */
    @Bean
    public AuthenticationManager authenticationManager(
            DaoAuthenticationProvider daoAuthenticationProvider,
            ObjectProvider<LdapAuthenticationProvider> ldapProviderProvider) {

        List<AuthenticationProvider> providers = new ArrayList<>();


        providers.add(daoAuthenticationProvider);


        LdapAuthenticationProvider ldapProvider = ldapProviderProvider.getIfAvailable();
        if (ldapProvider != null) {
            providers.add(ldapProvider);
        }

        return new ProviderManager(providers);
    }
}