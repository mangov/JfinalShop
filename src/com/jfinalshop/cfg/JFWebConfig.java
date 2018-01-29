package com.jfinalshop.cfg;

import org.beetl.ext.jfinal.BeetlRenderFactory;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.ext.plugin.shiro.ShiroPlugin;
import com.jfinal.ext.plugin.tablebind.AutoTableBindPlugin;
import com.jfinal.ext.plugin.tablebind.SimpleNameStyles;
import com.jfinal.ext.route.AutoBindRoutes;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinalshop.handler.JDruidStatViewHandler;
import com.jfinalshop.util.SerialNumberUtil;

public class JFWebConfig extends JFinalConfig {

    /**
     * 供Shiro插件使用。
     */
    Routes routes;

    @Override
    public void configConstant(Constants me) {
        //SqlReporter.setLogger(true);
        me.setErrorView(401, "401.html");
        me.setErrorView(403, "403.html");
        me.setError404View("404.html");
        me.setError500View("500.html");

        // 加载数据库配置文件
        loadPropertyFile("jdbc.properties");
        // 设定Beetl
        me.setMainRenderFactory(new BeetlRenderFactory());
        // 设定为开发者模式
        PropKit.use("appconfig.properties");
        me.setDevMode(PropKit.getBoolean("devMode", false));
    }

    @Override
    public void configRoute(Routes me) {
        this.routes = me;
        me.add(new AutoBindRoutes());
    }

    private StatFilter getStatFilter() {
        StatFilter statFilter = new StatFilter();
        statFilter.setLogSlowSql(true);
        statFilter.setMergeSql(true);
        return statFilter;
    }

    @Override
    public void configPlugin(Plugins me) {
        // mysql
        String url = getProperty("jdbcUrl");
        String username = getProperty("user");
        String password = getProperty("password");
        String driverClass = getProperty("driverClass");

        // mysql 数据源
        DruidPlugin dsMysql = new DruidPlugin(url, username, password, driverClass);
        dsMysql.addFilter(new WallFilter());//配置防御SQL注入攻击
        dsMysql.addFilter(getStatFilter());//打开Druid的监控统计功能
        dsMysql.setMaxActive(200);
        me.add(dsMysql);

        ActiveRecordPlugin arpMysql = new ActiveRecordPlugin("mysql", dsMysql);
        me.add(arpMysql);

        AutoTableBindPlugin atbp = new AutoTableBindPlugin(dsMysql, SimpleNameStyles.LOWER);
        atbp.setShowSql(true);
        atbp.setDialect(new MysqlDialect());// 配置MySql方言
        me.add(atbp);

        //加载Shiro插件
        me.add(new ShiroPlugin(routes));
    }

    @Override
    public void configInterceptor(Interceptors me) {
    }

    @Override
    public void configHandler(Handlers me) {
        JDruidStatViewHandler viewHandler = new JDruidStatViewHandler("/druid");
        me.add(viewHandler);
    }

    public void afterJFinalStart() {
        SerialNumberUtil.lastSnNumberInit();
    }

}
