package com.creugrogasoft.biphone;

import org.springframework.stereotype.Service;

import com.creugrogasoft.biphone.utils.*;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;

import java.util.HashMap;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;

@Service
public class BIPhoneService {
	
	public static HashMap<String, String> llegenda = new HashMap();

	public static String getAvui () {
		/*
			Panell resum diari
	    */
	    String table_today = "<table width='100%' class='table table-responsive'>";
	    table_today += "<thead>";
	    table_today += "<tr>";
	    table_today += "<th scope='col' class='text-center'>Cua</th>";
	    table_today += "<th scope='col' class='text-center'>Rebudes</th>";
	    table_today += "<th scope='col' class='text-center'>Ateses</th>";
	    table_today += "<th scope='col' class='text-center'>%ateses</th>";
	    table_today += "</tr>";
	    table_today += "</thead>";
	    table_today += "<tbody>";
	            
	    try {
	        Connection conn = Utils.createAsteriskConnection();
	        Statement stmt = conn.createStatement();
	        String sql = "select dst, count(*) as total, sum(IF(disposition = 'ANSWERED' && lastapp='Dial', 1, 0)) as ateses " +
	                "from asteriskcdrdb.cdr " +
	                "where DATEDIFF(CURDATE(), calldate)=0 and dcontext = 'ext-group' " +
	                "group by dst";
	        ResultSet rs=stmt.executeQuery(sql);
	        
	        while (rs.next()) {    
	            table_today += "<tr>";
	            table_today += "<td>"  + rs.getString("dst") + "/" + BIPhoneService.llegenda.get(rs.getString("dst"))+ "</td>";
	            table_today += "<td>"  + rs.getString("total") + "</td>";
	            table_today += "<td>"  + rs.getString("ateses") + "</td>";
	            float total = Float.parseFloat(rs.getString("total"));
	            float ateses = Float.parseFloat(rs.getString("ateses"));
	            java.util.Formatter formatter = new java.util.Formatter();
	            String percent = formatter.format("%.2f",(ateses/total)*100f).toString();
	            table_today += "<td>"  + percent + "%</td>";
	            table_today += "</tr>";
	        }
	        table_today += "</tbody></table>";
	        
	    } catch (Exception e ) {
	        e.printStackTrace(System.out);
	    } 
	    
    	return table_today;
	}
	
	public static String getFa1Setmana () {
		/*
	        Fa una setmana a aquesta hora...
	    */
	    
	    String table_week_ago = "<table width='100%' class='table table-responsive'>";
	    table_week_ago += "<thead>";
	    table_week_ago += "<tr>";
	    table_week_ago += "<th scope='col' class='text-center'>Cua</th>";
	    table_week_ago += "<th scope='col' class='text-center'>Rebudes</th>";
	    table_week_ago += "<th scope='col' class='text-center'>Ateses</th>";
	    table_week_ago += "<th scope='col' class='text-center'>%ateses</th>";
	    table_week_ago += "</tr>";
	    table_week_ago += "</thead>";
	    table_week_ago += "<tbody>";
	            
	    try {
	        Connection conn = Utils.createAsteriskConnection();
	        Statement stmt = conn.createStatement();
	        String sql = "select dst, count(*) as total, sum(IF(disposition = 'ANSWERED' && lastapp='Dial', 1, 0)) as ateses " + 
	                "from asteriskcdrdb.cdr  " + 
	                "where DATEDIFF(CURDATE(), calldate)=7 "
	                + "and (HOUR(calldate) < HOUR(CURRENT_TIMESTAMP()) or (HOUR(calldate) = HOUR(CURRENT_TIMESTAMP()) and MINUTE(calldate) <= MINUTE(CURRENT_TIMESTAMP()))) and dcontext  = 'ext-group'  " + 
	                "group by dst";
	        ResultSet rs=stmt.executeQuery(sql);
	        
	        while (rs.next()){    
	            table_week_ago += "<tr>";
	            table_week_ago += "<td>"  + rs.getString("dst") + "/" + BIPhoneService.llegenda.get(rs.getString("dst")) + "</td>";
	            table_week_ago += "<td>"  + rs.getString("total") + "</td>";
	            table_week_ago += "<td>"  + rs.getString("ateses") + "</td>";
	            float total = Float.parseFloat(rs.getString("total"));
	            float ateses = Float.parseFloat(rs.getString("ateses"));
	            java.util.Formatter formatter = new java.util.Formatter();
	            String percent = formatter.format("%.2f",(ateses/total)*100f).toString();
	            table_week_ago += "<td>"  + percent + "%</td>";
	            table_week_ago += "</tr>";
	        }
	        table_week_ago += "</tbody></table>";
	        
	    } catch (Exception e ) {
	        e.printStackTrace(System.out);
	    } 
	    return table_week_ago;
	}
	
