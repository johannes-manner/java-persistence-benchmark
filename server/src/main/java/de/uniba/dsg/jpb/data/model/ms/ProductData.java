package de.uniba.dsg.jpb.data.model.ms;

import de.uniba.dsg.jpb.util.Identifiable;

public class ProductData implements Identifiable<Long> {

  private Long id;
  private String imagePath;
  private String name;
  private double price;
  private String data;

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  public String getImagePath() {
    return imagePath;
  }

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }
}
