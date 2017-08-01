package com.huizhi.crawling.truck;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class TestProcessor implements PageProcessor {
	
	public static final String TRUCK_PRODUCT_INFO_LIST		= "https://product\\.360che\\.com/img/c1_\\w+\\.html";//货车车型明细信息

    private Site site = Site
            .me()
            .setDomain("product.360che.com")
            .setSleepTime(1500)
            .setRetryTimes(3)
            .setUserAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

	public void process(Page page) {
		String url=page.getUrl().toString();
		System.out.println("current url="+url);
		
        //货车车型页
        if(page.getUrl().toString().equals("https://img4.kcimg.cn/imgc/0/495/495192.jpg!800")){
        	/*page.addTargetRequests(page.getHtml().xpath("//dd[@class=\"pr_dd1\"]").links().regex(TRUCK_PRODUCT_INFO_LIST).all());
        	page.addTargetRequests(page.getHtml().xpath("//div[@class=\"cpmch\"]").links().regex(TRUCK_PRODUCT_INFO_LIST).all());
			System.out.println("根据货车产品状态信息，获取到的货车产品列表如下...");
			for(Request req:page.getTargetRequests()){
				System.out.println(req.getUrl().toString());
			}
			return;
			*/
        	
        	byte[] bs=page.getBytes();
        	OutputStream os;
			try {
				os = new FileOutputStream("/data/a.jpg");
				os.write(bs, 0, bs.length); 
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	
        }
	}

	public Site getSite() {
		return site;
	}

	public static void main(String[] args) {
		Spider.create(new TestProcessor()).addUrl("https://img4.kcimg.cn/imgc/0/495/495192.jpg!800").run();
	}

}