	public static String getNoAteses () {
		/*
	        Panell trucades no ateses
	    */
	        
	    
	    String table_noanswer = "<table width='100%' class='table table-striped table-bordered table-hover' id='dataTables-noanswer'>";
	    
	    table_noanswer += "<thead>";
	    table_noanswer += "<tr>";
	    table_noanswer += "<th scope='col' id='Origen'>Origen</th>";
	    table_noanswer += "<th scope='col' id='Hora'>Trucant des de...</th>";
	    table_noanswer += "<th scope='col' id='Espera'>Total espera</th>";
	    table_noanswer += "<th scope='col' id='Total'>Total trucades</th>";
	    table_noanswer += "</tr>";
	    table_noanswer += "</thead>";
	    table_noanswer += "<tbody>";
	    
	    try {
	        
	        Connection conn = Utils.createAsteriskConnection();
	        Statement stmt = conn.createStatement();
	        String sql = "SELECT b.src, " +
	                            "sum(b.duration) as espera, " +
	                            "min(b.calldate) as desde, " +
	                        "count(*) as total " +
	                    "FROM asteriskcdrdb.cdr b " +
	                    "where (b.disposition = 'NO ANSWER' or (b.disposition = 'ANSWERED' and lastapp<>'Dial')) and b.dst = '6000' and DATEDIFF(CURDATE(), b.calldate)=0 " +
	                            "and b.src NOT IN (SELECT c.src  " +
	                                                            "from asteriskcdrdb.cdr c  " +
	                                        "WHERE c.dst='6000'  " +
	                                            "and DATEDIFF(CURDATE(), c.calldate)=0  " +
	                                            "and c.disposition = 'ANSWERED'  " +
	                                            "and c.lastapp='Dial') " +
	                    "group by b.src";
	        
	        ResultSet rs=stmt.executeQuery(sql);
	        
	        while (rs.next()){    
	            table_noanswer += "<tr>";
	            table_noanswer += "<td>" + rs.getString("src")+ "</td>";
	            table_noanswer += "<td>" + rs.getString("desde")+ "</td>";
	            table_noanswer += "<td>" + Utils.parseSecondsToReport(Integer.parseInt(rs.getString("espera"))) + "</td>";
	            table_noanswer += "<td>" + rs.getString("total") + "</td>";
	            table_noanswer += "</tr>";
	        }
	        table_noanswer += "</tbody></table>";
	        
	    } catch (Exception e ) {
	        e.printStackTrace(System.out);
	    } 
	    return table_noanswer;
	}
	
	public static String getAteses () {
		/*
	        Panell trucades agafades
	    */
	
	    String table_answered = "<table width='100%' class='table table-striped table-bordered table-hover' id='dataTables-answered'>";
	    
	    table_answered += "<thead>";
	    table_answered += "<tr>";
	    table_answered += "<th scope='col' id='Extensio'>Extensio</th>";
	    table_answered += "<th scope='col' id='Contestades'>Contestades</th>";
	    table_answered += "<th scope='col' id='Conversa'>Temps total atencio</th>";
	    table_answered += "<th scope='col' id='Mitjana'>Mitjana per trucada</th>";
	    table_answered += "</tr>";
	    table_answered += "</thead>";
	    table_answered += "<tbody>";
	    
	    try {
	        
	        Connection conn = Utils.createAsteriskConnection();
	        Statement stmt = conn.createStatement();
	        String sql = "select RIGHT(LEFT(dstchannel, 7), 3) as extensio, COUNT(*) as contestades, SUM(billsec) as conversa, ROUND(SUM(billsec)/COUNT(*)) as mitjanaatencio " +
	                    "from asteriskcdrdb.cdr " +
	                    "where RIGHT(LEFT(dstchannel, 7), 3) in (111,112,102,100,101,104) and DATEDIFF(CURDATE(), calldate)=0 and dst = '6000' and disposition = 'ANSWERED' and lastapp='Dial' " +
	                    "GROUP BY RIGHT(LEFT(dstchannel, 7), 3) ";
	        ResultSet rs=stmt.executeQuery(sql);
	        
	        while (rs.next()){    
	            table_answered += "<tr>";
	            table_answered += "<td>" + rs.getString("extensio")+ "</td>";
	            table_answered += "<td>" + rs.getString("contestades")+ "</td>";
	            table_answered += "<td>" + Utils.parseSecondsToReport(Integer.parseInt(rs.getString("conversa"))) + "</td>";
	            table_answered += "<td>" + Utils.parseSecondsToReport(Integer.parseInt(rs.getString("mitjanaatencio"))) + "</td>";
	            table_answered += "</tr>";
	        }
	        table_answered += "</tbody></table>";
	        
	    } catch (Exception e ) {
	        e.printStackTrace(System.out);
	    }
	    return table_answered;
	}
	
