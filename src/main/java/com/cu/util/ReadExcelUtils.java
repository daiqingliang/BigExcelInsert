package com.cu.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;


/**
 * 基于XSSF and SAX (Event API)
 * 读取excel的第一个Sheet的内容
 * @author yzl
 *
 */
public class ReadExcelUtils {
    private int headCount = 0;
    private List<List<String>> list = new ArrayList<List<String>>();
    static BufferedWriter writer = null;
    private static StylesTable stylesTable;
    private  SheetHandler sheetHandler;
    
    public SheetHandler getSheetHandler() {
		return sheetHandler;
	}
	public void setSheetHandler(SheetHandler sheetHandler) {
		this.sheetHandler = sheetHandler;
	}
	public List<List<String>> getList() {
		return list;
	}
	public void setList(List<List<String>> list) {
		this.list = list;
	}

	/**
     * 通过文件流构建DOM进行解析
     * @param ins
     * @param headRowCount   跳过读取的表头的行数
     * @return
     * @throws InvalidFormatException
     * @throws IOException
     */
    public  List<List<String>> processDOMReadSheet(InputStream ins,int headRowCount) throws InvalidFormatException, IOException {
        Workbook workbook = WorkbookFactory.create(ins);
        return this.processDOMRead(workbook, headRowCount);
    }
    
    /**
     * 采用DOM的形式进行解析
     * @param filename
     * @param headRowCount   跳过读取的表头的行数
     * @return
     * @throws IOException 
     * @throws InvalidFormatException 
     * @throws Exception
     */
    public  List<List<String>> processDOMReadSheet(String filename,int headRowCount) throws InvalidFormatException, IOException {
        Workbook workbook = WorkbookFactory.create(new File(filename));
        return this.processDOMRead(workbook, headRowCount);
    }

    /**
     * 采用SAX进行解析
     * @param filename
     * @param headRowCount
     * @return
     * @throws OpenXML4JException 
     * @throws IOException 
     * @throws SAXException 
     * @throws Exception
     */
    public List<List<String>> processSAXReadSheet(String filename,int headRowCount) throws IOException, OpenXML4JException, SAXException   {
        headCount = headRowCount;
        
        OPCPackage pkg = OPCPackage.open(filename);
        XSSFReader r = new XSSFReader( pkg );
        SharedStringsTable sst = r.getSharedStringsTable();
        XMLReader parser = fetchSheetParser(sst);

        Iterator<InputStream> sheets = r.getSheetsData();
        InputStream sheet = sheets.next();
        InputSource sheetSource = new InputSource(sheet);
        parser.parse(sheetSource);
        sheet.close();
        setList(sheetHandler.getList());
        System.out.println("时间:"+Func.getDatenow()+",共读取了execl的记录数为 :"+list.size());
        /**
         * 去掉首行标题，如果不加标题，第一行末尾还是空单元格，还是会出问题
         * 如果有朋友解决了这个问题，请指教系下，被这个折腾的有点累
         */
        return list;
    }

    private XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException {
        XMLReader parser =
            XMLReaderFactory.createXMLReader(
                    "org.apache.xerces.parsers.SAXParser"
            );
        setSheetHandler(new SheetHandler(sst));
        //ContentHandler handler = new SheetHandler(sst);
        ContentHandler handler = (ContentHandler) sheetHandler;
        parser.setContentHandler(handler);
        return parser;
    }

    /**  
     * 自定义解析处理器 
     * See org.xml.sax.helpers.DefaultHandler javadocs  
     */  
    private static class SheetHandler extends DefaultHandler {  
          
        private SharedStringsTable sst;  
        private String lastContents;  
        private boolean nextIsString;  
        //读取行的索引
        private List<String> rowlist = new ArrayList<String>();
        private List<List<String>> list = new ArrayList<List<String>>();  
        private int curRow = 0;   
        private int curCol = 0;  
        
        public List<List<String>> getList() {
            if(list.size()>0){
            	list.remove(0);
            }
			return list;
		}

		//定义前一个元素和当前元素的位置，用来计算其中空的单元格数量，如A6和A8等  
        private String preRef = null, ref = null;  
        //定义该文档一行最大的单元格数，用来补全一行最后可能缺失的单元格  
        private String maxRef = null;  
          
        private CellDataType nextDataType = CellDataType.SSTINDEX;   
        private final DataFormatter formatter = new DataFormatter();   
        private short formatIndex;   
        private String formatString;   
          
        //用一个enum表示单元格可能的数据类型  
        enum CellDataType{   
            BOOL, ERROR, FORMULA, INLINESTR, SSTINDEX, NUMBER, DATE, NULL   
        }  
          
        private SheetHandler(SharedStringsTable sst) {  
            this.sst = sst;  
        }  
          
