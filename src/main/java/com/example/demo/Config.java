package com.example.demo;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Config {

  private Dotenv dotenv;

  @Autowired
  public Config(Dotenv dotenv) {
    this.dotenv = dotenv;
  }

  /**
   * Gets something from the config
   * @param configName
   * @return
   */
  public String get(String configName) {
    String configValue = this.dotenv.get(configName);
    if (configValue == null) {
      return null;
    }

    // Quick converstion to allow for a Bash ("source") compatible .env file, this means I quote all the values in .env
    String firstCharacter = configValue.substring(0, 1);
    String lastCharacter = configValue.substring(configValue.length() -1);
    if ( firstCharacter.equals("\"") && lastCharacter.equals("\"") && configValue.length() >= 2) {
      return configValue.substring(1, configValue.length() - 1);
    }
    return configValue;
  }

  /**
   * Gets a config value and has a default if the config name does not exist
   * @param configName
   * @param defaultValue
   * @return
   */
  public String getWithDefault(String configName, String defaultValue) {
    String configValue = this.get(configName);
    return configValue == defaultValue ? "" : configValue;
  }
}