	public static String getLlegendaGrups () {
		/*
	        Llegenda grups de timbrat
	    */
	    
	    String table_legend = "<table width='100%' class='table' id='dataTables-grup'>";
	    table_legend += "<thead>";
	    table_legend += "<tr>";
	    table_legend += "<th scope='col' class='text-center'>Codi</th>";
	    table_legend += "<th scope='col' class='text-center'>Descripció</th>";
	    table_legend += "<th scope='col' class='text-center'>Extensions</th>";
	    table_legend += "</tr>";
	    table_legend += "</thead>";
	    table_legend += "<tbody>";
	    
	    try {
	        Connection conn = Utils.createAsteriskCoreConnection();
	        Statement stmt = conn.createStatement();
	        String sql = "select grpnum as codi, grplist as llista, description as descr from asterisk.ringgroups union select extension as codi, '' as llista, descr as descr from asterisk.queues_config order by codi asc";
	        ResultSet rs=stmt.executeQuery(sql);
	        
	        while (rs.next()){    
	            table_legend += "<tr>";
	            table_legend += "<td>"  + rs.getString("codi") + "</td>";
	            table_legend += "<td>"  + rs.getString("descr") + "</td>";
	            table_legend += "<td>"  + rs.getString("llista") + "</td>";
	            table_legend += "</tr>";
	            BIPhoneService.llegenda.put(rs.getString("codi"), rs.getString("descr"));
	        }
	        table_legend += "</tbody></table>";
	        
	    } catch (Exception e ) {
	        e.printStackTrace(System.out);
	    } 
	    return table_legend;
	}
	
	public static String getLlegendaExtensions () {
		/*
	        Llegenda grups de timbrat
	    */
	    
	    String table_legend = "<table width='100%' class='table' id='dataTables-extensio'>";
	    table_legend += "<thead>";
	    table_legend += "<tr>";
	    table_legend += "<th scope='col' class='text-center'>Extensió</th>";
	    table_legend += "<th scope='col' class='text-center'>Descripció</th>";
	    table_legend += "</tr>";
	    table_legend += "</thead>";
	    table_legend += "<tbody>";
	    
	    try {
	        Connection conn = Utils.createAsteriskCoreConnection();
	        Statement stmt = conn.createStatement();
	        String sql = "SELECT u.extension, u.name FROM asterisk.users u";
	        ResultSet rs=stmt.executeQuery(sql);
	        
	        while (rs.next()){    
	            table_legend += "<tr>";
	            table_legend += "<td>"  + rs.getString("extension") + "</td>";
	            table_legend += "<td>"  + rs.getString("name") + "</td>";
	            table_legend += "</tr>";
	        }
	        table_legend += "</tbody></table>";
	        
	    } catch (Exception e ) {
	        e.printStackTrace(System.out);
	    } 
	    return table_legend;
	}
	
