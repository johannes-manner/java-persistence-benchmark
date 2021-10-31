package de.uniba.dsg.wss.data.model.ms;

import java.util.UUID;


public abstract class BaseData {

  private transient boolean writable;
  private String id;

  public BaseData() {
    writable = true;
    id = UUID.randomUUID().toString();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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
