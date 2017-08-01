package com.huizhi.crawling.truck;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.utils.FilePersistentBase;

public class TruckProcessor implements PageProcessor {
	
    public static final String TRUCK_TYPE_LIST 		= "https://product\\.360che\\.com/img/c1_s\\d+\\_b0_s0.html";			//货车类型
    public static final String TRUCK_BRAND_LIST 	= "https://product\\.360che\\.com/img/c1_s\\d+\\_b\\d+\\_s0.html";		//货车品牌
    public static final String TRUCK_SERIES_LIST 	= "https://product\\.360che\\.com/img/c1_s\\d+\\_b\\d+\\_s\\d+\\.html";	//货车系列
    public static final String TRUCK_PRODUCT_STATUS_LIST 	= "https://product\\.360che\\.com/img/c1_s\\d+\\_b\\d+\\_s\\d+\\_p\\d+\\_s\\d+\\.html";	//货车车型状态，在售，停售
    public static final String TRUCK_PRODUCT_LIST		= "https://product\\.360che\\.com/img/c1_s\\d+\\_b\\d+\\_s\\d+\\_m\\d+\\_t\\d+\\.html";		//货车车型明细信息
    //public static final String TRUCK_PRODUCT_INFO_LIST		= "https://product\\.360che\\.com/img/c1_\\w+\\.html";	//货车车型明细信息
    public static final String TRUCK_PRODUCT_INDEX		="https://product\\.360che\\.com/\\w+\\/\\w+\\_index.html";		//货车车型首页
    public static final String TRUCK_PRODUCT_PIC_LIST	="https://product\\.360che\\.com/img/c1_s\\d+\\_b\\d+\\_s\\d+\\_m\\d+\\_t\\d+\\.html";//货车图片库（从车型首页那里得到的图片库，实际上和货车车型明细信息一样，相对于从图片库又反回到了车型明细页面
    public static final String TRUCK_PRODUCT_PIC_MORE_LIST	="https://product\\.360che\\.com/img/c1_s\\d+\\_b\\d+\\_s\\d+\\_m\\d+\\_t\\d+\\.html";//单击MORE按钮之后进入的货车图片页面
    public static final String TRUCK_PRODUCT_PIC_DOWNLOAD_LIST="https://img\\d+\\.kcimg\\.cn/imgc/\\d+\\/\\d+\\/\\d+\\.jpg!";
    
    public static final String STATUS="test";
    
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
		Object after_truck_index= page.getRequest().getExtra("after_truck_index");
		Object after_truck_more=page.getRequest().getExtra("after_truck_more");
		
