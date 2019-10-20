package com.qingcheng.controller.POIUtils;

import com.qingcheng.pojo.order.Order;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class POIUtil {
    //传用户名和订单集合
    public static void  getExcel(List<Order> orderList){
        //创建工作簿
        Workbook wb = new XSSFWorkbook();

        //创建sheet
        Sheet sheet = wb.createSheet("订单管理");

        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        //生成列头
        String[] str = {"订单id", "订单金额", "支付类型", "用户名称", "收货人", "收货人手机号", "订单来源", "订单状态"};
        for (int i = 0; i < str.length; i++) {
            //创建行对象
            //创建单元格
            sheet.setColumnWidth(i, 20 * 250);//列宽
            cell = row.createCell(i);//数组下表
            cell.setCellValue(str[i]);

        }
        int j = 0;
        //生成数据
        for (Order order :orderList){
            row = sheet.createRow(++j);
            cell = row.createCell(0);//数组下表
            cell.setCellValue(order.getId());//订单编号
            cell = row.createCell(1);
            cell.setCellValue(order.getPayMoney()+"");//订单金额
            cell = row.createCell(2);
            if ("1".equals(order.getPayType())) {
                cell.setCellValue("在线支付");//支付类型
            }else {
                cell.setCellValue("货到付款");//支付类型
            }
            cell = row.createCell(3);
            cell.setCellValue(order.getUsername());//用户账号
            cell = row.createCell(4);
            cell.setCellValue(order.getReceiverContact());//收货人名称
            cell = row.createCell(5);
            cell.setCellValue(order.getReceiverMobile());//收货人手机号
            cell = row.createCell(6);
            cell.setCellValue("web");
            cell = row.createCell(7);
            if ("1".equals(order.getOrderStatus())){
                cell.setCellValue("待发货");//订单状态
            }else if ("0".equals(order.getOrderStatus())){
                cell.setCellValue("待付款");//订单状态
            }else if ("2".equals(order.getOrderStatus())){
                cell.setCellValue("已发货");//订单状态
            }


        }

        //设置样式
        /**
         * 1.创建样式对象
         * 2.通过样式对象指定样式
         * 3.配置单元个样式
         */

        CellStyle cellStyle = wb.createCellStyle();
        //通过样式对象指定样式
//        cellStyle.setBorderTop(BorderStyle.THIN); //细线
//        cellStyle.setBorderBottom(BorderStyle.THIN); //细线
//        cellStyle.setBorderLeft(BorderStyle.THIN); //细线
//        cellStyle.setBorderRight(BorderStyle.THIN); //细线

        //字体 对象
        Font font = wb.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short)12);//字号

        cellStyle.setFont(font);
        cell.setCellStyle(cellStyle);

        row.setHeightInPoints(20);//指定行高

        //6.将excel保存到本地磁盘中

        FileOutputStream fos =  null;
        Date date = new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddhhmmss");
        String dateStr = sdf.format(date);
        try {

            fos = new FileOutputStream("D:\\"+dateStr+"测试.xlsx");
            wb.write(fos);
            fos.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    }
