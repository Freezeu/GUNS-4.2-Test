package cn.stylefeng.guns.core.enums;

import org.jodconverter.document.DefaultDocumentFormatRegistry;
import org.jodconverter.document.DocumentFormat;

import java.io.*;

/**
 * 文档类型枚举
 *
 * @author xuyuxiang
 * @date 2020/7/6 15:00
 */
public enum DocumentFormatEnum {

    /**
     * DOC格式
     */
    DOC {
        @Override
        public DocumentFormat getFormFormat() {
            return DefaultDocumentFormatRegistry.DOC;
        }
    },

    /**
     * DOCX格式
     */
    DOCX {
        @Override
        public DocumentFormat getFormFormat() {
            return DefaultDocumentFormatRegistry.DOCX;
        }
    },

    /**
     * PPT格式
     */
    PPT {
        @Override
        public DocumentFormat getFormFormat() {
            return DefaultDocumentFormatRegistry.PPT;
        }
    },

    /**
     * PPTX格式
     */
    PPTX {
        @Override
        public DocumentFormat getFormFormat() {
            return DefaultDocumentFormatRegistry.PPTX;
        }
    },

    /**
     * XLS格式
     */
    XLS {
        @Override
        public DocumentFormat getFormFormat() {
            return DefaultDocumentFormatRegistry.XLS;
        }

        /**
         * XLS转换的目标为HTML，转成pdf的话会有还行的问题
         */
        @Override
        public DocumentFormat getTargetFormat() {
            return DefaultDocumentFormatRegistry.HTML;
        }
    },

    /**
     * XLSX格式
     */
    XLSX {
        @Override
        public DocumentFormat getFormFormat() {
            return DefaultDocumentFormatRegistry.XLSX;
        }

        /**
         * XLSX转换的目标为HTML，转成pdf的话会有还行的问题
         */
        @Override
        public DocumentFormat getTargetFormat() {
            return DefaultDocumentFormatRegistry.HTML;
        }
    },

    /**
     * TXT格式
     */
    TXT {
        @Override
        public DocumentFormat getFormFormat() {
            return DefaultDocumentFormatRegistry.TXT;
        }
    };

    public InputStream getInputStream(InputStream inputStream) throws IOException {
        return inputStream;
    }

    public abstract DocumentFormat getFormFormat();

    /**
     * 默认转为pdf
     *
     * @author xuyuxiang
     * @date 2020/7/9 10:26
     */
    public DocumentFormat getTargetFormat() {
        return DefaultDocumentFormatRegistry.PDF;
    }
}
