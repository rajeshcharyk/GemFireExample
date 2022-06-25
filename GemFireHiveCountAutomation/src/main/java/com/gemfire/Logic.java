package com.gemfire;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;

@Controller
public class Logic {
	static Logger logger = Logger.getLogger(GemFireHiveCountAutomationApplication.class.getName());
	  private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	  static String staticPath = "E:\\GemfireCount\\";
	  static boolean finalPopupValue = false;
	//  @Scheduled(cron = "0 10 21 * * ?", zone="IST")  //- Fires at 09:10 PM every day:
	    @Scheduled(cron = "0 59 21 * * ?", zone="IST")  //- Fires at 09:10 PM every day:
	  public static void codeFlow() throws ParseException, MessagingException {
	    logger.setLevel(Level.INFO);
	    logger.info("<<<<<<<<<<<<<<<<<-------------------->>>>>>>>>>>>>>>>>>>>");
	    File root = new File(staticPath);
	    if (root.exists()) {
	      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	      LocalDateTime now = LocalDateTime.now();
	      String outputPath = staticPath + "\\output_" + dtf.format(now) + "\\";
	      File input = new File(staticPath + "\\" + dtf.format(now));
	      if (input.exists()) {
	        String[] list = input.list();
	        if (list.length > 0) {
	          for (String list1: list) {
	            File soureceFile = new File(input + "\\" + list1);
	            String extension = FilenameUtils.getExtension(soureceFile.toString());
	            if (extension.equalsIgnoreCase("xlsx")) {
	              if (soureceFile.exists()) {
	                File output = new File(outputPath);
	                if (!output.exists()) {
	                  output.mkdirs();
	                }
	                String out = output + "\\" + list1;
	                finalPopupValue = mainLogic(soureceFile.toString(), out, finalPopupValue);
	              } else {
	                finalPopupValue = false;
	                logger.info("In Todays folder XLSX not exist.");
	              }
	            }
	          }
	        } else {
	          finalPopupValue = false;
	          logger.info("In Todays folder File not exist.");
	        }
	      } else {
	        finalPopupValue = false;
	        logger.info("Todays folder not exist in E Drive - GemfireCount");
	      }
	    } else {
	      finalPopupValue = false;
	      logger.info("Create a folder in E Driver name as GemfireCount");
	    }
	    if (finalPopupValue) {
	      logger.info("Successfully completed.");
	      logger.info("Execution Time -   " + dateTimeFormatter.format(LocalDateTime.now()));
	    }
	  }
	  static boolean mainLogic(String filePath, String outputPath, Boolean finalPopupValue) throws ParseException, MessagingException {
	    Workbook wb = null;
	    String error_region_names = "";
	    try {
	      wb = WorkbookFactory.create(new FileInputStream(filePath));
	      Sheet sheet = wb.getSheetAt(0);
	      Row row1 = sheet.getRow(0);
	      CellStyle styleForStatus = wb.createCellStyle();
	      Font fontForStatus = wb.createFont();
	      fontForStatus.setColor(IndexedColors.BLACK.getIndex());
	      Cell cellForStatus = row1.createCell(5);
	      cellForStatus.setCellValue("Status");
	      fontForStatus.setBold(true);
	      styleForStatus.setFont(fontForStatus);
	      cellForStatus.setCellStyle(styleForStatus);
	      for (int j = 1; j < sheet.getLastRowNum() + 1; j++) {
	        Row row = sheet.getRow(j);
	        Cell cell0 = row.getCell(0);
	        cell0.setCellType(CellType.STRING);
	        Cell cell = row.getCell(1);
	        cell.setCellType(CellType.STRING);
	        Cell cell1 = row.getCell(2);
	        cell1.setCellType(CellType.STRING);
	        Cell cell2 = row.getCell(3);
	        cell2.setCellType(CellType.STRING);
	        Cell cell3 = row.createCell(5);
	        if (cell.getStringCellValue().equalsIgnoreCase(cell1.getStringCellValue()) &&
	          cell1.getStringCellValue().equalsIgnoreCase(cell2.getStringCellValue()) &&
	          cell.getStringCellValue().equalsIgnoreCase(cell2.getStringCellValue())) {
	          cell3.setCellValue("True");
	          CellStyle styleForMatching = wb.createCellStyle();
	          Font fontForMatching = wb.createFont();
	          fontForMatching.setColor(IndexedColors.GREEN.getIndex());
	          styleForMatching.setFont(fontForMatching);
	          cell3.setCellStyle(styleForMatching);
	          fontForMatching.setBold(true);
	        } else {
	          CellStyle styleForMatching = wb.createCellStyle();
	          cell3.setCellValue("False");
	          Font fontMatching = wb.createFont();
	          fontMatching.setColor(IndexedColors.RED.getIndex());
	          fontMatching.setBold(true);
	          styleForMatching.setFont(fontMatching);
	          cell3.setCellStyle(styleForMatching);
	          error_region_names = cell0.getStringCellValue() + ", "+ error_region_names;
	          fontMatching.setBold(true);
	        }
	      }
	    } catch (EncryptedDocumentException e) {
	      e.printStackTrace();
	    } catch (FileNotFoundException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    } catch (InvalidFormatException e) {
	      e.printStackTrace();
	    }
	    try {
	      OutputStream fileOut = new FileOutputStream(outputPath);
	      wb.write(fileOut);
	      fileOut.close();
	      finalPopupValue = true;
	    } catch (FileNotFoundException e) {
	      e.printStackTrace();
	      finalPopupValue = false;
	      logger.info("Excel Sheet Opened, Please close gemfirecount_result Excel file");
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    sendMail(error_region_names.trim());
	    return finalPopupValue;
	  }
	  
		public static void sendMail(String statusMessage) throws ParseException, MessagingException {
	        
	        String from = "gemfiretesting@gmail.com";
	        String to = "gemfiretesting@gmail.com";
	        String host = "smtp.gmail.com";
	        Properties properties = System.getProperties();
	        properties.put("mail.smtp.host", host);
	        properties.put("mail.smtp.port", "465");
	        properties.put("mail.smtp.ssl.enable", "true");
	        properties.put("mail.smtp.auth", "true");
			 Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
		            protected PasswordAuthentication getPasswordAuthentication() {
		                return new PasswordAuthentication("gemfiretesting@gmail.com", "Gemfire@123");
		            }
		        });
			try {
	            MimeMessage message = new MimeMessage(session);
	            message.setFrom(new InternetAddress(from));
	            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
	            if(statusMessage.equalsIgnoreCase("")) {
	            	message.setSubject("Gemfire & Hive Count Status - Success");
		            message.setContent("Hi Team, <br><br> <span style=\"color:green;font-weight: bold;\">Received Gemfire & Hive count Mail and counts are matching</span><br><br>Thanks & Regards,<br>Subba Reddy.T,<br>+91-9948079661.", "text/html; charset=utf-8");
		            logger.info("Sent mail success...");
		            Transport.send(message);
	            }else {
	            	message.setSubject("Gemfire & Hive Count Status -Failuer");
	            	String failuer = "Hi Team, <br><br> <span style=\"font-weight: bold;\">Received Gemfire & Hive count Mail but count are mismatch.<br>Please check below regions are mismatch <br></span><span style=\"font-weight: bold;color:red;\"><br>"+statusMessage+"</span><br><br>Thanks & Regards,<br>Subba Reddy.T,<br>+91-9948079661.";
	            	message.setContent(failuer, "text/html; charset=utf-8");
	            	logger.info("Sent mail failuer because of counts are not matching... ");
		            Transport.send(message);
	            }
	        } catch (MessagingException mex) {
	            mex.printStackTrace();
	        }
		    }

}
