package service;

public class IdGenerator {
   private long currentId = 1;

   public long nextId() {
       return currentId++;
   }

}
