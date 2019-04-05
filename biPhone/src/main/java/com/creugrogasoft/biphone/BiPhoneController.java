package com.creugrogasoft.biphone;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;

import javax.naming.*;
import javax.naming.directory.*;
import javax.naming.ldap.LdapContext;

import java.util.Hashtable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import binomi.windows.*;

import com.creugrogasoft.biphone.utils.*;

@Controller
@Scope("request")
public class BiPhoneController {
	
	@Autowired
	private User user;
	
	@Autowired
	private Environment  env;
	

	@RequestMapping("/")
    public String index() throws NamingException {
		
		if (!user.isLogged())
			return "login";
		
		return "home";
    }
	
	@RequestMapping("/llegenda")
    public String llegenda(ModelMap model) {
		
		model.addAttribute("table_grups", BIPhoneService.getLlegendaGrups());
		model.addAttribute("table_extensions", BIPhoneService.getLlegendaExtensions());
		
		return "llegenda";
    }
	
	@RequestMapping("/login")
    public String login(@RequestParam (value = "username", required = true) String username,
    		@RequestParam (value = "password", required = true) String password) throws NamingException {
		
		try{
			LdapContext ctx = ActiveDirectory.getConnection(username, password);
			user = ActiveDirectory.getUser(username, ctx);
		    ctx.close();
		    System.out.println ("Username " + user.getUserPrincipal() + " logged!");
		    System.out.println ("Groups:" + user.getMemberOf());
		    // user logged ok, check if app is userRestricted
		    if (env.getProperty("userRestricted").compareTo("1") == 0) {
		    	if (env.getProperty("usersAllowed").contains(user.getUserPrincipal())) {
		    		System.out.println("User allowed!");
		    	} else {
		    		user = null;
		    		System.out.println(":( User NOT allowed! Get out madafaka!!");
		    	}
		    }
		    return "home";
		}
		catch(Exception e) {
		    //Failed to authenticate user!
		    user = null;
		}
		
		return "login";
	}
	
	@RequestMapping("/dashboard")
    public String dashboard(ModelMap model) {
		
		model.addAttribute("table_today", BIPhoneService.getAvui());
		model.addAttribute("table_fa1setmana", BIPhoneService.getFa1Setmana());
		model.addAttribute("table_noanswer", BIPhoneService.getNoAteses());
		model.addAttribute("table_answered", BIPhoneService.getAteses());
		model.addAttribute("table_llegenda", BIPhoneService.getLlegendaGrups());
		
		return "dashboard";
	}
	
	@RequestMapping("/graphics_filter")
    public String graphics_filter(ModelMap model) {

		/*
		 * Last month
		 */
		
		YearMonth thisMonth    = YearMonth.now();
		YearMonth lastMonth    = thisMonth.minusMonths(1);

		DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
		DateTimeFormatter dayMonthYearFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		String periode_datainici = lastMonth.format(monthYearFormatter)+"-01";
		String periode_datafi = lastMonth.atEndOfMonth().format(dayMonthYearFormatter);

		model.addAttribute("lastmonth_periode_datainici", periode_datainici);
		model.addAttribute("lastmonth_periode_datafi", periode_datafi);
		
		/*
		 * Last week
		 */
		
		LocalDate now = LocalDate.now();

		// determine country (Locale) specific first day of current week
		DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
		LocalDate startOfCurrentWeek = now.with(TemporalAdjusters.previousOrSame(firstDayOfWeek));
		
		LocalDate startOfPastWeek = startOfCurrentWeek.minusDays(7);
		LocalDate endOfPastWeek = startOfPastWeek.plusDays(6);
		
		model.addAttribute("lastweek_periode_datainici", startOfPastWeek);
		model.addAttribute("lastweek_periode_datafi", endOfPastWeek);
		
		/*
		 * Today and Yesterday
		 */
		
		model.addAttribute("avui", now);
		model.addAttribute("ahir", now.plusDays(-1));
				
		return "graphics_filter";
	}
	
