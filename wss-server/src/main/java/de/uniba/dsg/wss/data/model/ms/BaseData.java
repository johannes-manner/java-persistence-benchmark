package de.uniba.dsg.wss.data.model.ms;

import java.util.UUID;


public abstract class BaseData {

  private String id;

  public BaseData() {
    id = UUID.randomUUID().toString();
  }

  public BaseData(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  @Override
  protected Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError("Could not clone " + this.getClass().getName());
    }
  }

}