	public static String getDadesGraphics (String periode_datainici, String periode_datafi) {
		String data = "";
		String data_dif = "";
		String data_dif_percent = "";
		
		if (periode_datainici.compareTo("") != 0 && periode_datafi.compareTo("") != 0) {
			try {
	            Connection conn = Utils.createAsteriskConnection();
	            Statement stmt = conn.createStatement();
	            String sql = "select LPAD(HOUR(calldate),2,'0') as hora, count(*) as total, sum(IF(disposition = 'ANSWERED' && lastapp='Dial', 1, 0)) as ateses "
	                    + "from asteriskcdrdb.cdr "
	                    + "where HOUR(calldate) BETWEEN 7 AND 22 and dst=6000 and dcontext = 'ext-group' and DATE(calldate) BETWEEN STR_TO_DATE('" + periode_datainici + "', '%Y-%m-%d') AND STR_TO_DATE('" + periode_datafi+ "', '%Y-%m-%d') "
	                    + "group by LPAD(HOUR(calldate),2,'0') "
	                    + "order by 1 asc";
	            ResultSet rs=stmt.executeQuery(sql);

	            while (rs.next()) {
	            	if (data.compareTo("") != 0) {
	            		data += ", ";
	            	}
	            	if (data_dif.compareTo("") != 0) {
	            		data_dif += ", ";
	            	}
	            	if (data_dif_percent.compareTo("") != 0) {
	            		data_dif_percent += ", ";
	            	}
	            	
	            	String hora = rs.getString("hora");
	                int total = rs.getInt("total");
	                int ateses = rs.getInt("ateses");
	                
	                int dif_percent = 0;
	                if (total > 0) {
	                	dif_percent = 100-Math.round(100f*((1f*(ateses)) / (1f*total)));
	    	        }

	                data += "{ hour: '" + hora + "h', a:" + total + ", b:" + ateses + "}";
	                data_dif += "{ hour: '" + hora + "h', a:" + (total-ateses) + "}";
	                data_dif_percent += "{ hour: '" + hora + "h', a:" + dif_percent + "}";
	            }
	            
	            data = "[" + data + "]";
	            data_dif = "[" + data_dif + "]";
	            data_dif_percent = "[" + data_dif_percent + "]";
	        } catch (Exception e ) {
	            e.printStackTrace(System.out);
	            System.out.println("ERROR");
	        }
		}
		
		return data + "&&" + data_dif + "&&" + data_dif_percent;
	}
	
	public static String getDadesGraphicsReport (String periode_datainici, String periode_datafi) {
		HashMap<Integer, Integer> hashAteses = new HashMap();
		String data = "";
		
		if (periode_datainici != null) {
			 try {
	        	Connection conn = Utils.createAsteriskConnection();
	            Statement stmt = conn.createStatement();
	            String sql = "select RIGHT(LEFT(dstchannel, 7), 3) as extensio, sum(IF(disposition = 'ANSWERED' && lastapp='Dial', 1, 0)) as ateses " + 
	            		"from asteriskcdrdb.cdr " + 
	            		"where dst=6000 and dcontext = 'ext-group' and " + 
	            		"	DATE(calldate) BETWEEN STR_TO_DATE('" + periode_datainici + "', '%Y-%m-%d') AND STR_TO_DATE('" + periode_datafi + "', '%Y-%m-%d') " + 
	            		"    and RIGHT(LEFT(dstchannel, 7), 3) IN (111,112,102,100,101,104) " + 
	            		"group by RIGHT(LEFT(dstchannel, 7), 3)";
	            ResultSet rs=stmt.executeQuery(sql);

	            while (rs.next()){
	            	int extensio = rs.getInt("extensio");
	                int ateses = rs.getInt("ateses");
	                hashAteses.put(extensio, ateses);
	            }
	            
	            stmt = conn.createStatement();
	            sql = "select src, SUM(IF(left(dst,1) = 0, 1, 0)) as externes,  SUM(IF(left(dst,1) = 0, 0, 1)) as internes, count(*) as total " + 
	            		"from asteriskcdrdb.cdr " + 
	            		"where src in (111,112,102,100,101,104) and " + 
	            		"	DATE(calldate) BETWEEN STR_TO_DATE('" + periode_datainici + "', '%Y-%m-%d') AND STR_TO_DATE('" + periode_datafi + "', '%Y-%m-%d') " + 
	            		"group by src order by src asc";
	            rs=stmt.executeQuery(sql);

	            while (rs.next()){
	            	if (data.compareTo("") != 0) {
	            		data += ", ";
	            	}
	            	
	            	int src = rs.getInt("src");
	                int internes = rs.getInt("internes");
	                int externes = rs.getInt("externes");
	                int total = rs.getInt("total");
	                
	                data += "{ label: 'ext " + src + "', value:" + (hashAteses.get(src) + internes + externes) + "}";
	            }
	            data = "[" + data + "]";
	        } catch (Exception e ) {
	            e.printStackTrace(System.out);
	            System.out.println("ERROR");
	        }
	        
		}
		return data;
	}
	