        /** 
         * 解析一个element的开始时触发事件 
         */  
        public void startElement(String uri, String localName, String name,  
                Attributes attributes) throws SAXException {
        	// c => cell  
            if(name.equals("c")) {  
                //前一个单元格的位置  
                if(preRef == null){  
                    preRef = attributes.getValue("r");  
                }else{  
                    preRef = ref;  
                }  
                //当前单元格的位置  
                ref = attributes.getValue("r");  
                  
                this.setNextDataType(attributes);   
                  
                // Figure out if the value is an index in the SST  
                String cellType = attributes.getValue("t");  
                if(cellType != null && cellType.equals("s")) {  
                    nextIsString = true;  
                } else {  
                    nextIsString = false;  
                }  
            }
            // Clear contents cache  
            lastContents = "";  
        }  
          
        /** 
         * 根据element属性设置数据类型 
         * @param attributes 
         */  
        public void setNextDataType(Attributes attributes){   
  
            nextDataType = CellDataType.NUMBER;   
            formatIndex = -1;   
            formatString = null;   
            String cellType = attributes.getValue("t");   
            String cellStyleStr = attributes.getValue("s");   
            if ("b".equals(cellType)){   
                nextDataType = CellDataType.BOOL;  
            }else if ("e".equals(cellType)){   
                nextDataType = CellDataType.ERROR;   
            }else if ("inlineStr".equals(cellType)){   
                nextDataType = CellDataType.INLINESTR;   
            }else if ("s".equals(cellType)){   
                nextDataType = CellDataType.SSTINDEX;   
            }else if ("str".equals(cellType)){   
                nextDataType = CellDataType.FORMULA;   
            }  
            if (cellStyleStr != null){   
                int styleIndex = Integer.parseInt(cellStyleStr);
                if (stylesTable != null) {
                	XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);   
                    formatIndex = style.getDataFormat();   
                    formatString = style.getDataFormatString();   
                    if ("m/d/yy" == formatString){   
                        nextDataType = CellDataType.DATE;   
                        //full format is "yyyy-MM-dd hh:mm:ss.SSS";  
                        formatString = "yyyy-MM-dd";  
                    }   
                    if (formatString == null){   
                        nextDataType = CellDataType.NULL;   
                        formatString = BuiltinFormats.getBuiltinFormat(formatIndex);   
                    }
                }
                   
            }   
        }  
          
        /** 
         * 解析一个element元素结束时触发事件 
         */  
        public void endElement(String uri, String localName, String name)  
                throws SAXException {  
            // Process the last contents as required.  
            // Do now, as characters() may be called more than once
        	if(nextIsString) {  
                int idx = Integer.parseInt(lastContents);  
                lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();  
                nextIsString = false;  
            }  
  
            // v => contents of a cell  
            // Output after we've seen the string contents  
            if (name.equals("v")) {
            	String value = "";
                value = this.getDataValue(lastContents.trim(), "");
                //补全单元格之间的空单元格  
                if(!ref.equals(preRef)){
                    int len = countNullCell(ref, preRef);
                    for(int i=0;i<len;i++){
                        rowlist.add(curCol, "");
                        curCol++;
                    }
                }
                rowlist.add(curCol, value);
                curCol++;
            }else {
                //如果标签名称为 row，这说明已到行尾，调用 optRows() 方法 
                if (name.equals("row")) {
//                    String value = "";
                    //默认第一行为表头，以该行单元格数目为最大数目
                    if(curRow == 0){
                        maxRef = ref;
                    }
                    //补全一行尾部可能缺失的单元格  
                    if(maxRef != null){  
                        int len = countNullCell(maxRef, ref);  
                        for(int i=0;i<=len;i++){  
                            rowlist.add(curCol, "");  
                            curCol++;  
                        }  
                    }  
                    curRow++;  
                    //一行的末尾重置一些数据 
                    list.add(rowlist);
                    rowlist = new ArrayList<String>();   
                    curCol = 0;   
                    preRef = null;  
                    ref = null;  
                }
            }
        }
          
        /** 
         * 根据数据类型获取数据 
         * @param value 
         * @param thisStr 
         * @return 
         */  
        public String getDataValue(String value, String thisStr)   
  