	@RequestMapping("/graphics")
    public String graphics(ModelMap model, 
    		@RequestParam (value = "periode_datainici", required = false, defaultValue = "") String periode_datainici,
    		@RequestParam (value = "periode_datafi", required = false, defaultValue = "") String periode_datafi) {
		
		if (	periode_datainici.compareTo("") != 0 &&
				periode_datafi.compareTo("") != 0 ) {
			String rawdata = BIPhoneService.getDadesGraphics(periode_datainici, periode_datafi);
			
			model.addAttribute("periode_datainici", periode_datainici);
			model.addAttribute("periode_datafi", periode_datafi);
			
			String data = rawdata.split("&&")[0];
			String data_dif = rawdata.split("&&")[1];
			String data_dif_percent = rawdata.split("&&")[2];
			
			model.addAttribute("data", data);
			model.addAttribute("data_dif", data_dif);
			model.addAttribute("data_dif_percent", data_dif_percent);
		} else {
			return "redirect:/graphics_filter";
		}
		
		return "graphics";
	}
	
	@RequestMapping("/compare")
    public String compare(ModelMap model, 
    		@RequestParam (value = "periode1_datainici", required = false, defaultValue = "") String periode1_datainici,
    		@RequestParam (value = "periode1_datafi", required = false, defaultValue = "") String periode1_datafi,
    		@RequestParam (value = "periode2_datainici", required = false, defaultValue = "") String periode2_datainici,
    		@RequestParam (value = "periode2_datafi", required = false, defaultValue = "") String periode2_datafi) {

		if (	periode1_datainici.compareTo("") != 0 &&
				periode1_datafi.compareTo("") != 0 &&
				periode2_datainici.compareTo("") != 0 &&
				periode2_datafi.compareTo("") != 0) {
			String tables = BIPhoneService.getComparador(periode1_datainici, periode1_datafi, periode2_datainici, periode2_datafi);
			
			String table_periode1 = tables.split("&&")[0];
			String table_periode2 = tables.split("&&")[1];
			
			model.addAttribute("periode1_datainici", periode1_datainici);
			model.addAttribute("periode1_datafi", periode1_datafi);
			model.addAttribute("periode2_datainici", periode2_datainici);
			model.addAttribute("periode2_datafi", periode2_datafi);
			
			model.addAttribute("table_periode1", table_periode1);
			model.addAttribute("table_periode2", table_periode2);
		} else {
			return "redirect:/compare_filter";
		}
		
		return "compare";
	}
	
	
	@RequestMapping("/compare_filter")
    public String compare_filter(ModelMap model) {
		
		/*
		 * Last 2 months
		 */
		YearMonth thisMonth    = YearMonth.now();
		YearMonth lastMonth    = thisMonth.minusMonths(1);
		YearMonth twoMonthsAgo = thisMonth.minusMonths(2);

		DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
		DateTimeFormatter dayMonthYearFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		String periode2_datainici = lastMonth.format(monthYearFormatter)+"-01";
		String periode2_datafi = lastMonth.atEndOfMonth().format(dayMonthYearFormatter);
		
		String periode1_datainici = twoMonthsAgo.format(monthYearFormatter)+"-01";
		String periode1_datafi = twoMonthsAgo.atEndOfMonth().format(dayMonthYearFormatter);
		
		model.addAttribute("twomonths_periode1_datainici", periode1_datainici);
		model.addAttribute("twomonths_periode1_datafi", periode1_datafi);
		model.addAttribute("twomonths_periode2_datainici", periode2_datainici);
		model.addAttribute("twomonths_periode2_datafi", periode2_datafi);
		
		/*
		 * Last two weeks
		 */
		
		LocalDate now = LocalDate.now();

		// determine country (Locale) specific first day of current week
		DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
		LocalDate startOfCurrentWeek = now.with(TemporalAdjusters.previousOrSame(firstDayOfWeek));
		
		LocalDate startOfPastWeek = startOfCurrentWeek.minusDays(7);
		LocalDate endOfPastWeek = startOfPastWeek.plusDays(6);
		
		LocalDate startOfTwoWeeksAgo = startOfCurrentWeek.minusDays(14);
		LocalDate endOfTwoWeeksAgo = startOfTwoWeeksAgo.plusDays(6);
				
		model.addAttribute("twoweeks_periode1_datainici", startOfTwoWeeksAgo);
		model.addAttribute("twoweeks_periode1_datafi", endOfTwoWeeksAgo);
		model.addAttribute("twoweeks_periode2_datainici", startOfPastWeek);
		model.addAttribute("twoweeks_periode2_datafi", endOfPastWeek);
		
		return "compare_filter";
	}
	