	public static String getPeriode (String periode_datainici, String periode_datafi) {
		String table_periode = "";
		
		if (periode_datainici.compareTo("") != 0 && periode_datafi.compareTo("") != 0) {
			table_periode = "<table width='100%' class='table table-striped table-bordered'>";
	        table_periode += "<thead>";
	        table_periode += "<tr>";
	        table_periode += "<th scope='col' id='hour'>Hora</th>";
	        table_periode += "<th scope='col' id='amount'>Contestades</th>";
	        table_periode += "<th scope='col' id='ratio'>No contestades</th>";
	        table_periode += "<th scope='col' id='ratio'>Total</th>";
	        table_periode += "<th scope='col' id='ratio'>% no ateses</th>";
	        table_periode += "</tr>";
	        table_periode += "</thead>";
	        table_periode += "<tbody>";
	        
	        try {
	            Connection conn = Utils.createAsteriskConnection();
	            Statement stmt = conn.createStatement();
	            String sql = "select LPAD(HOUR(calldate),2,'0') as hora, count(*) as total, sum(IF(disposition = 'ANSWERED' && lastapp='Dial', 1, 0)) as ateses "
	                    + "from asteriskcdrdb.cdr "
	                    + "where HOUR(calldate) BETWEEN 7 AND 22 and dst=6000 and dcontext = 'ext-group' and DATE(calldate) BETWEEN STR_TO_DATE('" + periode_datainici + "', '%Y-%m-%d') AND STR_TO_DATE('" + periode_datafi+ "', '%Y-%m-%d') "
	                    + "group by LPAD(HOUR(calldate),2,'0') "
	                    + "order by 1 asc";
	            ResultSet rs=stmt.executeQuery(sql);

	            while (rs.next()){
	                int total = rs.getInt("total");
	                int ateses = rs.getInt("ateses");
	                int no_ateses = total - ateses;
	                float percent_no_answer = 0;

	                if (total > 0) {
	                    percent_no_answer = 100f*((1f*no_ateses) / (1f*total));
	                }
	                String fpercent_no_answer = String.format("%.3g%n", percent_no_answer);
	                table_periode += "<tr>";
	                table_periode += "<td>" + rs.getString("hora")+ "h</td>";
	                table_periode += "<td>" + ateses + "</td>";
	                table_periode += "<td>" + no_ateses + "</td>";
	                table_periode += "<td>" + total + "</td>";
	                table_periode += "<td>" + fpercent_no_answer + " %</td>";
	                table_periode += "</tr>";
	            }
	            table_periode += "</tbody></table>";
	        } catch (Exception e ) {
	            e.printStackTrace(System.out);
	            System.out.println("ERROR");
	        }
		}
		
		return table_periode;
	}
	
