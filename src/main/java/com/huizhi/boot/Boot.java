package com.huizhi.boot;

import com.huizhi.crawling.truck.TruckProcessor;

import us.codecraft.webmagic.Spider;

public class Boot {

	/**
	 * 该程序取网站的第一个车型没问题，但是没有经过更多的测试，要测试的地方包括：more按钮是否有，列表信息是否有
	 * @param args
	 */
	public static void main(String[] args) {
        Spider.create(new TruckProcessor()).addUrl("https://product.360che.com/pic/").run();
        //Spider.create(new TruckProcessor()).addUrl("https://product.360che.com/img/c1_s64_b1163_s6715_m25880_t0.html").run();//测试报错
        
	}

}