	@RequestMapping("/report_filter")
    public String report_filter(ModelMap model) {

		/*
		 * Last month
		 */
		YearMonth thisMonth    = YearMonth.now();
		YearMonth lastMonth    = thisMonth.minusMonths(1);

		DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
		DateTimeFormatter dayMonthYearFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		String periode_datainici = lastMonth.format(monthYearFormatter)+"-01";
		String periode_datafi = lastMonth.atEndOfMonth().format(dayMonthYearFormatter);

		model.addAttribute("lastmonth_periode_datainici", periode_datainici);
		model.addAttribute("lastmonth_periode_datafi", periode_datafi);
		
		/*
		 * Last week
		 */
		
		LocalDate now = LocalDate.now();

		// determine country (Locale) specific first day of current week
		DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
		LocalDate startOfCurrentWeek = now.with(TemporalAdjusters.previousOrSame(firstDayOfWeek));
		
		LocalDate startOfPastWeek = startOfCurrentWeek.minusDays(7);
		LocalDate endOfPastWeek = startOfPastWeek.plusDays(6);
		
		model.addAttribute("lastweek_periode_datainici", startOfPastWeek);
		model.addAttribute("lastweek_periode_datafi", endOfPastWeek);
		
		/*
		 * Today and Yesterday
		 */
		
		model.addAttribute("avui", now);
		model.addAttribute("ahir", now.plusDays(-1));
		
				
		return "report_filter";
	}
	
	@RequestMapping("/calls_filter") 	
    public String calls_filter(ModelMap model) {
		
		return "calls_filter";
	}
	
	@RequestMapping("/calls")
    public String calls(ModelMap model, 
    		@RequestParam (value = "origen", required = false, defaultValue = "") String origen,
    		@RequestParam (value = "desti", required = false, defaultValue = "") String desti,
    		@RequestParam (value = "periode_datainici", required = false, defaultValue = "") String periode_datainici,
    		@RequestParam (value = "periode_datafi", required = false, defaultValue = "") String periode_datafi) {

		if (	origen.compareTo("") != 0
				|| desti.compareTo("") != 0
				|| periode_datainici.compareTo("") != 0 
				|| periode_datafi.compareTo("") != 0 ) {
			String table_trucades = BIPhoneService.getTrucades(origen, desti, periode_datainici, periode_datafi);
			
			model.addAttribute("origen", origen);
			model.addAttribute("desti", desti);
			model.addAttribute("periode_datainici", periode_datainici);
			model.addAttribute("periode_datafi", periode_datafi);
			
			model.addAttribute("table_trucades", table_trucades);
		} else {
			model.addAttribute("message", "Ha de omplir almenys un camp");
			return "calls_filter";
		}
		
		return "calls";
	}
	
	@RequestMapping("/report")
    public String compare(ModelMap model, 
    		@RequestParam (value = "periode_datainici", required = false, defaultValue = "") String periode_datainici,
    		@RequestParam (value = "periode_datafi", required = false, defaultValue = "") String periode_datafi) {

		if (	periode_datainici.compareTo("") != 0 &&
				periode_datafi.compareTo("") != 0 ) {
			String table_pergrup = BIPhoneService.getReportPerGrup(periode_datainici, periode_datafi);
			String table_perextensio = BIPhoneService.getReportPerExtensio(periode_datainici, periode_datafi);
			String table_perfranjahoraria = BIPhoneService.getPeriode(periode_datainici, periode_datafi);
			String data_atesesperextensio = BIPhoneService.getDadesGraphicsReport(periode_datainici, periode_datafi);
			
			model.addAttribute("periode_datainici", periode_datainici);
			model.addAttribute("periode_datafi", periode_datafi);
			model.addAttribute("data_atesesperextensio", data_atesesperextensio);
			
			model.addAttribute("table_pergrup", table_pergrup);
			model.addAttribute("table_perextensio", table_perextensio);
			model.addAttribute("table_perfranjahoraria", table_perfranjahoraria);
		} else {
			return "redirect:/report_filter";
		}
		
		return "report";
	}
}