	public static String getComparador (String periode1_datainici, String periode1_datafi, String periode2_datainici, String periode2_datafi) {
		HashMap<String, RowItem> periode_anterior = new HashMap();
	    
	    String table_periode1 = "";
	    String table_periode2 = "";
	    
	    if (periode1_datainici != null) {
	        table_periode1 = "<table width='100%' class='table table-striped table-bordered'>";
	        table_periode1 += "<thead>";
	        table_periode1 += "<tr>";
	        table_periode1 += "<th scope='col' id='hour'>Hora</th>";
	        table_periode1 += "<th scope='col' id='amount'>Contestades</th>";
	        table_periode1 += "<th scope='col' id='ratio'>No contestades</th>";
	        table_periode1 += "<th scope='col' id='ratio'>Total</th>";
	        table_periode1 += "<th scope='col' id='ratio'>% no ateses</th>";
	        table_periode1 += "</tr>";
	        table_periode1 += "</thead>";
	        table_periode1 += "<tbody>";

	        table_periode2 = "<table width='100%' class='table table-striped table-bordered'>";
	        table_periode2 += "<thead>";
	        table_periode2 += "<tr>";
	        table_periode2 += "<th scope='col' id='hour'>Hora</th>";
	        table_periode2 += "<th scope='col' id='amount'>Contestades</th>";
	        table_periode2 += "<th scope='col' id='ratio'>No contestades</th>";
	        table_periode2 += "<th scope='col' id='ratio'>Total</th>";
	        table_periode2 += "<th scope='col' id='ratio'>% no ateses</th>";
	        table_periode2 += "</tr>";
	        table_periode2 += "</thead>";
	        table_periode2 += "<tbody>";

	        try {
	            Connection conn = Utils.createAsteriskConnection();
	            Statement stmt = conn.createStatement();
	            String sql = "select LPAD(HOUR(calldate),2,'0') as hora, count(*) as total, sum(IF(disposition = 'ANSWERED' && lastapp='Dial', 1, 0)) as ateses "
	                    + "from asteriskcdrdb.cdr "
	                    + "where HOUR(calldate) BETWEEN 7 AND 22 and dst=6000 and dcontext = 'ext-group' and DATE(calldate) BETWEEN STR_TO_DATE('" + periode1_datainici + "', '%Y-%m-%d') AND STR_TO_DATE('" + periode1_datafi+ "', '%Y-%m-%d') "
	                    + "group by LPAD(HOUR(calldate),2,'0') "
	                    + "order by 1 asc";
	            ResultSet rs=stmt.executeQuery(sql);

	            while (rs.next()){
	                int total = rs.getInt("total");
	                int ateses = rs.getInt("ateses");
	                int no_ateses = total - ateses;
	                float percent_no_answer = 0;

	                if (total > 0) {
	                    percent_no_answer = 100f*((1f*no_ateses) / (1f*total));
	                }
	                String fpercent_no_answer = String.format("%.3g%n", percent_no_answer);
	                table_periode1 += "<tr>";
	                table_periode1 += "<td>" + rs.getString("hora")+ "h</td>";
	                table_periode1 += "<td>" + ateses + "</td>";
	                table_periode1 += "<td>" + no_ateses + "</td>";
	                table_periode1 += "<td>" + total + "</td>";
	                table_periode1 += "<td>" + fpercent_no_answer + " %</td>";
	                table_periode1 += "</tr>";

	                periode_anterior.put(rs.getString("hora"), new RowItem(rs.getString("hora"), total, ateses, no_ateses, percent_no_answer));
	            }
	            table_periode1 += "</tbody></table>";


	            sql = "select LPAD(HOUR(calldate),2,'0') as hora, count(*) as total, sum(IF(disposition = 'ANSWERED' && lastapp='Dial', 1, 0)) as ateses "
	                    + "from asteriskcdrdb.cdr "
	                    + "where HOUR(calldate) BETWEEN 7 AND 22 and dst=6000 and dcontext = 'ext-group' and DATE(calldate) BETWEEN STR_TO_DATE('" + periode2_datainici + "', '%Y-%m-%d') AND STR_TO_DATE('" + periode2_datafi+ "', '%Y-%m-%d') "
	                    + "group by LPAD(HOUR(calldate),2,'0') "
	                    + "order by 1 asc";
	            rs=stmt.executeQuery(sql);

	            while (rs.next()){
	                RowItem fila_periode_anterior = periode_anterior.get(rs.getString("hora"));
	                if (fila_periode_anterior == null) {
	                    fila_periode_anterior = new RowItem(rs.getString("hora"), 0,0,0,0f);
	                }
	                int total = rs.getInt("total");
	                int ateses = rs.getInt("ateses");
	                int no_ateses = total - ateses;
	                float percent_no_answer = 0;

	                if (total > 0) {
	                    percent_no_answer = 100f*((1f*no_ateses) / (1f*total));
	                }
	                String fontcomparativa  = "green";
	                String icon = "fa-arrow-up";
	                if (fila_periode_anterior.getPercentatgeNoAteses() < percent_no_answer) {
	                    fontcomparativa  = "red";
	                    icon = "fa-arrow-down";
	                }


	                String fpercent_no_answer = String.format("%.3g%n", percent_no_answer);
	                table_periode2 += "<tr>";
	                table_periode2 += "<td>" + rs.getString("hora")+ "h</td>";
	                table_periode2 += "<td>" + ateses + " ("+(ateses - fila_periode_anterior.getAteses())+")</td>";
	                table_periode2 += "<td>" + no_ateses + " ("+(no_ateses - fila_periode_anterior.getNoAteses()) +")</td>";
	                table_periode2 += "<td>" + total + " ("+(total - fila_periode_anterior.getTotal())+")</td>";
	                String sdif = String.format("%.3g%n", percent_no_answer-fila_periode_anterior.getPercentatgeNoAteses());
	                table_periode2 += "<td><font color='"+fontcomparativa+"'>" + fpercent_no_answer + " % ( <i class='fa "+icon+"'></i>"+sdif+"%</font>)</td>";
	                table_periode2 += "</tr>";
	            }
	            table_periode2 += "</tbody></table>";
	        } catch (Exception e ) {
	            e.printStackTrace(System.out);
	            System.out.println("ERROR");
	        }  
	    }
	    
	    return table_periode1 + "&&" + table_periode2;
	}
	
