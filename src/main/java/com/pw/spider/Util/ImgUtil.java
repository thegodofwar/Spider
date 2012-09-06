package com.pw.spider.Util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;

import org.apache.log4j.Logger;

public class ImgUtil {
    
	public static final Logger LOG=Logger.getLogger(ImgUtil.class.getName());
	
	//images with one more merge into one image
	public static byte[] merge(List<byte[]> imgs,String type) {
		try {
			int size = imgs.size();
			if (size == 1) {
				return imgs.get(0);
			}
               
			ByteArrayInputStream imgInputs[] = new ByteArrayInputStream[size];
			BufferedImage bufferImgs[] = new BufferedImage[size];
			int imgRGBArrays[][] = new int[size][];
			
			int totalHeight = 0;
			int lastWidth = 0;
			for (int i = 0; i < size; i++) {
				imgInputs[i] = new ByteArrayInputStream(imgs.get(i));
				try {
					bufferImgs[i] = ImageIO.read(imgInputs[i]);
				} catch (IOException e) {
					LOG.error("", e);
				}
				totalHeight += bufferImgs[i].getHeight();
				if(bufferImgs[i].getWidth()>lastWidth) {
				   lastWidth=bufferImgs[i].getWidth();
				}
			}
			if (totalHeight < 1) {
				LOG.error("imgs' total height<1.");
				return null;
			}
            
			for (int i = 0; i < size; i++) {
				int width = bufferImgs[i].getWidth();
				int height = bufferImgs[i].getHeight();
				imgRGBArrays[i] = new int[lastWidth * height];
				imgRGBArrays[i] = bufferImgs[i].getRGB(0, 0, width, height,
						imgRGBArrays[i], 0, width);
			}
			
			
			BufferedImage lastImg = new BufferedImage(lastWidth, totalHeight,
					BufferedImage.TYPE_INT_RGB);
			int tempHeight = 0;
			for (int i = 0; i < size; i++) {
				lastImg.setRGB(0, tempHeight, lastWidth, bufferImgs[i]
						.getHeight(), imgRGBArrays[i], 0, bufferImgs[i].getWidth());
				tempHeight += bufferImgs[i].getHeight();
				bufferImgs[i]=null;
				imgInputs[i].close();
				imgInputs[i]=null;
				imgRGBArrays[i]=null;
			}
			ByteArrayOutputStream lastBytes = new ByteArrayOutputStream();
			try {
				ImageIO.write(lastImg, type, lastBytes);
			} catch (IOException e) {
				LOG.error("", e);
			}
			byte[] result=lastBytes.toByteArray();
			imgInputs=null;
			bufferImgs=null;
			imgRGBArrays=null;
			lastImg=null;
			lastBytes.close();
			lastBytes=null;
			return result;
		} catch (Throwable e) {
			LOG.error("", e);
			return null;
		}
	}
	
	public static void main(String args[]) {
		/*HttpClient httpclient=HttpCrawler.createMultiThreadClient(400,80,6000,9000);
		byte[] bytes1=HttpCrawler.crawlPic("121xs download content image",httpclient,"http://www.121xs.com/files/article/attachment/6/6151/1770835/282746.gif");
    	byte[] bytes2=HttpCrawler.crawlPic("121xs download content image",httpclient,"http://www.121xs.com/files/article/attachment/6/6151/1770835/282747.gif");
    	byte[] bytes=merge(Arrays.asList(new byte[][]{bytes1,bytes2}),"gif");
    	String imagePath = "D:\\image\\000.gif";
    	FileImageOutputStream imageOutput=null;
		try {
			imageOutput = new FileImageOutputStream(new File(imagePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	if(bytes==null||bytes.length==0) {
    		System.out.println("the byte array is empty!");
    	}
    	try {
			imageOutput.write(bytes, 0, bytes.length);
			imageOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		FileImageInputStream fi1=null;
		try {
			fi1=new FileImageInputStream(new File("d:\\pic\\nuli.jpg"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] buffer1=new byte[1024];
		ByteArrayOutputStream bos1=new ByteArrayOutputStream();
		int len1=0;
		try {
			while((len1=fi1.read(buffer1))>0) {
				bos1.write(buffer1,0,len1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		FileImageInputStream fi2=null;
		try {
			fi2=new FileImageInputStream(new File("d:\\pic\\1.jpg"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int len2=0;
		byte[] buffer2=new byte[1024];
		ByteArrayOutputStream bos2=new ByteArrayOutputStream();
		try {
			while((len2=fi2.read(buffer2))>0) {
				bos2.write(buffer2,0,len2);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fi1.close();
			fi1=null;
			fi2.close();
			fi2=null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		buffer1=null;
		buffer2=null;
		
		byte bytes[]=merge(Arrays.asList(new byte[][]{bos1.toByteArray(),bos2.toByteArray()}),"jpg");
		String imagePath = "D:\\image\\1111.gif";
    	FileImageOutputStream imageOutput=null;
		try {
			imageOutput = new FileImageOutputStream(new File(imagePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	if(bytes==null||bytes.length==0) {
    		System.out.println("the byte array is empty!");
    	}
    	try {
			imageOutput.write(bytes, 0, bytes.length);
			imageOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
