package com.example.demo.controller;

import com.example.demo.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WelcomeController {

  private Config config;

  @Autowired
  public WelcomeController(Config config) {
    this.config = config;
  }

  @ResponseBody
  @GetMapping(value = "/", produces = "text/plain")
  public String welcome() {
    return String.join(
        "\n",
        "Hello!",
        "Welcome to " + this.config.getWithDefault("APPLICATION_NAME", ""),
        "Description: " + this.config.getWithDefault("APPLICATION_DESCRIPTION", ""),
        "By: " + this.config.getWithDefault("APPLICATION_VENDOR_NAME", ""),
        "Version: " + this.config.getWithDefault("APPLICATION_VERSION_STRING", "0.0.0"),
        "Build: " + this.config.getWithDefault("APPLICATION_BUILD_DATE", "N.A.")
    );
  }
}