	public static String getReportPerGrup (String periode_datainici, String periode_datafi) {
		String table_periode = "";
		
		if (periode_datainici != null) {
			table_periode = "<table width='100%' class='table table-striped table-bordered' id='dataTables-grup'>";
	        table_periode += "<thead>";
	        table_periode += "<tr>";
	        table_periode += "<th scope='col' id='hour'>Grup</th>";
	        table_periode += "<th scope='col' id='amount'>Contestades</th>";
	        table_periode += "<th scope='col' id='ratio'>No contestades</th>";
	        table_periode += "<th scope='col' id='ratio'>Total</th>";
	        table_periode += "<th scope='col' id='ratio'>% no ateses</th>";
	        table_periode += "</tr>";
	        table_periode += "</thead>";
	        table_periode += "<tbody>";
	        
	        try {
	            Connection conn = Utils.createAsteriskConnection();
	            Statement stmt = conn.createStatement();
	            String sql = "select dst, count(*) as total, sum(IF(disposition = 'ANSWERED' && lastapp='Dial', 1, 0)) as ateses " + 
	            		"from asteriskcdrdb.cdr " + 
	            		"where dcontext IN ('ext-group', 'ext-queues') " + 
	            		"    and DATE(calldate) BETWEEN STR_TO_DATE('" + periode_datainici + "', '%Y-%m-%d') AND STR_TO_DATE('" + periode_datafi + "', '%Y-%m-%d') " + 
	            		"group by dst";
	            ResultSet rs=stmt.executeQuery(sql);

	            while (rs.next()){
	            	String dst = rs.getString("dst");
	                int total = rs.getInt("total");
	                int ateses = rs.getInt("ateses");
	                int no_ateses = total - ateses;
	                float percent_no_answer = 0;

	                if (total > 0) {
	                    percent_no_answer = 100f*((1f*no_ateses) / (1f*total));
	                }
	                String fpercent_no_answer = String.format("%.3g%n", percent_no_answer);
	                table_periode += "<tr>";
	                table_periode += "<td>" + dst + " / " + BIPhoneService.llegenda.get(dst) + "</td>";
	                table_periode += "<td>" + ateses + "</td>";
	                table_periode += "<td>" + no_ateses + "</td>";
	                table_periode += "<td>" + total + "</td>";
	                table_periode += "<td>" + fpercent_no_answer + " %</td>";
	                table_periode += "</tr>";
	            }
	            table_periode += "</tbody></table>";
	        } catch (Exception e ) {
	            e.printStackTrace(System.out);
	            System.out.println("ERROR");
	        }
	        
		}
		return table_periode;
	}
	
	
	public static String getReportPerExtensio (String periode_datainici, String periode_datafi) {
		HashMap<Integer, Integer> hashAteses = new HashMap();
		String table_periode = "";
		
		if (periode_datainici != null) {
			table_periode = "<table width='100%' class='table table-striped table-bordered' id='dataTables-extensio'>";
	        table_periode += "<thead>";
	        table_periode += "<tr>";
	        table_periode += "<th scope='col' id='hour'>Extensió</th>";
	        table_periode += "<th scope='col' id='amount'>Ateses</th>";
	        table_periode += "<th scope='col' id='ratio'>Fetes - internes</th>";
	        table_periode += "<th scope='col' id='ratio'>Fetes - externes</th>";
	        table_periode += "<th scope='col' id='ratio'>Total</th>";
	        table_periode += "</tr>";
	        table_periode += "</thead>";
	        table_periode += "<tbody>";
	        
	        try {
	        	Connection conn = Utils.createAsteriskConnection();
	            Statement stmt = conn.createStatement();
	            String sql = "select RIGHT(LEFT(dstchannel, 7), 3) as extensio, sum(IF(disposition = 'ANSWERED' && lastapp='Dial', 1, 0)) as ateses " + 
	            		"from asteriskcdrdb.cdr " + 
	            		"where dst=6000 and dcontext = 'ext-group' and " + 
	            		"	DATE(calldate) BETWEEN STR_TO_DATE('" + periode_datainici + "', '%Y-%m-%d') AND STR_TO_DATE('" + periode_datafi + "', '%Y-%m-%d') " + 
	            		"    and RIGHT(LEFT(dstchannel, 7), 3) IN (111,112,102,100,101,104) " + 
	            		"group by RIGHT(LEFT(dstchannel, 7), 3)";
	            ResultSet rs=stmt.executeQuery(sql);

	            while (rs.next()){
	            	int extensio = rs.getInt("extensio");
	                int ateses = rs.getInt("ateses");
	                hashAteses.put(extensio, ateses);
	            }
	            
	            stmt = conn.createStatement();
	            sql = "select src, SUM(IF(left(dst,1) = 0, 1, 0)) as externes,  SUM(IF(left(dst,1) = 0, 0, 1)) as internes, count(*) as total " + 
	            		"from asteriskcdrdb.cdr " + 
	            		"where src in (111,112,102,100,101,104) and " + 
	            		"	DATE(calldate) BETWEEN STR_TO_DATE('" + periode_datainici + "', '%Y-%m-%d') AND STR_TO_DATE('" + periode_datafi + "', '%Y-%m-%d') " + 
	            		"group by src order by src asc";
	            rs=stmt.executeQuery(sql);

	            while (rs.next()){
	            	int src = rs.getInt("src");
	                int internes = rs.getInt("internes");
	                int externes = rs.getInt("externes");
	                int total = rs.getInt("total");
	                
	                table_periode += "<tr>";
	                table_periode += "<td>" + src + "</td>";
	                table_periode += "<td>" + hashAteses.get(src) + "</td>";
	                table_periode += "<td>" + internes + "</td>";
	                table_periode += "<td>" + externes + "</td>";
	                table_periode += "<td>" + (internes + externes + hashAteses.get(src)) + "</td>";
	                table_periode += "</tr>";
	            }
	            table_periode += "</tbody></table>";
	        } catch (Exception e ) {
	            e.printStackTrace(System.out);
	            System.out.println("ERROR");
	        }
	        
		}
		return table_periode;
	}
	