		if(url.equals("https://product.360che.com/pic/")){
			
			System.out.println("起始页，开始获取货车类型信息...");
			/*
			for(String link:page.getHtml().links().all()){
				System.out.println("link="+link);
			}
			*/
			//page.addTargetRequests(page.getHtml().links().regex(TRUCK_TYPE_LIST).all());//这种方法会有重复的URL被写入
			Selectable links= page.getHtml().xpath("//div[@class=\"cpmch\"]//div[@class=\"pro_big_b\"]//a");
			for(Selectable link:links.nodes()){
				String href=link.xpath("/a/@href").regex(TRUCK_TYPE_LIST).toString();
				String text=link.xpath("/a/text()").toString();
				
				if(href!=null){
					Request request=new Request(href);
					request.putExtra("truck_type", text);
					page.addTargetRequest(request);					
				}
				
//				if(TruckProcessor.STATUS.equals("test")){
//					break;//切记，正式版本要删掉，为了减少请求，去掉了很多request
//				}
				
			}
			
			//page.addTargetRequests(page.getHtml().xpath("//div[@class=\"cpmch\"]").links().regex(TRUCK_TYPE_LIST).all());
			
			System.out.println("根据起始页信息，获取到的货车类型列表如下...");
			for(Request req:page.getTargetRequests()){
				System.out.println(req.getUrl().toString()+","+req.getExtra("truck_type"));
			}
			return;
		}
        //货车类型页
        if (page.getUrl().regex(TRUCK_TYPE_LIST).match()) {
        	
            //page.addTargetRequests(page.getHtml().xpath("//div[@class=\"pplb1newshow\"]").links().regex(TRUCK_BRAND_LIST).all());
        	
        	Selectable links= page.getHtml().xpath("//div[@class=\"pplb1newshow\"]//div[@class=\"group1\"]//div[@class=\" newtree\"]//a");
        	
			for(Selectable link:links.nodes()){
				String href=link.xpath("/a/@href").regex(TRUCK_BRAND_LIST).toString();
				String text=link.xpath("/a/text()").toString();
				
				if(href!=null){
					Request request=new Request(href);
					request.putExtra("truck_type", page.getRequest().getExtra("truck_type"));
					request.putExtra("truck_brand", text);
					page.addTargetRequest(request);					
				}
				
				if(TruckProcessor.STATUS.equals("test")){
					break;//切记，正式版本要删掉，为了减少请求，去掉了很多request
				}
			}
            
			System.out.println("根据货车类型信息，获取到的货车品牌列表如下...");
			for(Request req:page.getTargetRequests()){
				System.out.println(req.getUrl().toString()+","+req.getExtra("truck_type")+","+req.getExtra("truck_brand"));
			}
			return;
        } 
        //货车品牌页
        if (page.getUrl().regex(TRUCK_BRAND_LIST).match()) {
            //page.addTargetRequests(page.getHtml().xpath("//dl[@class=\"newshow\"]").links().regex(TRUCK_SERIES_LIST).all());
        	Selectable links= page.getHtml().xpath("//dl[@class=\"newshow\"]//dd//a");
        	
			for(Selectable link:links.nodes()){
				String href=link.xpath("/a/@href").regex(TRUCK_SERIES_LIST).toString();
				String text=link.xpath("/a/text()").toString();
				
				if(href!=null){
					Request request=new Request(href);
					request.putExtra("truck_type", page.getRequest().getExtra("truck_type"));
					request.putExtra("truck_brand", page.getRequest().getExtra("truck_brand"));
					request.putExtra("truck_series", text);
					page.addTargetRequest(request);					
				}
				
				if(TruckProcessor.STATUS.equals("test")){
					break;//切记，正式版本要删掉，为了减少请求，去掉了很多request
				}
			}
        	
            System.out.println("根据货车品牌信息，获取到的货车系列列表如下...");
			for(Request req:page.getTargetRequests()){
				System.out.println(req.getUrl().toString()+","+req.getExtra("truck_type")+","+req.getExtra("truck_brand")+","+req.getExtra("truck_series"));
			}
			return;
        } 
        //货车车型系列页
        if(page.getUrl().regex(TRUCK_SERIES_LIST).match()){
        	//page.addTargetRequests(page.getHtml().xpath("//div[@class=\"imgname_b_d1\"]").links().regex(TRUCK_PRODUCT_SATUS_LIST).all());
        	Selectable links= page.getHtml().xpath("//div[@class=\"imgname_b_d1\"]/a");
        	
			for(Selectable link:links.nodes()){
				String href=link.xpath("/a/@href").regex(TRUCK_PRODUCT_STATUS_LIST).toString();
				String text=link.xpath("/a/text()").toString();
				
				if(href!=null){
					Request request=new Request(href);
					request.putExtra("truck_type", page.getRequest().getExtra("truck_type"));
					request.putExtra("truck_brand", page.getRequest().getExtra("truck_brand"));
					request.putExtra("truck_series", page.getRequest().getExtra("truck_series"));
					request.putExtra("truck_product_status", text);
					page.addTargetRequest(request);					
				}
				
				if(TruckProcessor.STATUS.equals("test")){
					break;//切记，正式版本要删掉，为了减少请求，去掉了很多request
				}
			}
        	
			System.out.println("根据货车系列信息，获取到的货车产品状态列表如下...");
			for(Request req:page.getTargetRequests()){
				System.out.println(req.getUrl().toString()+","+req.getExtra("truck_type")+","+req.getExtra("truck_brand")+","+req.getExtra("truck_series")+","+req.getExtra("truck_product_status"));
			}
			return;
        }
        //货车车型状态页
        if(page.getUrl().regex(TRUCK_PRODUCT_STATUS_LIST).match()){
        	//page.addTargetRequests(page.getHtml().xpath("//div[@class=\"imgname_b_cent\"]").links().regex(TRUCK_PRODUCT_INFO_LIST).all());
        	Selectable links= page.getHtml().xpath("//div[@class=\"imgname_b_cent di\"]/dl/dt/a");
        	
			for(Selectable link:links.nodes()){
				String href=link.xpath("/a/@href").regex(TRUCK_PRODUCT_LIST).toString();
				String text=link.xpath("/a/@title").toString();
				
				if(href!=null){
					Request request=new Request(href);
					request.putExtra("truck_type", page.getRequest().getExtra("truck_type"));
					request.putExtra("truck_brand", page.getRequest().getExtra("truck_brand"));
					request.putExtra("truck_series", page.getRequest().getExtra("truck_series"));
					request.putExtra("truck_product_status", page.getRequest().getExtra("truck_product_status"));
					request.putExtra("truck_product_info", text.replace(" 卡车图片", ""));
					page.addTargetRequest(request);					
				}
				
				if(TruckProcessor.STATUS.equals("test")){
					break;//切记，正式版本要删掉，为了减少请求，去掉了很多request
				}
			}
        	
			System.out.println("根据货车产品状态信息，获取到的货车产品列表如下...");
			for(Request req:page.getTargetRequests()){
				System.out.println(req.getUrl().toString()+","+req.getExtra("truck_type")+","
			+req.getExtra("truck_brand")+","+req.getExtra("truck_series")+","+req.getExtra("truck_product_status")+","+req.getExtra("truck_product_info"));
			}
			return;
        }
        //货车车型列表页
        if(after_truck_index==null&&after_truck_more==null&&page.getUrl().regex(TRUCK_PRODUCT_LIST).match()){
        	
        	Selectable links= page.getHtml().xpath("//div[@class=\"imgname_b_c\"]//div[@class=\"imgname_b_b2\"]/a");
        	
			for(Selectable link:links.nodes()){
				String href=link.xpath("/a/@href").toString();
				
				if(href!=null){
					Request request=new Request(href);
					request.putExtra("truck_type", page.getRequest().getExtra("truck_type"));
					request.putExtra("truck_brand", page.getRequest().getExtra("truck_brand"));
					request.putExtra("truck_series", page.getRequest().getExtra("truck_series"));
					request.putExtra("truck_product_status", page.getRequest().getExtra("truck_product_status"));
					request.putExtra("truck_product_info", page.getRequest().getExtra("truck_product_info"));
					page.addTargetRequest(request);					
				}
				
				if(TruckProcessor.STATUS.equals("test")){
					break;//切记，正式版本要删掉，为了减少请求，去掉了很多request
				}				
			}
			System.out.println("根据货车产品列表信息，获取到的货车产品首页如下...");
			for(Request req:page.getTargetRequests()){
				System.out.println(req.getUrl().toString()+","+req.getExtra("truck_type")+","
			+req.getExtra("truck_brand")+","+req.getExtra("truck_series")+","+req.getExtra("truck_product_status")+","+req.getExtra("truck_product_info"));
			}
			return;
        }
        //货车车型首页
        if(page.getUrl().regex(TRUCK_PRODUCT_INDEX).match()){
        	String truck_type=page.getRequest().getExtra("truck_type").toString();
        	String truck_brand= page.getRequest().getExtra("truck_brand").toString();
        	String truck_series= page.getRequest().getExtra("truck_series").toString();
        	String truck_product_status=page.getRequest().getExtra("truck_product_status").toString();
        	String truck_product_info= page.getRequest().getExtra("truck_product_info").toString();
        	
        	String path="/data/truck/360che"+File.separator+truck_type.trim()+File.separator+truck_brand.trim()+File.separator+truck_series.trim()+File.separator+truck_product_status.trim()+File.separator+truck_product_info.trim()+File.separator;
        	String name="truck_param.html";
        	FilePersistentBase fPB=new FilePersistentBase();
        	
            PrintWriter printWriter = null;
			try {
				printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fPB.getFile(path+name)),"UTF-8"));
				printWriter.println(page.getHtml().xpath("//div[@class='sppic']//div[@class='cx02_cen02']").toString());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}finally{
				printWriter.close();
			}
            
			Selectable links= page.getHtml().xpath("//div[@class=\"sppic\"]//div[@class=\"sppic_tit\"]/strong/a");
			
			for(Selectable link:links.nodes()){
				String href=link.xpath("/a/@href").toString();
				
				if(href!=null){
					if(!href.contains("https://product.360che.com")){
						href="https://product.360che.com"+href;
					}
					Request request=new Request(href);
					request.putExtra("truck_type", page.getRequest().getExtra("truck_type"));
					request.putExtra("truck_brand", page.getRequest().getExtra("truck_brand"));
					request.putExtra("truck_series", page.getRequest().getExtra("truck_series"));
					request.putExtra("truck_product_status", page.getRequest().getExtra("truck_product_status"));
					request.putExtra("truck_product_info", page.getRequest().getExtra("truck_product_info"));
					request.putExtra("after_truck_index", "1");
					page.addTargetRequest(request);					
				}
				
				if(TruckProcessor.STATUS.equals("test")){
					break;//切记，正式版本要删掉，为了减少请求，去掉了很多request
				}				
			}
			
			System.out.println("根据货车产品首页，获取图片库地址如下...");
			for(Request req:page.getTargetRequests()){
				System.out.println(req.getUrl().toString()+","+req.getExtra("truck_type")+","
			+req.getExtra("truck_brand")+","+req.getExtra("truck_series")+","+req.getExtra("truck_product_status")+","+req.getExtra("truck_product_info"));
			}
			return;
        }
        
        //货车图片库页
        if(after_truck_index!=null&&after_truck_more==null&&page.getUrl().regex(TRUCK_PRODUCT_PIC_LIST).match()){
        	String more=page.getHtml().xpath("//div[@class='imgname_b_b']//div[@class='imgname_b_b2']").toString();
        	if(more!=null){	//有more按钮
        		System.out.println(more);
        		String href= page.getHtml().xpath("//div[@class='imgname_b_b']//div[@class='imgname_b_b2']/a/@href").toString();
        		   			    				
	       		Request request=new Request(href);
				request.putExtra("truck_type", page.getRequest().getExtra("truck_type"));
				request.putExtra("truck_brand", page.getRequest().getExtra("truck_brand"));
				request.putExtra("truck_series", page.getRequest().getExtra("truck_series"));
				request.putExtra("truck_product_status", page.getRequest().getExtra("truck_product_status"));
				request.putExtra("truck_product_info", page.getRequest().getExtra("truck_product_info"));
				request.putExtra("after_truck_index", "1");
				request.putExtra("after_truck_more", "1");
				page.addTargetRequest(request);	   			
				
        	}else{			//无more按钮，直接获取图片地址
        		System.out.println("more is null");
        		Selectable imgs= page.getHtml().xpath("//div[@class='imgname_b_cent']//dl[@class='dlwidthnew']/dt/a/img");
        		for(Selectable img:imgs.nodes()){
        			String img_src=img.xpath("/img/@src").toString();
        			
    	       		Request request=new Request(img_src);
    				request.putExtra("truck_type", page.getRequest().getExtra("truck_type"));
    				request.putExtra("truck_brand", page.getRequest().getExtra("truck_brand"));
    				request.putExtra("truck_series", page.getRequest().getExtra("truck_series"));
    				request.putExtra("truck_product_status", page.getRequest().getExtra("truck_product_status"));
    				request.putExtra("truck_product_info", page.getRequest().getExtra("truck_product_info"));
    				request.putExtra("after_truck_index", "1");
    				request.putExtra("after_truck_more", "1");
    				
    				page.addTargetRequest(request);
        		}	
        	}
        }
        
        if(after_truck_index!=null&&after_truck_more!=null&&page.getUrl().regex(TRUCK_PRODUCT_PIC_MORE_LIST).match()){
    		Selectable imgs= page.getHtml().xpath("//div[@class='imgname_b_cent']/dl/dt/a/img");
    		for(Selectable img:imgs.nodes()){
    			String img_src=img.xpath("/img/@src").toString().split("!")[0]+"!1200";
    			
	       		Request request=new Request(img_src);
				request.putExtra("truck_type", page.getRequest().getExtra("truck_type"));
				request.putExtra("truck_brand", page.getRequest().getExtra("truck_brand"));
				request.putExtra("truck_series", page.getRequest().getExtra("truck_series"));
				request.putExtra("truck_product_status", page.getRequest().getExtra("truck_product_status"));
				request.putExtra("truck_product_info", page.getRequest().getExtra("truck_product_info"));
				request.putExtra("after_truck_index", "1");
				request.putExtra("after_truck_more", "1");
				
				page.addTargetRequest(request);
    		}
    		
    		//页码
    		Selectable pagers= page.getHtml().xpath("//div[@class='pageboxtom']//div[@class='pages']//a[@class!='pages-wd']");
    		for(Selectable pager:pagers.nodes()){
    			String href=pager.xpath("/a/@href").toString();
    			
				if(!href.contains("https://product.360che.com")){
					href="https://product.360che.com"+href;
				}
    			
	       		Request request=new Request(href);
				request.putExtra("truck_type", page.getRequest().getExtra("truck_type"));
				request.putExtra("truck_brand", page.getRequest().getExtra("truck_brand"));
				request.putExtra("truck_series", page.getRequest().getExtra("truck_series"));
				request.putExtra("truck_product_status", page.getRequest().getExtra("truck_product_status"));
				request.putExtra("truck_product_info", page.getRequest().getExtra("truck_product_info"));
				request.putExtra("after_truck_index", "1");
				request.putExtra("after_truck_more", "1");
				
				page.addTargetRequest(request);
    		}
        }
        
        if(after_truck_index!=null&&after_truck_more!=null&&page.getUrl().regex(TRUCK_PRODUCT_PIC_DOWNLOAD_LIST).match()){
        	
        	String truck_type=page.getRequest().getExtra("truck_type").toString();
        	String truck_brand= page.getRequest().getExtra("truck_brand").toString();
        	String truck_series= page.getRequest().getExtra("truck_series").toString();
        	String truck_product_status=page.getRequest().getExtra("truck_product_status").toString();
        	String truck_product_info= page.getRequest().getExtra("truck_product_info").toString();
        	
        	String path="/data/truck/360che"+File.separator+truck_type.trim()+File.separator+truck_brand.trim()+File.separator+truck_series.trim()+File.separator+truck_product_status.trim()+File.separator+truck_product_info.trim()+File.separator+"img"+File.separator;
        	String name=UUID.randomUUID().toString().replace("-", "")+".jpg";
        	
        	FilePersistentBase fPB=new FilePersistentBase();
        	
        	byte[] bs=page.getBytes();
        	OutputStream os;
			try {
				os = new FileOutputStream(fPB.getFile(path+name));
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

}
