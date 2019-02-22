package com.dyc.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;


@Component
public class AccessFilter extends ZuulFilter {

    private static Logger LOGGER = LoggerFactory.getLogger(AccessFilter.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public String filterType() {
        //前置过滤器
        return "pre";
    }

    @Override
    public int filterOrder() {
        //优先级，数字越大，优先级越低
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        //是否执行该过滤器，true代表需要过滤
        return true;
    }

    @Override
    public Object run() {

        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String requesUrl = request.getRequestURL().toString();
        LOGGER.info("send {} request to {}", request.getMethod(), requesUrl);
        //登录接口放行
        if (requesUrl.endsWith("auth/login")) {
            LOGGER.info("alow login api");
            return null;
        }

        //获取传来的参数accessToken
        Object jwtToken = request.getParameter("accessToken");
        if (jwtToken == null) {
            LOGGER.warn("access token is empty");
            //过滤该请求，不往下级服务去转发请求，到此结束
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(401);
            ctx.setResponseBody("{\"result\":\"accessToken为空!\"}");
            ctx.getResponse().setContentType("text/html;charset=UTF-8");
            return null;
        }

        String cacheToken = (String)redisTemplate.opsForValue().get("jwtToken");
        if(StringUtils.isBlank(cacheToken)){
            LOGGER.warn("access token is expired");
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(401);
            ctx.setResponseBody("{\"result\":\"accessToken已过期!\"}");
            ctx.getResponse().setContentType("text/html;charset=UTF-8");
            return null;
        }

        //如果有token，则进行路由转发
        LOGGER.info("access token ok");
        //这里return的值没有意义，zuul框架没有使用该返回值
        return null;
    }

}