	public static String getTrucades (String origen, String desti, String periode_datainici, String periode_datafi) {
		String table_trucades = "";
		
		if (periode_datainici != null) {
			table_trucades = "<table width='100%' class='table table-striped table-bordered' id='dataTables-trucades'>";
			table_trucades += "<thead>";
			table_trucades += "<tr>";
			table_trucades += "<th scope='col'>Dia i hora</th>";
			table_trucades += "<th scope='col'>Origen</th>";
			table_trucades += "<th scope='col'>Desti</th>";
			table_trucades += "<th scope='col'>Durada</th>";
			table_trucades += "<th scope='col'>Estado</th>";
			table_trucades += "</tr>";
			table_trucades += "</thead>";
			table_trucades += "<tbody>";
	        
	        try {
	        	Connection conn = Utils.createAsteriskConnection();
	            Statement stmt = conn.createStatement();
	            String sql = "select calldate, src, dst, duration, disposition " + 
	            		"from asteriskcdrdb.cdr where ";
	            boolean useAND = false;
	            if (origen.compareTo("") != 0) {
           			sql += "src = " + origen;
           			useAND = true;
           		}
	            if (desti.compareTo("") != 0) {
	            	if (useAND) {
	            		sql += " and ";
	            	}
           			sql += "dst = " + desti;
           			useAND = true;
           		}
	            if (periode_datainici.compareTo("") != 0) {
	            	if (useAND) {
	            		sql += " and ";
	            	}
           			sql += "DATE(calldate) >= STR_TO_DATE('" + periode_datainici + "', '%Y-%m-%d')";
           			useAND = true;
           		}
	            if (periode_datafi.compareTo("") != 0) {
	            	if (useAND) {
	            		sql += " and ";
	            	}
           			sql += "DATE(calldate) <= STR_TO_DATE('" + periode_datafi + "', '%Y-%m-%d')";
           			useAND = true;
           		}
	            
	            sql += " order by calldate desc LIMIT 100";
	            
	            ResultSet rs=stmt.executeQuery(sql);

	            while (rs.next()){
	            	String diaHora = rs.getString("calldate");
	                String src = rs.getString("src");
	                String dst = rs.getString("dst");
	                int duration = rs.getInt("duration");
	                String estado = rs.getString("disposition");
	                
	                table_trucades += "<tr>";
	                table_trucades += "<td>" + diaHora + "</td>";
	                table_trucades += "<td>" + src + "</td>";
	                table_trucades += "<td>" + dst + "</td>";
	                table_trucades += "<td>" + duration + "</td>";
	                table_trucades += "<td>" + estado + "</td>";
	                table_trucades += "</tr>";
	            }
	            table_trucades += "</tbody></table>";
	        } catch (Exception e ) {
	            e.printStackTrace(System.out);
	            System.out.println("ERROR");
	        }
	        
		}
		return table_trucades;
	}
}
