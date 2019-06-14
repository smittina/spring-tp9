package com.training.spring.bigcorp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;


/**
 * Configure la sécurité de l'application
 */
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    /**
     * Role Admin
     */
    public final static String ROLE_ADMIN = "ROLE_ADMIN";

    @Configuration
    protected static class ApplicationSecurity extends WebSecurityConfigurerAdapter {

        @Autowired
        private DataSource dataSource;

        @Override
        protected void configure(HttpSecurity http) throws Exception{
            http.authorizeRequests()    //On sécurise toutes les requêtes entrantes
                .antMatchers("/css/**", "/img/**").permitAll()  //Ressources web en libre accès
                .antMatchers("/**/create/").hasAuthority(ROLE_ADMIN)
                .anyRequest().authenticated()
                .and().formLogin()
                    .loginPage("/formLogin")
                    .loginProcessingUrl("/login")
                    .usernameParameter("username")
                    .passwordParameter("password").permitAll()
                .and().logout()
                    .invalidateHttpSession(true)    // user non déconnecté si refresh
                .and().headers().frameOptions().disable()
                .and().csrf().ignoringAntMatchers("/console/**");     // on désactive pour le moment la protection crsf

        }

        @Override
        protected void configure(AuthenticationManagerBuilder builder) throws Exception{
            builder.jdbcAuthentication()
                    .passwordEncoder(bCryptPasswordEncoder())
                    .dataSource(dataSource);
        }

        @Bean
        public BCryptPasswordEncoder bCryptPasswordEncoder(){
            return new BCryptPasswordEncoder();
        }

        @Bean
        public JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource) {
            JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager();
            jdbcUserDetailsManager.setDataSource(dataSource);
            jdbcUserDetailsManager.createUser(User.builder().username("user").password(
                    bCryptPasswordEncoder().encode("password")).roles("USER").build());
            jdbcUserDetailsManager.createUser(User.builder().username("admin").password(
                    bCryptPasswordEncoder().encode("password")).roles("ADMIN").build());
            return jdbcUserDetailsManager;
        }
    }

    @Configuration
    protected static class SecurityWebMvcConfigurer implements WebMvcConfigurer{

        public void addViewControllers(ViewControllerRegistry registry){
            /*
                On indique à Spring à quoi correspond /formLogin pour login.mustache
                et / pour index.mustache

                On aurait pu le faire via un controller mais c'est plus simple ainsi
             */
            registry.addViewController("/").setViewName("index");
            registry.addViewController("/formLogin").setViewName("login");
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry){
            registry.addInterceptor(securityInfoInterceptor());
        }
    }

    @Bean
    public static HandlerInterceptorAdapter securityInfoInterceptor(){
        return new HandlerInterceptorAdapter() {
            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
                if(modelAndView != null){
                    // Est-ce qu'on est admin ou non
                    modelAndView.addObject("admin",request.isUserInRole("ADMIN"));
                    //Est-ce qu'on est loggué ou non
                    modelAndView.addObject("logged", request.getUserPrincipal() != null);
                    modelAndView.addObject("_csrf", request.getAttribute("_csrf"));
                    // Gestion des erreurs
                    if(request.getRequestURL() != null && request.getQueryString() != null){
                        modelAndView.addObject("error", request.getRequestURI().equals("/formLogin") &&
                                request.getQueryString().contains("error"));
                    }
                }
            }
        };
    }
}
