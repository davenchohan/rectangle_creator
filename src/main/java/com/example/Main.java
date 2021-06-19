/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

@Controller
@SpringBootApplication
public class Main {

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Autowired
  private DataSource dataSource;

  public static void main(String[] args) throws Exception {
    SpringApplication.run(Main.class, args);
  }

  @RequestMapping("/")
  String index(Map<String, Object> model) {
    String name = "Bobby";
    model.put("name", name);
    return "redirect:/rectangle/home";
  }

  @GetMapping(
    path = "/rectangle"
  )
  public String getRectangleForm(Map<String, Object> model){
    Rectangle rectangle = new Rectangle();  // creates new rectangle object with empty fname and lname
    model.put("rectangle", rectangle);
    return "rectangle";
  }

  @PostMapping(
    path = "/rectangle",
    consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
  )

  public String handleBrowserRectangleSubmit(Map<String, Object> model, Rectangle rectangle) throws Exception {
    // Save the rectangle data into the database
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS rectangle (id serial, name varchar(20), color varchar(20), width integer, height integer)");
      String sql = "INSERT INTO rectangle (name,color,width,height) VALUES ('" + rectangle.getName() + "','" + rectangle.getColour() + "','" + rectangle.getWidth() + "','" + rectangle.getHeight() + "')";
      stmt.executeUpdate(sql);

      System.out.println(rectangle.getName() + " " + rectangle.getColour() + " " + rectangle.getWidth() + " " + rectangle.getHeight());
      return "redirect:/rectangle/home";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }

  }

  @GetMapping("/rectangle/home")
  public String getRectangleSuccess(Map<String, Object> model){
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS rectangle (id serial, name varchar(20), color varchar(20), width integer, height integer)");
      ResultSet rs = stmt.executeQuery("SELECT * FROM rectangle");

      ArrayList<Rectangle> output = new ArrayList<Rectangle>();
      while (rs.next()) {
        Rectangle rectangle = new Rectangle();
        String name = rs.getString("name");
        String id = rs.getString("id");
        int width = rs.getInt("width");
        int height = rs.getInt("height");
        String colour = rs.getString("color");
        rectangle.setName(name);
        rectangle.setColour(colour);
        rectangle.setHeight(height);
        rectangle.setWidth(width);
        rectangle.setId(id);
        
        output.add(rectangle);
      }

      model.put("records", output);
      return "success";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping("/rectangle/read/{pid}")
  public String getRectangleInfo(Map<String, Object> model, @PathVariable String pid) {
    System.out.println(pid);
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM rectangle WHERE id="+ pid);

      ArrayList<Rectangle> output = new ArrayList<Rectangle>();
      while (rs.next()) {
        Rectangle rectangle = new Rectangle();
        String name = rs.getString("name");
        String id = rs.getString("id");
        int width = rs.getInt("width");
        int height = rs.getInt("height");
        String colour = rs.getString("color");
        rectangle.setName(name);
        rectangle.setColour(colour);
        rectangle.setHeight(height);
        rectangle.setWidth(width);
        rectangle.setId(id);
        
        output.add(rectangle);
      }

      model.put("records", output);
      return "rectangleinfo";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping("/rectangle/delete/{pid}")
    public String deleteRectangle(Map<String, Object> model, @PathVariable String pid){
      System.out.println(pid);
      try (Connection connection = dataSource.getConnection()) {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("DELETE FROM rectangle WHERE id="+ pid);
      } catch (Exception e) {
        model.put("message", e.getMessage());
        return "error";
      }
      return "redirect:/rectangle/home";
  }

  @GetMapping("/rectangle/deleteall")
  public String deleteRectangle(Map<String, Object> model){
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("DROP TABLE rectangle");
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
    return "redirect:/rectangle/home";
}

  @RequestMapping("/db")
  String db(Map<String, Object> model) {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
      stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
      ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");

      ArrayList<String> output = new ArrayList<String>();
      while (rs.next()) {
        output.add("Read from DB: " + rs.getTimestamp("tick"));
      }

      model.put("records", output);
      return "db";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @Bean
  public DataSource dataSource() throws SQLException {
    if (dbUrl == null || dbUrl.isEmpty()) {
      return new HikariDataSource();
    } else {
      HikariConfig config = new HikariConfig();
      config.setJdbcUrl(dbUrl);
      return new HikariDataSource(config);
    }
  }

}