        {   
            switch (nextDataType)   
            {   
                //这几个的顺序不能随便交换，交换了很可能会导致数据错误   
                case BOOL:   
                char first = value.charAt(0);   
                thisStr = first == '0' ? "FALSE" : "TRUE";   
                break;   
                case ERROR:   
                thisStr = "\"ERROR:" + value.toString() + '"';   
                break;   
                case FORMULA:   
                thisStr = '"' + value.toString() + '"';   
                break;   
                case INLINESTR:   
                XSSFRichTextString rtsi = new XSSFRichTextString(value.toString());   
                thisStr = rtsi.toString();   
                rtsi = null;   
                break;   
                case SSTINDEX:   
                //String sstIndex = value.toString();   
                thisStr = value.toString();   
                break;   
                case NUMBER:   
                if (formatString != null){   
                    thisStr = formatter.formatRawCellContents(Double.parseDouble(value), formatIndex, formatString).trim();   
                }else{  
                    thisStr = value;   
                }   
                thisStr = thisStr.replace("_", "").trim();   
                break;   
                case DATE:   
                    try{  
                        thisStr = formatter.formatRawCellContents(Double.parseDouble(value), formatIndex, formatString);   
                    }catch(NumberFormatException ex){  
                        thisStr = value.toString();  
                    }  
                thisStr = thisStr.replace(" ", "");  
                break;   
                default:   
                thisStr = "";   
                break;   
            }   
            return thisStr;   
        }   
  
        /** 
         * 获取element的文本数据 
         */  
        public void characters(char[] ch, int start, int length)  
                throws SAXException {  
            lastContents += new String(ch, start, length);  
        }  
          
        /** 
         * 计算两个单元格之间的单元格数目(同一行) 
         * @param ref 
         * @param preRef 
         * @return 
         */  
        public int countNullCell(String ref, String preRef){  
            //excel2007最大行数是1048576，最大列数是16384，最后一列列名是XFD  
            String xfd = ref.replaceAll("\\d+", "");  
            String xfd_1 = preRef.replaceAll("\\d+", "");  
              
            xfd = fillChar(xfd, 3, '@', true);  
            xfd_1 = fillChar(xfd_1, 3, '@', true);  
              
            char[] letter = xfd.toCharArray();  
            char[] letter_1 = xfd_1.toCharArray();  
            int res = (letter[0]-letter_1[0])*26*26 + (letter[1]-letter_1[1])*26 + (letter[2]-letter_1[2]);  
            return res-1;  
        }  
          
        /** 
         * 字符串的填充 
         * @param str 
         * @param len 
         * @param let 
         * @param isPre 
         * @return 
         */  
        String fillChar(String str, int len, char let, boolean isPre){  
            int len_1 = str.length();  
            if(len_1 <len){  
                if(isPre){  
                    for(int i=0;i<(len-len_1);i++){  
                        str = let+str;  
                    }  
                }else{  
                    for(int i=0;i<(len-len_1);i++){  
                        str = str+let;  
                    }  
                }  
            }  
            return str;  
        }  
    }
    
    /**
     * DOM的形式解析execl
     * @param workbook
     * @param headRowCount
     * @return
     * @throws InvalidFormatException
     * @throws IOException
     */
    private List<List<String>> processDOMRead(Workbook workbook,int headRowCount) throws InvalidFormatException, IOException {
        headCount = headRowCount;
        
        Sheet sheet = workbook.getSheetAt(0);
        //行数
        int endRowIndex = sheet.getLastRowNum();
        
        Row row = null;
        List<String> rowList = null;
        
        for(int i=headCount; i<=endRowIndex; i++){
            rowList = new ArrayList<String>();
            row = sheet.getRow(i);
            for(int j=0; j<row.getLastCellNum();j++){
                if(null==row.getCell(j)){
                    rowList.add(null);
                    continue;
                }
                int dataType = row.getCell(j).getCellType();
                if(dataType == Cell.CELL_TYPE_NUMERIC){
                    DecimalFormat df = new DecimalFormat("0.####################");  
                    rowList.add(df.format(row.getCell(j).getNumericCellValue()));
                }else if(dataType == Cell.CELL_TYPE_BLANK){
                    rowList.add(null);
                }else if(dataType == Cell.CELL_TYPE_ERROR){
                    rowList.add(null);
                }else{
                    //这里的去空格根据自己的情况判断
                    String valString = row.getCell(j).getStringCellValue();
                    Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                    Matcher m = p.matcher(valString);
                    valString = m.replaceAll("");
                    //去掉狗日的不知道是啥东西的空格
                    if(valString.indexOf(" ")!=-1){
                        valString = valString.substring(0, valString.indexOf(" "));
                    }
                    
                    rowList.add(valString);
                }
            }
            
            list.add(rowList);
        }
        System.out.println("时间:"+Func.getDatenow()+",共读取了execl的记录数为 :"+list.size());
        
        return list;
    }
    
    public static void main(String[] args) throws Exception {
    	ReadExcelUtils h = new ReadExcelUtils();
        String fileName1 = "C:/Users/daiql/Desktop/2018上门明细截止6月.xlsx";
        List<List<String>> result = h.processDOMReadSheet(fileName1,1);
        System.out.println(result.toString());
        System.out.println(result.size());
        for (int i = 0; i < result.size(); i++) {
        	System.out.println("当前第:"+(i+1)+"行，该List共计"+result.get(i).size()+"行数据");
        }
        
    }
}