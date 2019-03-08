package com.example.demo.controller;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WelcomeController {

  private Dotenv dotenv;

  @Autowired
  public WelcomeController(Dotenv dotenv) {
    this.dotenv = dotenv;
  }

  @ResponseBody
  @GetMapping(value = "/", produces = "text/plain")
  public String welcome() {
    return String.join(
        "\n",
        "Hello!",
        "Welcome to " + this.dotenv.get("APPLICATION_NAME", ""),
        "Description: " + this.dotenv.get("APPLICATION_DESCRIPTION", ""),
        "By: " + this.dotenv.get("APPLICATION_VENDOR_NAME", ""),
        "Version: " + this.dotenv.get("APPLICATION_VERSION_STRING", "0.0.0"),
        "Build: " + this.dotenv.get("APPLICATION_BUILD_DATE", "N.A.")
    );
  }
}
