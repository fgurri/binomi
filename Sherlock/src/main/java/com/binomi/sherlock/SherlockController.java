package com.binomi.sherlock;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SherlockController {
	
	@RequestMapping("/")
	public String home () {
		return "home";
	}
}
