package cn.stylefeng.guns.core.util;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;

/**
 * 简单导入导出工具类
 *
 * @author xuyuxiang
 * @date 2020/6/30 17:25
 */
public class PoiUtil {

    /**
     * 使用流的方式导出excel
     *
     * @author xuyuxiang
     * @date 2020/7/1 10:00
     *
     * @param excelName 要导出的文件名称，如GunsUsers.xls
     * @param pojoClass Excel实体类
     * @param data 要导出的数据集合
     */
    public static void exportExcelWithStream(String excelName, Class pojoClass, Collection data) {
        try {
            HttpServletResponse response = HttpServeletUtil.getResponse();
            String fileName = URLEncoder.encode(excelName, "UTF-8");
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            response.setContentType("application/octet-stream;charset=UTF-8");
            ServletOutputStream outputStream = response.getOutputStream();
            Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), pojoClass, data);
            workbook.write(outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用文件的方式导出excel
     *
     * @author xuyuxiang
     * @date 2020/7/1 9:58
     *
     * @param filePath 文件路径，如 d:/demo/demo.xls
     * @param pojoClass Excel实体类
     * @param data 要导出的数据集合
     */
    public static void exportExcelWithFile(String filePath, Class pojoClass, Collection data) {

        try {
            //先创建父文件夹
            FileUtil.mkParentDirs(filePath);
            File file = FileUtil.file(filePath);
            Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), pojoClass, data);
            FileOutputStream fos = new FileOutputStream(file);
            workbook.write(fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 根据文件路径来导入Excel
     *
     * @author xuyuxiang
     * @date 2020/7/1 9:58
     *
     * @param filePath 文件路径
     * @param titleRows 表标题的行数
     * @param headerRows 表头行数
     * @param pojoClass Excel实体类
     */
    public static <T> List<T> importExcel(String filePath, Integer titleRows, Integer headerRows, Class<T> pojoClass) {
        //判断文件是否存在
        if (ObjectUtil.isEmpty(filePath)) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcel(new File(filePath), pojoClass, params);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return list;
    }
    /**
     * 根据接收的Excel文件来导入Excel,并封装成实体类
     *
     * @author xuyuxiang
     * @date 2020/7/1 9:57
     *
     * @param file 上传的文件
     * @param titleRows 表标题的行数
     * @param headerRows 表头行数
     * @param pojoClass Excel实体类
     *
     */
    public static <T> List<T> importExcel(MultipartFile file, Integer titleRows, Integer headerRows, Class<T> pojoClass) {
        if(ObjectUtil.isNull(file)) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcel(file.getInputStream(), pojoClass, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
